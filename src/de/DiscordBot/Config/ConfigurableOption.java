package de.DiscordBot.Config;

import java.util.HashMap;

import de.DiscordBot.Commands.DiscordCommand;

public class ConfigurableOption {
  DiscordCommand          dc;
  String                  label;
  String                  description;
  OptionType              type;
  String                  mysqlOption;
  HashMap<String, String> keyValuePairs;
  String[] list;

  public ConfigurableOption(
          DiscordCommand dc,
          String label,
          String description,
          OptionType type,
          String mysqlOption,
          HashMap<String, String> keyValuePairs) {
    this.dc = dc;
    this.label = label;
    this.description = description;
    this.type = type;
    this.mysqlOption = mysqlOption;
    this.keyValuePairs = keyValuePairs;
  }

  public ConfigurableOption(
          DiscordCommand dc,
          String label,
          String description,
          OptionType type,
          String mysqlOption,
          String[] list) {
    this.dc = dc;
    this.label = label;
    this.description = description;
    this.type = type;
    this.mysqlOption = mysqlOption;
    this.list = list;
  }

}
