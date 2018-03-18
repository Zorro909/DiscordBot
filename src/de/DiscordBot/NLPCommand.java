package de.DiscordBot;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import javautils.HTTPManager.Connection;
import javautils.HTTPManager.InetManager;
import net.dv8tion.jda.core.entities.impl.MessageImpl;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class NLPCommand implements Runnable {

  private MessageReceivedEvent event;
  private Gson gson = new Gson();

  public NLPCommand(MessageReceivedEvent event) {
    this.event = event;
  }

  @Override
  public void run() {
    try {
      System.out.println("Execute!");
      Connection c = InetManager.openConnection(
              "https://api.wit.ai/message?v=20170307&q=" + URLEncoder.encode(event.getMessage().getContent(), "UTF-8"));
      c.getHttpConnection().addRequestProperty(
              "Authorization", "Bearer DINQH4C6U5OAAHO32PSBYQWQKQ5OL22X");
      c.initGet(false, new HashMap<String, String>());
      String response = c.get();
      JsonReader jr = new JsonReader(new StringReader(response.trim()));
      jr.setLenient(true);
      JsonObject j = gson.fromJson(jr, JsonObject.class);
      JsonObject entities = j.getAsJsonObject("entities");
      String intent = "";
      String word = "";
      String recipient = "";
      String phrase_to_translate = "";
      String language = "";

      if (entities.has("intent")) {
        intent = entities
                .getAsJsonArray("intent").get(0).getAsJsonObject().get("value").getAsString();
      }
      if (entities.has("word")) {
        word = entities.getAsJsonArray("word").get(0).getAsJsonObject().get("value").getAsString();
      }
      if (entities.has("recipient")) {
        recipient = entities
                .getAsJsonArray("recipient").get(0).getAsJsonObject().get("value").getAsString();
      }
      if (entities.has("phrase_to_translate")) {
        phrase_to_translate = entities
                .getAsJsonArray("phrase_to_translate").get(0).getAsJsonObject().get("value")
                .getAsString();
      }
      if (entities.has("language")) {
        language = entities
                .getAsJsonArray("language").get(0).getAsJsonObject().get("value").getAsString();
      }

      if (intent.equalsIgnoreCase("meaning") && (recipient.equalsIgnoreCase("myself"))) {
        if (!word.isEmpty()) {
          Field sub = MessageImpl.class.getDeclaredField("subContent");
          sub.setAccessible(true);
          sub.set(event.getMessage(),"\\urban" + " " + word);
          DiscordBot.exec.execute(new CommandExecutor(event));
          return;
        }
      }
      if (intent.equalsIgnoreCase("megaman") && word.equalsIgnoreCase("gate")) {
        DiscordBot.botChannel
                .sendMessage(
                        "...\r\n"
                                + "Let's get on with the mission.\r\n"
                                + "I have nothing to discuss about that.")
                .submit();
        return;
      }
      if (recipient.equalsIgnoreCase("myself") && intent.equalsIgnoreCase("translate")
              && !phrase_to_translate.isEmpty() && !language.isEmpty()) {
        Field sub = MessageImpl.class.getDeclaredField("subContent");
        sub.setAccessible(true);
        sub.set(event.getMessage(),"\\" + language + " " + phrase_to_translate);
        DiscordBot.exec.execute(new CommandExecutor(event));
        return;
      }
      if(recipient.equalsIgnoreCase("myself") && intent.equalsIgnoreCase("hangman")) {
        Field sub = MessageImpl.class.getDeclaredField("subContent");
        sub.setAccessible(true);
        sub.set(event.getMessage(),"\\hangman");
        DiscordBot.exec.execute(new CommandExecutor(event));
      }
    } catch (IOException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}
