package de.DiscordBot.ChatLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;


import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public class ChatLogChannel {

  String guild;
  public String name;
  File log;
  FileWriter fw;

  public LinkedList<ChatLogMessage> clm = new LinkedList<ChatLogMessage>();

  public ChatLogChannel(String guild, String name, File log) {
    this.guild = guild;
    this.name = name;
    this.log = log;
    if(!log.exists()) {
      try {
        log.createNewFile();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    try {
      fw = new FileWriter(log, true);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void addChatMessage(User author, Message message) {
    try {
      String ment = "";
      for(User u : message.getMentionedUsers()) {
        ment += ":" + Base64.getEncoder().encodeToString(u.getName().getBytes("UTF-8"));
      }
      fw.append(Base64.getEncoder().encodeToString(author.getName().getBytes("UTF-8")) + ":" + author.isBot() + ":" + message.getCreationTime().toEpochSecond() + ":" + Base64.getEncoder().encodeToString(message.getContent().getBytes("UTF-8")).replace(":", "\\\\\\\\double////") + ment + "\n");
      fw.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void load() {
    try {
      clm.clear();
      BufferedReader br = new BufferedReader(new FileReader(log));
      String line = "";
      while((line=br.readLine())!=null) {
          String args[] = line.split(":");
          ArrayList<String> ment = new ArrayList<String>();
          if(args.length>4) {
            for(int i = 4; i < args.length;i++) {
              String name = args[i];
              try {
                name = new String(Base64.getDecoder().decode(name),"UTF-8");
              }catch(Exception e) {

              }
              ment.add(args[i]);
            }
          }
          clm.add(new ChatLogMessage(new String(Base64.getDecoder().decode(args[0]),"UTF-8"), Long.valueOf(args[2]), Boolean.valueOf(args[1]), (args.length > 3 ? new String(Base64.getDecoder().decode(args[3].replace("\\\\\\\\double////", ":")),"UTF-8") : ""), ment));
      }
      br.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
