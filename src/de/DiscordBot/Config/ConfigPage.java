package de.DiscordBot.Config;

import java.util.LinkedList;

import de.DiscordBot.Commands.DiscordCommand;

public class ConfigPage {

  public String                        label;
  public DiscordCommand                cmd;
  public LinkedList<ConfigurableOption> conf;

  public ConfigPage(String string, DiscordCommand cmd, LinkedList<ConfigurableOption> conf) {
    this.label = string;
    this.cmd = cmd;
    this.conf = conf;
  }

}