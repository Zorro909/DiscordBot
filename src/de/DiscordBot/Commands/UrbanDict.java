package de.DiscordBot.Commands;

import java.io.IOException;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import javautils.HTTPManager.Connection;
import javautils.HTTPManager.InetManager;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

public class UrbanDict extends DiscordCommand{

  public UrbanDict() {
    super("urbandict", new String[] {"urban", "wtf"}, "Looks up a certain word using UrbanDictionary", "\\urbandict [word]");
    // TODO Auto-generated constructor stub
  }

  public static void sendToChannel(String string, MessageChannel channel) {
    Connection con;
    channel.sendTyping().submit();
    try {
      con = InetManager.openConnection("http://api.urbandictionary.com/v0/define?term=" + string.toLowerCase());
      con.initGet(false, new HashMap<String,String>());
      JsonObject jo = new Gson().fromJson(con.get(), JsonObject.class);
      if(jo.get("result_type").getAsString().equalsIgnoreCase("no_results")) {
        channel.sendMessage("Sorry, but the word " + string + " was not found on UrbanDictionary").submit();
        return;
      }
      JsonArray ja = jo.get("list").getAsJsonArray();
      JsonObject def = ja.get(0).getAsJsonObject();
      channel.sendMessage("Definition for " + def.get("word").getAsString() + " from UrbanDictionary:\n" + def.get("definition").getAsString()).submit();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  @Override
  public Object execute(String command, String[] args, Message m) {
    Connection con;
    try {
      con = InetManager.openConnection("http://api.urbandictionary.com/v0/define?term=" + args[0]);
      con.initGet(false, new HashMap<String,String>());
      JsonObject jo = new Gson().fromJson(con.get(), JsonObject.class);
      if(jo.get("result_type").getAsString().equalsIgnoreCase("no_results")) {
        return new MessageBuilder().append("Sorry, but the word " + args[0] + " was not found on UrbanDictionary").build();
      }
      JsonArray ja = jo.get("list").getAsJsonArray();
      JsonObject def = ja.get(0).getAsJsonObject();
      return new MessageBuilder().append("Definition for " + def.get("word").getAsString() + " from UrbanDictionary:\n" + def.get("definition").getAsString()).build();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
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
