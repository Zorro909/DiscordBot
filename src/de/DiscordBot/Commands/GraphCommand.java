package de.DiscordBot.Commands;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import de.DiscordBot.Config.ConfigurableOption;
import de.DiscordBot.Config.OptionType;
import de.Zorro909.GraphDrawer.Graph;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

public class GraphCommand extends DiscordCommand {

  public GraphCommand() {
    super("graph", new String[] {"math","hate","love"}, "Creates a Graph of the supplied function", "\\graph [minX] [minY] [function]");
    // TODO Auto-generated constructor stub
  }

  @Override
  public Object execute(String command, String[] args, Message m) {
    Config c = getConfig(m.getGuild());
    String p = c.getValue("precision", "0.05");
    double precision = Double.valueOf(p);
    MessageBuilder mb = new MessageBuilder();
    if(command.equalsIgnoreCase("hate")) {
      mb.append("You hate math? Ask @Zorro909#1972 for help if you need any.\n");
    }else if(command.equalsIgnoreCase("love")) {
      mb.append("You love math? Cool! Me too!\n");
    }
    Graph g = new Graph(join(args,2), Integer.valueOf(args[0]), Integer.valueOf(args[1]), precision);
    Object o = g.drawGraph();
    if(o instanceof String) {
      mb.append((String)o);
      return mb.build();
    }
    File l = new File("graph" + System.currentTimeMillis() + ".png");
    try {
      ImageIO.write((RenderedImage) o, "png", l);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    m.getChannel().sendFile(l, (!mb.isEmpty() ? mb.build() : null)).complete();
    l.delete();
    return "";
  }

  private String join(String[] args, int i) {
    String s = "";
    for(i = i;i<args.length;i++) {
      s+=args[i] + " ";
    }
    return s;
  }

  @Override
  public void setupCommandConfig(Guild g, Config cfg) {
    cfg.setValue("precision", "0.05");
  }

  @Override
  public ConfigPage createRemoteConfigurable() {
    LinkedList<ConfigurableOption> conf =new LinkedList<>();
    conf.add(new ConfigurableOption(this, "Precision", "How precise should the Graph Command be?", OptionType.LIST, "defaultSubreddit", new String[] {"1","0.5","0.25","0.2","0.1","0.05", "0.01"}));
    ConfigPage cp = new ConfigPage("GraphDrawer", this, conf);
    return cp;
  }

  @Override
  public boolean isRemoteConfigurable() {
    // TODO Auto-generated method stub
    return true;
  }

}
