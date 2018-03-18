package de.DiscordBot.Config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import de.DiscordBot.DiscordBot;
import de.DiscordBot.Game.DiscordGame;
import javautils.UtilHelpers.Cleanable;
import javautils.mysql.MySQLConfiguration;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

public class GameConfig {

	private MySQLConfiguration conf = null;
	private String cmdName = "";
	private Long gID;
	private HashMap<String, Cleanable<String>> cache = new HashMap<String, Cleanable<String>>();
	private long textChannelID;

	public GameConfig(String game, Guild g, TextChannel tc, DiscordGame dGame) {
		conf = DiscordBot.mysql;
		this.cmdName = cmdName;
		gID = g.getIdLong();
		textChannelID = tc.getIdLong();
		if (!dGame.sqlConfigured.contains(tc.getIdLong())) {
			dGame.setupGameConfig(g, tc, this);
		}
	}

	public String getValue(String key) throws SQLException, InterruptedException {
		key = key.toLowerCase();
		if (cache.containsKey(key)) {
			return cache.get(key).getObject();
		}
		ResultSet rs = conf.get(cmdName, "VALUE", "KEYUSERCHANNEL", key + textChannelID);
		if (!rs.first()) {
			return null;
		} else {
			String value = rs.getString("VALUE");
			Cleanable<String> c = new Cleanable<String>(value, 5000, 1000 * 60 * 5);
			c.addToMap(key, cache);
			return value;
		}
	}
	
	public String getUserValue(String user, String key) throws SQLException, InterruptedException {
		user = user.toLowerCase();
		key = key.toLowerCase();
		if (cache.containsKey(user+key)) {
			return cache.get(user+key).getObject();
		}
		ResultSet rs = conf.get(cmdName, "VALUE", "KEYUSERCHANNEL", key + user + textChannelID);
		if (!rs.first()) {
			return null;
		} else {
			String value = rs.getString("VALUE");
			Cleanable<String> c = new Cleanable<String>(value, 5000, 1000 * 60 * 5);
			c.addToMap(user+key, cache);
			return value;
		}
	}

	public void setUserValue(String user, String option, String value) {
		user = user.toLowerCase();
		option = option.toLowerCase();
		try {
			conf.update(cmdName, new String[] { "VALUE", value },
					new String[] { "KEYUSERCHANNEL", user + option + textChannelID });
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Cleanable<String> c = new Cleanable<String>(value, 5000, 1000 * 60 * 5);
		c.addToMap(option, cache);
	}

	public String getUserValue(String user, String option, String defaultValue) {
		try {
			return getUserValue(user, option);
		} catch (Exception e) {
			return defaultValue;
		}
	}

}
