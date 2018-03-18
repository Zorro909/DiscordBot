package de.DiscordBot.Commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import de.DiscordBot.Config.ConfigurableOption;
import javautils.UtilHelpers.FileUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class HangmanCommand extends DiscordCommand implements EventListener {

  List<String> wordlist;

  public HangmanCommand(JDA bot) {
    super("hangman", new String[] {}, "Let's you play a round of Hangman", "\\hangman");

    try {
      wordlist = Arrays.asList(FileUtils.readAll(new File("wordlist.txt")).split("\n"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    bot.addEventListener(this);
  }

  HashMap<MessageChannel, Game> state = new HashMap<MessageChannel, Game>();

  @Override
  public Object execute(String command, String[] args, Message m) {
    if (state.containsKey(m.getChannel())) {
      return new MessageBuilder()
              .append(
                      "Sorry but in this Channel there is already a Game playing, finish it before starting a new one!")
              .build();
    } else {
      Game g = new Game();
      g.channel = m.getChannel();
      g.word = wordlist.get(new Random().nextInt(wordlist.size())).toLowerCase().trim();

      String underscores = "";
      for (char c : g.word.toCharArray()) {
        if (c == ' ') {
          underscores += "  ";
        } else {
          if (g.guesses.contains(Character.valueOf(c))) {
            underscores += c + " ";
          } else {
            underscores += "_ ";
          }
        }
      }

      Message msg = new MessageBuilder()
              .append(
                      "Hangman\nA Hangman game was started! Guess by writing a single Character into this channel")
              .appendCodeBlock(
                      "    _________\n"
                              + "    |         |\n" + "    |         \n" + "    |        \n"
                              + "    |        \n" + "    |\n" + "    |",
                      "")
              .appendCodeBlock("Guesses: " + g.guesses.size() + "\nWord: " + underscores, "")
              .build();
      g.lastId = m.getChannel().sendMessage(msg).complete().getId();
      state.put(m.getChannel(), g);
      return "";
    }
  }

  private class Game {

    String               lastId;
    MessageChannel       channel;
    String               word    = "";
    int                  wrongs  = 0;
    ArrayList<Character> guesses = new ArrayList<Character>();

  }

  @Override
  public void onEvent(Event event) {
    if (event instanceof MessageReceivedEvent) {
      MessageReceivedEvent mre = (MessageReceivedEvent) event;

      if (state.containsKey(mre.getChannel())) {
        if (mre.getMessage().getContent().length() == 1) {
          char guess = mre.getMessage().getContent().toLowerCase().charAt(0);
          Game g = state.get(mre.getChannel());
          mre.getMessage().delete().submit();
          if (g.guesses.contains(Character.valueOf(guess))) {
            g.channel.sendMessage("The Letter " + guess + " was already guessed!").submit();
          } else {
            if (!g.word.contains(guess + "")) {
              g.wrongs++;
            }
            g.guesses.add(Character.valueOf(guess));
            String underscores = "";
            for (char c : g.word.toCharArray()) {
              if (c == ' ') {
                underscores += "  ";
              } else {
                if (g.guesses.contains(Character.valueOf(c))) {
                  underscores += c + " ";
                } else {
                  underscores += "_ ";
                }
              }
            }

            String hangman = "";

            hangman = "    _________\n"
                    + "    |        |\n" + "    |        " + (g.wrongs > 0 ? "0" : "") + "\n"
                    + "    |       " + (g.wrongs > 2 ? "/" : "") + (g.wrongs > 1 ? "|" : "")
                    + (g.wrongs > 3 ? "\\" : "") + "\n" + "    |       " + (g.wrongs > 4 ? "/" : "")
                    + " " + (g.wrongs > 5 ? "\\" : "") + "\n" + "    |\n" + "    |";

            if (!underscores.contains("_")) {
              mre.getChannel().deleteMessageById(g.lastId).submit();
              Message msg = new MessageBuilder()
                      .append(
                              "A Hangman game was started! Guess by writing a single Character into this channel")
                      .appendCodeBlock(hangman, "")
                      .appendCodeBlock(
                              "Guesses: " + g.guesses.size() + "\nWord: " + underscores, "")
                      .append("You've won! Appreciate it! Party-Time!").build();
              mre.getChannel().sendMessage(msg).submit();
              state.remove(mre.getChannel());
              return;
            } else {
              mre.getChannel().deleteMessageById(g.lastId).submit();
              if (g.wrongs > 5) {
                Message msg = new MessageBuilder()
                        .append("Hangman\nGuess by writing a single Character into this channel")
                        .appendCodeBlock(hangman, "")
                        .appendCodeBlock(
                                "Guesses: " + g.guesses.size() + "\nWord: " + underscores, "")
                        .append(
                                "I'm sorry, but you unfortunately lost this round, the word was: "
                                        + g.word)
                        .build();
                mre.getChannel().sendMessage(msg).submit();
                state.remove(mre.getChannel());
                return;
              }

              Message msg = new MessageBuilder()
                      .append("Hangman\nGuess by writing a single Character into this channel")
                      .appendCodeBlock(hangman, "")
                      .appendCodeBlock(
                              "Guesses: " + g.guesses.size() + "\nWord: " + underscores, "")
                      .build();
              g.lastId = mre.getChannel().sendMessage(msg).complete().getId();
            }
          }
        }
      }

    }
  }

  @Override
  public void setupCommandConfig(Guild g, Config cfg) {
    // TODO Auto-generated method stub

  }

  @Override
  public ConfigPage createRemoteConfigurable() {
    return null;
  }

  @Override
  public boolean isRemoteConfigurable() {
    return false;
  }

}
