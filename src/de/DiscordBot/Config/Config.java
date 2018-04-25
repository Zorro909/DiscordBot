package de.DiscordBot.Config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

import de.DiscordBot.DiscordBot;
import de.DiscordBot.Commands.DiscordCommand;
import javautils.UtilHelpers.Cleanable;
import javautils.mysql.Entry;
import javautils.mysql.MySQLConfiguration;
import net.dv8tion.jda.core.entities.Guild;

public class Config {

	private MySQLConfiguration conf = null;
	private String cmdName = "";
	private Long gID;
	private HashMap<String, Cleanable<String>> cache = new HashMap<String, Cleanable<String>>();

	public Config(String cmdName, Guild g, DiscordCommand d) {
		conf = DiscordBot.mysql;
		this.cmdName = cmdName;
		if (g != null) {
			gID = g.getIdLong();
		} else {
			gID = 0L;
		}
		if (!d.sqlConfigured.contains(gID)) {
			d.setupCommandConfig(null, this);
			d.sqlConfigured.add(gID);
		}
	}

	public String getValue(String option) throws SQLException, InterruptedException {
		option = option.toLowerCase();
		if (cache.containsKey(option)) {
			return cache.get(option).getObject();
		}
		ResultSet rs = conf.get(cmdName, "VALUE", "GUILD", "" + gID, "OPTIONGUILD", option + gID);
		if (!rs.first()) {
			return null;
		} else {
			String value = rs.getString("VALUE");
			Cleanable<String> c = new Cleanable<String>(value, 5000, 1000 * 60 * 5);
			c.addToMap(option, cache);
			return value;
		}
	}

	public HashMap<String, String> getValues(String... keys) {
		HashMap<String, String> vals = new HashMap<String, String>();
		String sql = "SELECT VALUE, OPTIONGUILD FROM " + cmdName + " WHERE GUILD = '" + gID + "' AND OPTIONGUILD IN (";
		for (String s : keys) {
			if (!cache.containsKey(s + gID)) {
				sql += "'" + s + gID + "',";
			} else {
				vals.put(s, cache.get(s + gID).getObject());
			}
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += ");";
		try {
			Statement s = conf.getConnection().createStatement();
			ResultSet rs = s.executeQuery(sql);
			rs.beforeFirst();
			int gIDLength = String.valueOf(gID).length();
			while (rs.next()) {
				String optionguild = rs.getString("OPTIONGUILD");
				Cleanable<String> c = new Cleanable<String>(rs.getString("value"), 5000, 1000 * 60 * 5);
				c.addToMap(optionguild, cache);
				optionguild = optionguild.substring(0, optionguild.length() - gIDLength);
				vals.put(optionguild, rs.getString("VALUE"));
			}
			rs.close();
			s.close();
			return vals;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vals;
	}

	public ArrayList<String> getKeys(String regex) {
		ArrayList<String> keys = new ArrayList<String>();
		Statement s;
		try {
			s = conf.getConnection().createStatement();
			ResultSet rs = s.executeQuery("SELECT OPTIONGUILD from " + cmdName
					+ " WHERE OPTIONGUILD REGEXP_LIKE(OPTIONGUILD, '" + regex + "');");
			rs.beforeFirst();
			while (rs.next()) {
				String optionguild = rs.getString("OPTIONGUILD");
				optionguild = optionguild.substring(0, optionguild.length() - String.valueOf(gID).length());
				keys.add(optionguild);
			}
			rs.close();
			s.close();
			return keys;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return keys;
		}
	}

	public void setValue(String option, String value) {
		option = option.toLowerCase();
		try {
			conf.update(cmdName, new String[] { "VALUE", "GUILD", "OPTIONGUILD" },
					new String[] { value, gID + "", option + gID });
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Cleanable<String> c = new Cleanable<String>(value, 5000, 1000 * 60 * 5);
		c.addToMap(option, cache);
	}

	public void setValue(String option, boolean value) {
		setValue(option, value ? "true" : "false");
	}

	public boolean getBooleanValue(String option) throws SQLException, InterruptedException {
		String val = getValue(option);
		if (val.equalsIgnoreCase("true")) {
			return true;
		} else {
			return false;
		}
	}

	public Map<String, Boolean> getBooleanValues(String... keys) {
		HashMap<String, String> vals = getValues(keys);
		Map<String, Boolean> bool = vals.entrySet().stream()
				.collect(Collectors.toMap((Map.Entry<String, String> entry) -> entry.getKey(),
						(entry) -> entry.getValue().equals("true") ? true : false));
		return bool;
	}

	public String getValue(String option, String defaultValue) {
		try {
			return getValue(option);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public int getIntValue(String string, int defaultValue) {
		try {
			return Integer.valueOf(getValue(string, defaultValue + ""));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public Map<String, Integer> getIntValues(String... keys) {
		HashMap<String, String> vals = getValues(keys);
		Map<String, Integer> ints = vals.entrySet().stream().collect(Collectors.toMap(
				(Map.Entry<String, String> entry) -> entry.getKey(), (entry) -> Integer.valueOf(entry.getValue())));
		return ints;
	}

	public void setIntValue(String string, int i) {
		setValue(string, i + "");
	}

}
