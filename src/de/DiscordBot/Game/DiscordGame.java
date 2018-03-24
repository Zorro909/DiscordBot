package de.DiscordBot.Game;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import de.DiscordBot.CommandExecutor;
import de.DiscordBot.DiscordBot;
import javautils.mysql.Entry;
import javautils.mysql.MySQLConfiguration;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public abstract class DiscordGame {

	private String name;

	public DiscordGame(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	protected abstract void loadGameConfig(Guild g, TextChannel tc, MySQLConfiguration sql);

	protected abstract void setupGameConfig(Guild g, TextChannel tc, MySQLConfiguration sql);

	public abstract void receiveMessage(Message m, MySQLConfiguration sql);

	public boolean registerGameChannel(TextChannel tc) {
		if (CommandExecutor.gameChannels.containsKey(tc.getId())) {
			return false;
		}
		MySQLConfiguration sql = DiscordBot.mysql;
		Entry e = new Entry("games");
		String ch = name + ";";
		try {
			ch += sql.get("games", "CHANNELS", "NAME", name.toLowerCase()).getString("CHANNELS");
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		HashMap<String, String> v = new HashMap<String, String>();
		v.put("NAME", name.toLowerCase());
		v.put("CHANNELS", ch);
		e.setValues(v);
		if (!sql.updateEntry(e)) {
			return false;
		}
		return true;
	}
}
