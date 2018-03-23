package de.DiscordBot.Commands;

import de.DiscordBot.CommandExecutor;
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
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Chatlog Stats");
			eb.addField(new Field("Messages in RAM", CommandExecutor.getChatLog().countMessages() + "", true));
			return eb;
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
