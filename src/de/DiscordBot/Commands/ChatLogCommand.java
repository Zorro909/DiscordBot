package de.DiscordBot.Commands;

import java.util.HashMap;

import de.DiscordBot.CommandExecutor;
import de.DiscordBot.ChatLog.ChatLog;
import de.DiscordBot.ChatLog.ChatLogChannel;
import de.DiscordBot.ChatLog.ChatLogMessage;
import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public class ChatLogCommand extends DiscordCommand {

	public ChatLogCommand() {
		super("chatlog", new String[] {"clog"}, "You can view stats for the logged ChatLog of the Bot here", "\\chatlog [stats|loadChannel]");
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(String command, String[] args, Message m) {
		if(args.length==0) {
			return new MessageBuilder().append("Usage: " + getUsage());
		}
		if(args[0].equalsIgnoreCase("stats")) {
			ChatLog cl =  CommandExecutor.getChatLog();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Chatlog Stats");
			eb.addField(new Field("Total Messages captured", cl.countMessages(m.getGuild()) + "", true));
			HashMap<String, Integer> encounter = new HashMap<String, Integer>();
			long most = 0;
			String me = "";
			for (ChatLogChannel clc : cl.listChannels(m.getGuild()).values()) {
				for(ChatLogMessage clm : clc.clm) {
					if(encounter.containsKey(clm.content)) {
						encounter.put(clm.content, encounter.get(clm.content)+1);
						if(most<encounter.get(clm.content)) {
							most = encounter.get(clm.content);
							me = clm.content;
						}
					}else {
						encounter.put(clm.content, 1);
					}
				}
			}
			eb.addField(new Field("Most sent Message (" + most + "):", me + "", true));		
			return eb.build();
		}
		
		return null;
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
