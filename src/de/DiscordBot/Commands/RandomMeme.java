package de.DiscordBot.Commands;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import de.DiscordBot.Config.ConfigurableOption;
import de.DiscordBot.Config.OptionType;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.http.oauth.OAuthHelper;
import net.dean.jraw.models.OEmbed;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class RandomMeme extends DiscordCommand {

  public RandomMeme() {
    super("randommeme", new String[] {"rmem", "rmeme", "meme", "reddit"}, "Searches for a random image on a subreddit (default: /r/memes)", "\\randommeme {subreddit}");
  }

  public static RedditClient rc;

  private static Object[] getRandomImg(String string) {

    Submission s = rc.getRandomSubmission(string);
    BufferedImage bi = null;
    while (bi == null) {
      try {
        if (s.getOEmbedMedia() != null) {
          bi = ImageIO.read(s.getOEmbedMedia().getThumbnail().getUrl());
        } else {
          bi = ImageIO.read(new URL(s.getUrl()));
        }
      } catch (Exception e) {
        s = rc.getRandomSubmission(string);
      }
    }

    return new Object[] { bi, s };
  }

  static void initiateRC() {
    Credentials c = Credentials
            .script("Zorro909HD", "Zorro909HD", "GO6UsElDHOoGbA", "iFIFYPtBVOqEpdPoeQCNSMp-vds");

    rc = new RedditClient(UserAgent.of("desktop:LewdBot:1.0(by /u/Zorro909HD)"));
    OAuthHelper oa = rc.getOAuthHelper();
    try {
      OAuthData oad = oa.easyAuth(c);
      System.out.println(oa.getAuthStatus().name());
      rc.authenticate(oad);
    } catch (NetworkException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (OAuthException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public Object execute(String command, String[] args, Message m) {
    if (rc == null || !rc.isAuthenticated()) {
      initiateRC();
    }
    String subreddit = "memes";
    try {
      subreddit = getConfig(m.getGuild()).getValue("defaultSubreddit");
    } catch (SQLException | InterruptedException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    if(args.length>0) {
      subreddit = args[0];
    }
    Object[] o = getRandomImg(subreddit);
    BufferedImage bi = (BufferedImage) o[0];
    Submission s = (Submission) o[1];
    try {
      File cache = new File(new Random().nextInt(9999) + ".png");
      if (cache.length() > (8 << 20)) {
        return execute(command,args,m);
      }
      ImageIO.write(bi, "png", cache);
      MessageBuilder mb = new MessageBuilder().append("Your random meme: ");
      m.getChannel().sendFile(cache, s.getTitle() + ".png", mb.build()).complete();
      cache.delete();
      return "";
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void setupCommandConfig(Guild g, Config cfg) {
    cfg.setValue("defaultSubreddit", "memes");
  }

  @Override
  public ConfigPage createRemoteConfigurable() {
    LinkedList<ConfigurableOption> conf =new LinkedList<>();
    conf.add(new ConfigurableOption(this, "Meme Subreddit", "Which subreddit to use for getting memes", OptionType.STRING, "defaultSubreddit", new String[] {}));
    return new ConfigPage("/r/Meme", this, conf);
  }

  @Override
  public boolean isRemoteConfigurable() {
    // TODO Auto-generated method stub
    return true;
  }

}
