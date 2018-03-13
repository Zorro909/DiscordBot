package CookieRPG.CookieRPG;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class MessageHandler {

  @EventSubscriber
  public void onReadyEvent(ReadyEvent event) { // This method is called when the ReadyEvent is dispatched
    System.out.println("Bot ready!");
  }

  @EventSubscriber
  public void onMessageReceivedEvent(MessageReceivedEvent event) {
    System.out.println(event.getMessage().getContent());
  }

}
