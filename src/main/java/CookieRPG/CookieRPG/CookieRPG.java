package CookieRPG.CookieRPG;

import sx.blah.discord.api.IDiscordClient;

public class CookieRPG {

  private IDiscordClient discord;

  public CookieRPG(IDiscordClient discord) {
    this.discord = discord;
    discord.getDispatcher().registerListener(new MessageHandler());
  }


}
