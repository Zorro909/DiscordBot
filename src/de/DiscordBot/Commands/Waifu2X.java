package de.DiscordBot.Commands;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Message.Attachment;

public class Waifu2X extends DiscordCommand{

  public Waifu2X() {
    super("waifu", new String[] {"waifu2x"}, "Upscales a image using the Waifu2X API", "Attach a Image to your command message");
    // TODO Auto-generated constructor stub
  }

  @Override
  public Object execute(String command, String[] args, Message m) {
    if (m.getAttachments().isEmpty() || m.getAttachments().size() > 1) {
      return new MessageBuilder().append("You need to attach exactly one Picture to apply Waifu2x!").build();
    }
    Attachment a = m.getAttachments().get(0);
    try {
      if(a.getHeight()>2559||a.getWidth()>2559) {
        return new MessageBuilder().append("The attached Image must be smaller than 2560*2560 in dimensions!").build();
      }
      Process p = new ProcessBuilder(
              "curl", "-F", "image=" + a.getUrl(), "-H",
              "api-key:8b40a9fc-39ea-401c-b656-fa804d380874", "https://api.deepai.org/api/waifu2x")
                      .start();
      int seconds = 0;
      BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String back = "";
      String line = "";
      while (!p.waitFor(2, TimeUnit.SECONDS)) {
        seconds += 2;
        if (seconds == 30) {
          m.getChannel().sendMessage("Still processing image... please wait").submit();
        }
        while(br.ready()) {
          back+=(char)br.read();
        }
        if(back.endsWith("}")) {
          p.destroyForcibly();
        }
      }
      while ((line = br.readLine()) != null) {
        back += line + "\n";
        System.out.println(line);
      }
      JsonObject jo = new Gson().fromJson(back, JsonObject.class);
      String url = jo.get("output_url").getAsString();
      BufferedImage bi = ImageIO.read(new URL(url));
      File f = new File(new Random().nextInt(9999) + ".png");
      ImageIO.write(bi, "png", f);
      m.getChannel()
              .sendFile(f, new MessageBuilder().append("Your Waifu2x processed Picture!").build())
              .complete();
      f.delete();
      return "";
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
