package de.DiscordBot.Commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

import de.DiscordBot.DiscordBot;
import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import de.DiscordBot.Config.ConfigurableOption;
import javautils.mysql.MySQLConfiguration;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class ReminderCommand extends DiscordCommand {

	private Thread reminder = null;

	public ReminderCommand() {
		super("remind", new String[] { "writeMeAtSomePoint", "dontForget", "secureRemind" },
				"Reminds you (or someone else) of sth at a later point in time!",
				"\\remind {@User} [time (ex. 4m/2h/3D/1W/4M/2Y)] [message]");
		if (reminder == null) {
			reminder = new Thread(new Runnable() {

				@Override
				public void run() {
					MySQLConfiguration co = DiscordBot.mysql;
					Connection c = DiscordBot.mysql.getConnection();
					PreparedStatement ps = null;
					try {
						ps = c.prepareStatement("SELECT OPTIONGUILD FROM remind WHERE timestamp(VALUE) <= NOW()");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					while (true) {
						ResultSet rs = null;
						try {
							rs = ps.executeQuery();
							if (rs.first()) {
								rs.beforeFirst();
								while (rs.next()) {
									String option = rs.getString("OPTIONGUILD");
									String message = co.get("remind", "VALUE", "OPTIONGUILD", "message" + option)
											.getString("VALUE");
									String user = co.get("remind", "VALUE", "OPTIONGUILD", "user" + option)
											.getString("VALUE");
									String chan = co.get("remind", "VALUE", "OPTIONGUILD", "channel" + option)
											.getString("VALUE");
									boolean secure = co.get("remind", "VALUE", "OPTIONGUILD", "secure" + option)
											.getString("VALUE").equals("true");
									JDA jda = DiscordBot.getBot();
									User target = jda.getUserById(Long.valueOf(user));
									MessageBuilder mb = new MessageBuilder();
									mb.append(target);
									mb.append("\nReminder for: " + message);
									if (!secure) {
										TextChannel tc = jda.getTextChannelById(Long.valueOf(chan));
										tc.sendMessage(mb.build()).submit();
									}else {
										final Message send = mb.build();
										target.openPrivateChannel().queue((channel) -> {
											channel.sendMessage(send).submit();
										});
									}
								}
							}
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							Thread.sleep(10000L);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			});
			reminder.start();
		}
	}

	@Override
	public Object execute(String command, String[] args, Message m) {
		boolean secure = false;
		if (command.equalsIgnoreCase("secureremind")) {
			secure = true;
		}
		User target = null;
		Calendar cal = Calendar.getInstance();
		String t = "";
		String mes = "";
		if (m.getMentionedUsers().isEmpty()) {
			target = m.getAuthor();
			t = args[0];
			for (int i = 1; i < args.length; i++) {
				mes += " " + args[i];
			}
			mes = mes.substring(1);
		} else {
			target = m.getMentionedUsers().get(0);
			t = args[1];
			for (int i = 2; i < args.length; i++) {
				mes += " " + args[i];
			}
			mes = mes.substring(1);
		}
		int amount = Integer.valueOf(t.substring(0, t.length() - 2));
		switch (t.charAt(t.length() - 1)) {
		case 'S':
		case 's':
			cal.add(Calendar.SECOND, amount);
			break;
		case 'm':
			cal.add(Calendar.MINUTE, amount);
			break;
		case 'h':
		case 'H':
			cal.add(Calendar.HOUR_OF_DAY, amount);
			break;
		case 'd':
		case 'D':
			cal.add(Calendar.DATE, amount);
			break;
		case 'w':
		case 'W':
			cal.add(Calendar.WEEK_OF_YEAR, amount);
			break;
		case 'M':
			cal.add(Calendar.MONTH, amount);
			break;
		case 'Y':
		case 'y':
			cal.add(Calendar.YEAR, amount);
			break;
		default:
			return "The time descriptor: '" + t.charAt(t.length() - 1) + "' isn't recognized!";
		}
		Config g = getConfig(m.getGuild());
		Timestamp when = new Timestamp(cal.getTimeInMillis());
		String id = UUID.randomUUID().toString();
		g.setValue(id, when.toString());
		g.setValue("message" + id, mes);
		g.setValue("secure" + id, secure);
		g.setValue("user" + id, target.getIdLong() + "");
		g.setValue("channel" + id, m.getTextChannel().getIdLong() + "");
		if (secure) {
			m.delete().submit();
			return "You will be reminded securely, when it's time :D";
		} else {
			return "You will be reminded here, when it's time :D";
		}
	}

	@Override
	public void setupCommandConfig(Guild g, Config cfg) {
		// TODO Auto-generated method stub

	}

	@Override
	public ConfigPage createRemoteConfigurable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRemoteConfigurable() {
		// TODO Auto-generated method stub
		return false;
	}

}
