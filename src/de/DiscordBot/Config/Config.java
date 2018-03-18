package de.DiscordBot.Config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import de.DiscordBot.DiscordBot;
import de.DiscordBot.Commands.DiscordCommand;
import javautils.UtilHelpers.Cleanable;
import javautils.mysql.Entry;
import javautils.mysql.MySQLConfiguration;
import net.dv8tion.jda.core.entities.Guild;

public class Config {

  private MySQLConfiguration conf    = null;
  private String             cmdName = "";
  private Long               gID;
  private HashMap<String, Cleanable<String>> cache = new HashMap<String, Cleanable<String>>();

  public Config(String cmdName, Guild g, DiscordCommand d) {
    conf = DiscordBot.mysql;
    this.cmdName = cmdName;
    gID = g.getIdLong();
    if (!d.sqlConfigured.contains(g.getIdLong())) {
      d.setupCommandConfig(g, this);
      d.sqlConfigured.add(g.getIdLong());
    }
  }

  public String getValue(String option) throws SQLException, InterruptedException {
    option = option.toLowerCase();
    if(cache.containsKey(option)) {
      return cache.get(option).getObject();
    }
    ResultSet rs = conf.get(cmdName,"VALUE", "GUILD",""+ gID, "OPTIONGUILD", option + gID);
    if(!rs.first()) {
      return null;
    }else {
      String value = rs.getString("VALUE");
      Cleanable<String> c = new Cleanable<String>(value, 5000, 1000 * 60 * 5);
      c.addToMap(option, cache);
      return value;
    }
  }

  public void setValue(String option, String value) {
    option = option.toLowerCase();
    try {
      conf.update(cmdName, new String[] {"VALUE",value}, new String[] {"GUILD", gID + "", "OPTIONGUILD", option + gID});
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
    if(val.equalsIgnoreCase("true")) {
      return true;
    }else {
      return false;
    }
  }

  public String getValue(String option, String defaultValue) {
    try {
      return getValue(option);
    }catch(Exception e) {
      return defaultValue;
    }
  }

}
