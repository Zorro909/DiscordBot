package de.DiscordBot.Commands;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import de.DiscordBot.Config.ConfigurableOption;
import de.DiscordBot.Config.OptionType;
import fastily.jwiki.core.Wiki;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

public class WikiBot extends DiscordCommand {

  public WikiBot() {
    super("wiki", new String[] {"wikipedia", "knowitall"}, "Searches for a certain site on wikipedia!", "\\wiki [search]");
  }

  @Override
  public Object execute(String command, String[] args, Message m) {
    Config cfg = getConfig(m.getGuild());
    String lang = "en";
    try {
      lang = cfg.getValue("language");
    } catch (SQLException | InterruptedException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    Wiki wiki = new Wiki(lang + ".wikipedia.org");
    List<String> l = wiki.allPages(m.getContent().split(" ", 2)[1], false, false, 1, null);
    if (l.isEmpty()) {
      return new MessageBuilder().append("Sorry, but your Request was not found on Wikipedia!").build();
    } else {
      String title = "";
      try {
        title = wiki.resolveRedirect(l.get(0));
      } catch (Exception e) {
        title = l.get(0);
      }
      EmbedBuilder eb = new EmbedBuilder();
      eb.setTitle(title);
      List<String> img = wiki.getImagesOnPage(title);
      if (img.isEmpty()) {
        img.add("https://" + lang + ".wikipedia.org/static/images/project-logos/enwiki.png");
      } else {
        img.set(0, wiki.getImageInfo(img.get(0)).get(0).url.toString());
      }
      System.out.println(img.get(0));
      eb.setAuthor(
              "Wikipedia", "https://" + lang + ".wikipedia.org/wiki/" + title.replace(" ", "_"), img.get(0));
      String text = wiki.getTextExtract(title);
      if (text.length() > 1024) {
        String add = " [Read More!](https://" + lang + ".wikipedia.org/wiki/" + title.replace(" ", "_") + ")";
        text = text.substring(0, 1023 - add.length()) + add;
      }
      eb.addField("Summary", text, false);
      eb.setImage(img.get(0));
      return eb.build();
    }
  }

  @Override
  public void setupCommandConfig(Guild g, Config cfg) {
    cfg.setValue("language", "en");
  }

  @Override
  public ConfigPage createRemoteConfigurable() {
    LinkedList<ConfigurableOption> conf = new LinkedList<ConfigurableOption>();
    conf.add(new ConfigurableOption(this, "Language", "Which version of wikipedia to use (example: en.wikipedia.org)", OptionType.MAP, "language", TranslateCommand.langs));
    ConfigPage cp = new ConfigPage("WikipediaCommand", this, conf);
    return cp;
  }

  @Override
  public boolean isRemoteConfigurable() {
    return true;
  }

}
