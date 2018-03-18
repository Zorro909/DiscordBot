package de.DiscordBot.ChatLog;

import java.util.ArrayList;

public class ChatLogMessage {

  public String user, content;
  public long time;
  boolean bot;
  ArrayList<String> ment;

  public ChatLogMessage(String user, long time, boolean bot, String content, ArrayList<String> ment) {
     this.user = user;
     this.content = content;
     this.time = time;
     this.bot = bot;
     this.ment = ment;
  }

}
