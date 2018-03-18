package de.DiscordBot.Commands;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import com.kimbrelk.da.oauth2.AuthGrantType;
import com.kimbrelk.da.oauth2.ClientCredentials;
import com.kimbrelk.da.oauth2.OAuth2;
import com.kimbrelk.da.oauth2.response.RespDeviationsQuery;
import com.kimbrelk.da.oauth2.response.RespError;
import com.kimbrelk.da.oauth2.response.Response;

import de.DiscordBot.CommandExecutor;
import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import de.DiscordBot.Config.ConfigurableOption;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

public class DeviantArtSearch extends DiscordCommand {

  public DeviantArtSearch() {
    super(
            "deviant", new String[] {},
            "Searches for an random Image using one single tag (!) on DeviantArt (Caution, can be naughty!)",
            "\\deviant [tag]");
    // TODO Auto-generated constructor stub
  }

  static OAuth2                         auth;
  static HashMap<String, List<Integer>> tags = new HashMap<String, List<Integer>>();

  public static Object searchForRandomImage(String search) {
    Random random = new Random();
    if (auth == null || auth.getToken().hasExpired()) {
      ClientCredentials CREDENTIALS =
              new ClientCredentials(6923, "c4e2973fbe94ac4ce8cf3dcd4e69a20d");
      auth = new OAuth2(CREDENTIALS, "desktop:lewdBot:1.0");
      Response r = auth.requestAuthToken(AuthGrantType.CLIENT_CREDENTIALS, null, null);
      if (r instanceof RespError) {
        System.out.println(((RespError) r).getDescription());
        if (((RespError) r).getDescription().contains("overloaded")) {
          auth = null;
          return null;
        }
      }
    }
    int offset = 0;
    if (tags.containsKey(search)) {
      List<Integer> i = tags.get(search);
      if (random.nextInt(i.size() + 1) > i.size()) {
        offset = i.size();
      } else {
        offset = i.get(random.nextInt(i.size()));
      }
    }
    Response r = auth.requestBrowseTags(search, offset, 25);
    if (r.isSuccess()) {
      RespDeviationsQuery response = (RespDeviationsQuery) r;
      if (tags.containsKey(search)) {
        tags.get(search).add(response.getNextOffset());
      } else {
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(0);
        list.add(response.getNextOffset());
        tags.put(search, list);
      }
      int size = response.getResults().length;
      System.out.println("Found " + size + " results for " + search);
      if(size<1) {
        return new Integer(0);
      }
      int rand = random.nextInt(size);
      File cache = new File("DeviantCache.png");
      try {
        ImageIO.write(
                ImageIO.read(new URL(response.getResults()[rand].getContent().getSource())), "png",
                cache);
        return cache;
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    } else {
      System.out.println(((RespError) r).getDescription());
      return null;
    }
    return null;
  }

  @Override
  public Object execute(String command, String[] args, Message m) {
    if (args.length < 1 || args[0].isEmpty()) {
      return new MessageBuilder()
              .append("You need to supply at least one tag to search for!").build();
    }
    String tag = args[0];
    if(args.length>1) {
      tag = CommandExecutor.join(args, 0).replace(" ", "_");
    }
    Object ca = searchForRandomImage(args[0]);
    if (ca == null) {
      return new MessageBuilder()
              .append("Service Unavailable due to overloading or spamming").build();
    }else if(ca instanceof Integer) {
      return new MessageBuilder().append("There is no image tagged " + tag + " on deviantart! Sorry :(").build();
    }else {
    m
            .getChannel()
            .sendFile((File)ca, new MessageBuilder().append("Have Fun with your pic!").build())
            .complete();
    ((File)ca).delete();
    }
    return "";
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
