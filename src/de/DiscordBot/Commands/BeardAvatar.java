package de.DiscordBot.Commands;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import de.DiscordBot.Config.ConfigurableOption;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class BeardAvatar extends DiscordCommand{

  public BeardAvatar() {
    super("mustacheavatar", new String[] {"mustache", "beard"}, "Puts a mustache on the avatar of the user", "\\mustacheavatar {@User}");
    // TODO Auto-generated constructor stub
  }

  static BufferedImage beard;

  private static void init() {
    if (beard == null) {
      try {
        beard = ImageIO.read(new File("beard.png"));
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    System.setProperty(
            "http.agent",
            "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
  }

  private static BufferedImage getAvatar(User user) {
    try {
      Process p = new ProcessBuilder("curl","--output","avcache.png", user.getAvatarUrl()).start();
      p.waitFor();
      File f = new File("avcache.png");
      if(!f.exists()) {
        return null;
      }
      BufferedImage bi = ImageIO.read(f);
      f.delete();
      return bi;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return null;
  }

  @Override
  public Object execute(String command, String[] args, Message m) {
    init();
    if (m.getMentionedUsers().isEmpty()) {
      try {
        BufferedImage bi = getAvatar(m.getAuthor());
        bi.getGraphics().drawImage(
                beard, bi.getWidth() / 4, bi.getHeight() / 4, bi.getWidth() / 2, bi.getHeight() / 2,
                null);
        File cache = new File(new Random().nextInt(9999) + ".png");
        ImageIO.write(bi, "png", cache);
        m.getChannel()
                .sendFile(
                        cache, m.getAuthor().getName() + ".png", new MessageBuilder()
                                .append(m.getAuthor().getName() + " with Beard! :joy:").build())
                .complete();
        cache.delete();
        return "";
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    } else {
      try {
        BufferedImage bi = getAvatar(m.getMentionedUsers().get(0));
        bi.getGraphics().drawImage(
                beard, bi.getWidth() / 4, bi.getHeight() / 4, bi.getWidth() / 2, bi.getHeight() / 2,
                null);
        File cache = new File(new Random().nextInt(9999) + ".png");
        ImageIO.write(bi, "png", cache);
        m.getChannel()
                .sendFile(
                        cache, m.getMentionedUsers().get(0) + ".png",
                        new MessageBuilder()
                                .append(m.getMentionedUsers().get(0) + " with Beard! :joy:").build())
                .complete();
        cache.delete();
        return "";
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return null;
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
