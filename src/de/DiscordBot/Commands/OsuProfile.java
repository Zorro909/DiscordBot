package de.DiscordBot.Commands;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import de.DiscordBot.Config.ConfigurableOption;
import javautils.HTTPManager.Connection;
import javautils.HTTPManager.InetManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed.AuthorInfo;
import net.dv8tion.jda.core.entities.MessageEmbed.Thumbnail;
import net.dv8tion.jda.core.entities.impl.MessageEmbedImpl;

public class OsuProfile extends DiscordCommand{

  public OsuProfile() {
    super("osu", new String[] {"osuprofile"}, "Creates an overview over a Osu player", "\\osu [playername]");
    // TODO Auto-generated constructor stub
  }

  static Gson gson = new Gson();

  @Override
  public Object execute(String command, String[] args, Message m) {
    if(args.length<1) {
      return new MessageBuilder().append("Wrong Usage: " + getUsage());
    }
    Connection c;
    String json = "";
    try {
      c = InetManager.openConnection(
              "https://osu.ppy.sh/api/get_user?k=a13f90c09608958770e6a897fed71e8c05e75557&u="
                      + args[0]);
      json = c.get();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    JsonArray userData = gson.fromJson(json, JsonArray.class);
    if (userData.size() == 0) {
      return new MessageBuilder().append("Sorry, but the user " + args[0] + " can't be found!").build();
    }
    JsonObject jo = userData.get(0).getAsJsonObject();
    String username = jo.get("username").getAsString();

    String pp = jo.get("pp_raw").getAsString();

    String rank = jo.get("pp_rank").getAsString();

    String totalScore = jo.get("total_score").getAsString();
    String rankedScore = jo.get("ranked_score").getAsString();

    String playcount = jo.get("playcount").getAsString();
    String accuracy = jo.get("accuracy").getAsString();

    String count_ss = jo.get("count_rank_ss").getAsString();
    String count_s = jo.get("count_rank_s").getAsString();
    String count_a = jo.get("count_rank_a").getAsString();

    String level = jo.get("level").getAsString();
    String countryRank =
            jo.get("pp_country_rank").getAsString() + " (" + jo.get("country").getAsString() + ")";

    EmbedBuilder stats = new EmbedBuilder();
    stats.setAuthor(
            username, "https://osu.ppy.sh/users/" + jo.get("user_id").getAsString(),
            "https://a.ppy.sh/" + jo.get("user_id").getAsString() + "?1481308162.png");
    stats.setColor(Color.pink);
    BufferedImage bi = null;
    try {
      bi = ImageIO.read(
              new URL("https://a.ppy.sh/" + jo.get("user_id").getAsString() + "?1481308162.png"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (bi != null) {
      stats.setThumbnail("https://a.ppy.sh/" + jo.get("user_id").getAsString() + "?1481308162.png");
    }
    stats.addField("Performance Points", pp, true);
    stats.addField("Rank", "#" + rank + "\n#" + countryRank, true);
    stats.addField(
            "Accuracy", (((double) Math.round(Double.valueOf(accuracy) * 10000)) / 10000) + "%",
            true);
    stats.addField("Playcount", playcount, true);
    stats.addField("Level", level, true);
    stats.addField("Total Score", totalScore, true);
    stats.addField("Ranked Score", rankedScore, true);
    stats.addField("SS-Badges", count_ss, true);
    stats.addField("S-Badges", count_s, true);
    stats.addField("A-Badges", count_a, true);
    return stats.build();
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
