package de.DiscordBot.ChatLog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.DiscordBot.DiscordBot;
import net.dv8tion.jda.core.entities.Guild;

public class ChatLog {

	private HashMap<String, HashMap<String, ChatLogChannel>> guildChannels = new HashMap<>();
	File logFolder;

	public ChatLog(File logFolder) {
		this.logFolder = logFolder;
		if (!logFolder.exists()) {
			logFolder.mkdirs();
		} else {
			for (File guild : logFolder.listFiles()) {
				if (guild.isDirectory()) {
					HashMap<String, ChatLogChannel> chan = new HashMap<String, ChatLogChannel>();
					for (File channel : guild.listFiles()) {
						String chaName = channel.getName().substring(0, channel.getName().lastIndexOf("."));
						chan.put(chaName, new ChatLogChannel(guild.getName(), chaName, channel));
					}
					guildChannels.put(guild.getName(), chan);
				} else {
					System.out.println("What does the File " + guild.getName() + " do in the ChatLog Folder?");
				}
			}
		}
		new ChatLogInterface(this, DiscordBot.getBot());
	}

	public ChatLogChannel getChannel(Guild guild, String name) {
		HashMap<String, ChatLogChannel> chan = new HashMap<String, ChatLogChannel>();
		if (guildChannels.containsKey(guild.getName())) {
			chan = guildChannels.get(guild.getName());
		}
		if (chan.containsKey(name)) {
			return chan.get(name);
		} else {
			new File(logFolder.getAbsolutePath() + "/" + guild.getName()).mkdirs();
			ChatLogChannel clc = new ChatLogChannel(guild.getName(), name,
					new File(logFolder.getAbsolutePath() + "/" + guild.getName(), name + ".channel"));
			chan.put(name, clc);
			guildChannels.put(guild.getName(), chan);
			return clc;
		}
	}

	public HashMap<String, ChatLogChannel> listChannels(Guild guild) {
		HashMap<String, ChatLogChannel> ch = new HashMap<>();
		if (!new File(logFolder.getAbsolutePath() + "/" + guild.getName()).exists()) {
			return ch;
		} else {
			for (File channel : new File(logFolder.getAbsolutePath() + "/" + guild.getName()).listFiles()) {
				if (channel.getName().endsWith(".channel")) {
					ch.put(channel.getName().split(".channe")[0],
							getChannel(guild, channel.getName().split(".channe")[0]));
				}
			}
		}

		return ch;
	}

	public long countMessages(Guild g) {
		long count = 0;
		for (ChatLogChannel clc : listChannels(g).values()) {
			clc.load();
			count += clc.clm.size();
		}
		return count;
	}
	
	long countMessages(String guildName) {
		long count = 0;
		for (String s : listChannels(guildName)) {
			ChatLogChannel clc = getChannel(guildName, s);
			clc.load();
			count += clc.clm.size();
		}
		return count;
	}

	ChatLogChannel getChannel(String guildName, String name) {
		HashMap<String, ChatLogChannel> chan = new HashMap<String, ChatLogChannel>();
		if (guildChannels.containsKey(guildName)) {
			chan = guildChannels.get(guildName);
		}
		if (chan.containsKey(name)) {
			return chan.get(name);
		} else {
			new File(logFolder.getAbsolutePath() + "/" + guildName).mkdirs();
			ChatLogChannel clc = new ChatLogChannel(guildName, name,
					new File(logFolder.getAbsolutePath() + "/" + guildName, name + ".channel"));
			chan.put(name, clc);
			guildChannels.put(guildName, chan);
			return clc;
		}
	}

	ArrayList<String> listChannels(String guildName) {
		ArrayList<String> ch = new ArrayList<>();
		if (!new File(logFolder.getAbsolutePath() + "/" + guildName).exists()) {
			return ch;
		} else {
			for (File channel : new File(logFolder.getAbsolutePath() + "/" + guildName).listFiles()) {
				if (channel.getName().endsWith(".channel")) {
					ch.add(channel.getName().split(".channe")[0]);
				}
			}
		}
		return ch;
	}

}
