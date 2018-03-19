package de.DiscordBot.Commands;

import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

public class LotusCommand extends DiscordCommand {

	public LotusCommand() {
		super("lotus", new String[] {}, "Help Command for Lotus :joy:", "\\lotus");
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(String command, String[] args, Message m) {
		MessageBuilder mb = new MessageBuilder();
		mb.append("~help");
		return mb.build();
	}

	@Override
	public void setupCommandConfig(Guild g, Config cfg) {
		
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
