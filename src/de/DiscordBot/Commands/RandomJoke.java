package de.DiscordBot.Commands;

import java.util.LinkedList;

import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import de.DiscordBot.Config.ConfigurableOption;
import de.DiscordBot.Config.OptionType;
import net.dean.jraw.models.Submission;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

public class RandomJoke extends DiscordCommand{

  public RandomJoke() {
    super("joke", new String[] {}, "Looks for a random joke on /r/jokes", "\\joke");
    // TODO Auto-generated constructor stub
  }

  @Override
  public Object execute(String command, String[] args, Message m) {
    if(RandomMeme.rc==null || RandomMeme.rc.isAuthenticated()) {
      RandomMeme.initiateRC();
    }
    Submission s = RandomMeme.rc.getRandomSubmission(getConfig(m.getGuild()).getValue("defaultSubreddit","jokes"));
    MessageBuilder mb = new MessageBuilder().append(s.getTitle());
    if(!s.getSelftext().isEmpty()) {
      mb.append("\n\n\n");
      mb.append(s.getSelftext().replace("&gt", ">").replace("&lt", "<").replace("&amp", "&").replace("&quot", "\""));
    }
    return mb.build();
  }

  @Override
  public void setupCommandConfig(Guild g, Config cfg) {
    cfg.setValue("defaultSubreddit", "jokes");

  }

  @Override
  public ConfigPage createRemoteConfigurable() {
    LinkedList<ConfigurableOption> conf =new LinkedList<>();
    conf.add(new ConfigurableOption(this, "Joke Subreddit", "Which subreddit to use for getting jokes", OptionType.STRING, "defaultSubreddit", new String[] {}));
    return new ConfigPage("/r/jokes", this, conf);
  }

  @Override
  public boolean isRemoteConfigurable() {
    // TODO Auto-generated method stub
    return true;
  }

}
