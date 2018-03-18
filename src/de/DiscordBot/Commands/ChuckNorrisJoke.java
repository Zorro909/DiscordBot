package de.DiscordBot.Commands;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import de.DiscordBot.Config.ConfigurableOption;
import javautils.HTTPManager.Connection;
import javautils.HTTPManager.InetManager;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class ChuckNorrisJoke extends DiscordCommand {

  public ChuckNorrisJoke() {
    super("chucknorris", new String[] {"chuck"}, "Gets a random Chuck Norris joke (Chuck Norris can be replaced with a username)", "\\chucknorris {@SpecifyUser}");
    // TODO Auto-generated constructor stub
  }

  public static String fetchRandomJoke() {
    try {
      Connection con = InetManager.openConnection("http://api.icndb.com/jokes/random");
      JsonObject jo = new Gson().fromJson(con.get(), JsonObject.class);
      return jo.get("value").getAsJsonObject().get("joke").getAsString().replace("&quot", "\"");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return "Error occured while contacting database! :(";
  }

  @Override
  public Object execute(String command, String[] args, Message m) {
    if (m.getMentionedUsers().size() > 0) {
      for (User u : m.getMentionedUsers()) {
        String joke = fetchRandomJoke();
        joke = joke.replace("Chuck Norris", u.getName()).replace("Chuck", u.getName());
        m.getChannel().sendMessage(u.getAsMention() + " " + joke).submit();
      }
      return "";
    } else {
      return new MessageBuilder().append(fetchRandomJoke()).setTTS(true).build();
    }
  }

  @Override
  public void setupCommandConfig(Guild g, Config cfg) {
    // TODO Auto-generated method stub

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
