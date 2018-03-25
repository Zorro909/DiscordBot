package de.DiscordBot;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

import de.DiscordBot.ChatLog.ChatLog;
import de.DiscordBot.Commands.DiscordCommand;
import de.DiscordBot.Config.RemoteConfigurator;
import de.DiscordBot.Game.DiscordGame;
import javautils.mysql.DATA_TYPE;
import javautils.mysql.MySQLConfiguration;
import javautils.mysql.Table;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandExecutor implements Runnable {

	MessageReceivedEvent event;
	private static ChatLog cl;
	static HashMap<String, Guild> guilds = new HashMap<String, Guild>();
	static HashMap<String, MessageChannel> channel = new HashMap<String, MessageChannel>();
	
	static HashMap<String, DiscordGame> gameList = new HashMap<String, DiscordGame>();
	public static HashMap<String, DiscordGame> gameChannels = new HashMap<String, DiscordGame>();
	
	static HashMap<String, DiscordCommand> commands = new HashMap<String, DiscordCommand>();
	static LinkedList<DiscordCommand> cList = new LinkedList<DiscordCommand>();
	static Message help;

	private static volatile HashMap<Long, MessageChannel> sendTypingChannels = new HashMap<Long, MessageChannel>();

	private static boolean setUp = false;

	private static RemoteConfigurator rc;

	public CommandExecutor(MessageReceivedEvent event) {
		this.event = event;

		if (!setUp) {
			setUpMySQL();
			generateHelp();
			setUp = true;
			new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						for (MessageChannel c : sendTypingChannels.values()) {
							c.sendTyping().submit();
						}
						try {
							Thread.sleep(2500L);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}).start();
		}

		// rc = new RemoteConfigurator();
	}

	private void setUpMySQL() {
		Table games = new Table("games");
		games.addColumn("NAME", DATA_TYPE.VARCHAR, true, false, true, true);
		games.addColumn("Channels", DATA_TYPE.LARGETEXT, true, false, false, false);
		DiscordBot.mysql.createTable(games);
	}

	private void generateHelp() {
		MessageBuilder msb = new MessageBuilder();
		msb.append("Help for LewdBot:\n");
		for (DiscordCommand cmd : cList) {
			msb.append("  \\" + cmd.getCommandName() + ": " + cmd.getDescription() + "\n   " + cmd.getUsage() + "\n");
			if (cmd.getCommandAliases().length > 0) {
				msb.append("  Aliases: ");
				int aliases = 0;
				for (String s : cmd.getCommandAliases()) {
					if (aliases == 3) {
						break;
					}
					msb.append("\\" + s + " ");
					aliases++;
				}
				msb.append("\n");
			}
		}
		help = msb.build();
	}

	static URLClassLoader loader;
	
	public static void refresh() {
		commands.clear();
		cList.clear();
		gameChannels.clear();
		gameList.clear();
		if(loader!=null) {
			try {
				loader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			loader = null;
		}
		System.gc();
		ArrayList<URL> urls = new ArrayList<URL>();
		if(new File("plugins").exists()) {
			for(File f : new File("plugins").listFiles()) {
				if(f.getPath().endsWith(".jar")) {
					try {
						urls.add(f.toURL());
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else {
			new File("plugins").mkdir();
		}
		loader = new URLClassLoader(urls.toArray(new URL[] {}));
		ServiceLoader<DiscordCommand> commandService = ServiceLoader.load(DiscordCommand.class, loader);
		Iterator<DiscordCommand> i = commandService.iterator();
		while(i.hasNext()) {
			addCommand(i.next());
		}
		System.out.println("Loaded " + cList.size() + " Commands");
		ServiceLoader<DiscordGame> gameService = ServiceLoader.load(DiscordGame.class, loader);
		Iterator<DiscordGame> g = gameService.iterator();
		while(g.hasNext()) {
			addGame(g.next());
		}
		System.out.println("Loaded " + gameList.size() + " Games");
	}

	private static void addGame(DiscordGame game) {
		String name = game.getName();
		gameList.put(name, game);
		try {
			ResultSet rs = DiscordBot.mysql.get("games", "CHANNELS", "NAME", name.toLowerCase());
			
			if(rs.first()) {
				JDA j = DiscordBot.getBot();
				String ch = rs.getString("CHANNELS");
				for(String s : ch.split(";")) {
					gameChannels.put(s, game);
					game.loadGameConfig(j.getTextChannelById(s).getGuild(), j.getTextChannelById(s), DiscordBot.mysql);
				}
			}
		} catch (SQLException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void addCommand(DiscordCommand cmd) {
		commands.put(cmd.getCommandName().toLowerCase(), cmd);
		for (String s : cmd.getCommandAliases()) {
			commands.put(s.toLowerCase(), cmd);
		}
		cList.add(cmd);

		Table t = new Table(cmd.getCommandName().toLowerCase());
		t.addColumn("OPTIONGUILD", DATA_TYPE.VARCHAR, true, false, true, false);
		t.addColumn("GUILD", DATA_TYPE.VARCHAR, true, false, false, false);
		t.addColumn("VALUE", DATA_TYPE.MEDIUMTEXT, true, false, false, false);

		MySQLConfiguration conf = DiscordBot.mysql;
		conf.createTable(t);

		List<Guild> guilds = DiscordBot.getBot().getGuilds();
		for (Guild g : guilds) {
			try {
				cmd.sqlConfigured.add(g.getIdLong());
				if (!conf.get(cmd.getCommandName().toLowerCase(), "GUILD", "GUILD", "" + g.getIdLong()).first()) {
					cmd.setupCommandConfig(g, cmd.getConfig(g));
				}
			} catch (SQLException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		if (event.isFromType(ChannelType.PRIVATE)) {
			Message incoming = event.getMessage();
			if (incoming.getContent().startsWith("\\")) {
				String command = incoming.getContent().substring(1);
				String[] args = command.split(" ");
				switch (args[0].toLowerCase()) {
				case "setguild":
					try {
						guilds.put(incoming.getAuthor().getName(),
								event.getJDA().getGuildsByName(command.split(" ", 2)[1], true).get(0));
						event.getPrivateChannel().sendMessage(
								"Your current Guild was set to " + guilds.get(incoming.getAuthor().getName()).getName())
								.submit();
					} catch (Exception e) {
						event.getPrivateChannel()
								.sendMessage("Sorry the guild " + command.split(" ", 2)[1] + " could not be found!")
								.submit();
					}
					break;
				case "setchannel":
					try {
						channel.put(incoming.getAuthor().getName(), guilds.get(incoming.getAuthor().getName())
								.getTextChannelsByName(command.split(" ", 2)[1], true).get(0));
						event.getPrivateChannel().sendMessage("Your current Channel was set to "
								+ channel.get(incoming.getAuthor().getName()).getName()).submit();
					} catch (Exception e) {
						event.getPrivateChannel().sendMessage("Sorry an exception was raised").submit();
					}
					break;
				case "sendmsg":
					try {
						channel.get(incoming.getAuthor().getName()).sendMessage(command.split(" ", 2)[1]).submit();
						event.getPrivateChannel().sendMessage("Your MSG was sent!").submit();
					} catch (Exception e) {
						event.getPrivateChannel()
								.sendMessage("Sorry the channel " + command.split(" ", 2)[1] + " could not be found!")
								.submit();
					}
				}
			}
		} else if (event.isFromType(ChannelType.TEXT)) {
			Message incoming = event.getMessage();
			if(gameChannels.containsKey(incoming.getTextChannel().getId())) {
				gameChannels.get(incoming.getTextChannel().getId()).receiveMessage(incoming, DiscordBot.mysql);
				return;
			}
			if (incoming.getContent().startsWith("\\")) {
				String command = incoming.getContent().split(" ", 2)[0].substring(1);
				String[] args = new String[0];
				if (incoming.getContent().length() > command.length() + 1) {
					args = incoming.getContent().substring(2 + command.length()).split(" ");
				}
				if (command.equalsIgnoreCase("help")) {
					event.getChannel().sendMessage(help).submit();
				} else {
					if (commands.containsKey(command.toLowerCase())) {
						long time = System.currentTimeMillis();
						sendTypingChannels.put(time, incoming.getChannel());
						DiscordCommand dc = commands.get(command.toLowerCase());
						Object ret = null;
						try {
							ret = dc.execute(command, args, incoming);
						} catch (Exception e) {
							incoming.getChannel().sendMessage(
									"Your Command generated an Error, please contact the Developer! @Zorro909#1972")
									.submit();
							e.printStackTrace();
							return;
						}
						if (ret instanceof Integer) {
							incoming.getChannel().sendMessage("Your Command generated an Error with the Code " + ret
									+ ", please contact the Developer! @Zorro909#1972");
						} else if (ret instanceof Message) {
							incoming.getChannel().sendMessage(((Message) (ret))).submit();
						} else if (ret instanceof MessageEmbed) {
							incoming.getChannel().sendMessage((MessageEmbed) ret).submit();
						}
						sendTypingChannels.remove(time);
					}
				}
			}
		}
	}

	public static String join(String[] args, int i) {
		String s = "";
		for (int l = i; l < args.length; l++) {
			s += " " + args[l];
		}
		if (!s.isEmpty()) {
			s = s.substring(1);
		}
		return s;
	}

	public static ChatLog getChatLog() {
		return cl;
	}

	public static void setChatLog(ChatLog cl) {
		CommandExecutor.cl = cl;
	}

}
