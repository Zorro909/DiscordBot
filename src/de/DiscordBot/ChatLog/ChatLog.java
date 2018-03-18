package de.DiscordBot.ChatLog;

import java.io.File;
import java.util.HashMap;

import net.dv8tion.jda.core.entities.Guild;

public class ChatLog {

  private HashMap<String, HashMap<String, ChatLogChannel>> guildChannels = new HashMap<>();
  private File                                             logFolder;

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
          System.out
                  .println("What does the File " + guild.getName() + " do in the ChatLog Folder?");
        }
      }
    }
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
      ChatLogChannel clc = new ChatLogChannel(
              guild.getName(), name,
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
      for (File channel : new File(logFolder.getAbsolutePath() + "/" + guild.getName())
              .listFiles()) {
        if (channel.getName().endsWith(".channel")) {
          ch.put(
                  channel.getName().split(".channe")[0],
                  getChannel(guild, channel.getName().split(".channe")[0]));
        }
      }
    }

    return ch;
  }


}
