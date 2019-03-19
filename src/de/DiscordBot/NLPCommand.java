package de.DiscordBot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

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
            HttpsURLConnection con = (HttpsURLConnection) new URL("https://api.wit.ai/message?v=20170307&q="
                    + URLEncoder.encode(event.getMessage().getContent(), "UTF-8")).openConnection();
            con.addRequestProperty("Authorization", "Bearer DINQH4C6U5OAAHO32PSBYQWQKQ5OL22X");
            con.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String response = "";
            String line = "";
            while ((line = br.readLine()) != null) {
                response += line + "\n";
            }
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
                intent = entities.getAsJsonArray("intent").get(0).getAsJsonObject().get("value").getAsString();
            }
            if (entities.has("word")) {
                word = entities.getAsJsonArray("word").get(0).getAsJsonObject().get("value").getAsString();
            }
            if (entities.has("recipient")) {
                recipient = entities.getAsJsonArray("recipient").get(0).getAsJsonObject().get("value").getAsString();
            }
            if (entities.has("phrase_to_translate")) {
                phrase_to_translate = entities.getAsJsonArray("phrase_to_translate").get(0).getAsJsonObject()
                        .get("value").getAsString();
            }
            if (entities.has("language")) {
                language = entities.getAsJsonArray("language").get(0).getAsJsonObject().get("value").getAsString();
            }

            if (intent.equalsIgnoreCase("meaning") && recipient.equalsIgnoreCase("myself")) {
                if (!word.isEmpty()) {
                    Field sub = MessageImpl.class.getDeclaredField("subContent");
                    sub.setAccessible(true);
                    sub.set(event.getMessage(), "\\urban" + " " + word);
                    DiscordBot.getExecService().execute(new CommandExecutor(event));
                    return;
                }
            }
            if (intent.equalsIgnoreCase("megaman") && word.equalsIgnoreCase("gate")) {
                event.getChannel().sendMessage(
                        "...\r\n" + "Let's get on with the mission.\r\n" + "I have nothing to discuss about that.")
                        .submit();
                return;
            }
            if (recipient.equalsIgnoreCase("myself") && intent.equalsIgnoreCase("translate")
                    && !phrase_to_translate.isEmpty() && !language.isEmpty()) {
                Field sub = MessageImpl.class.getDeclaredField("subContent");
                sub.setAccessible(true);
                sub.set(event.getMessage(), "\\" + language + " " + phrase_to_translate);
                DiscordBot.getExecService().execute(new CommandExecutor(event));
                return;
            }
            if (recipient.equalsIgnoreCase("myself") && intent.equalsIgnoreCase("hangman")) {
                Field sub = MessageImpl.class.getDeclaredField("subContent");
                sub.setAccessible(true);
                sub.set(event.getMessage(), "\\hangman");
                DiscordBot.getExecService().execute(new CommandExecutor(event));
            }
        } catch (IOException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException
                | SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
