package de.DiscordBot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.security.auth.login.LoginException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.gson.Gson;

import de.DiscordBot.ChatLog.ChatLog;
import de.DiscordBot.ChatLog.ChatLogChannel;
import de.DiscordBot.ChatLog.ChatLogMessage;
import de.DiscordBot.Commands.DiscordService;
import javautils.UtilHelpers.Cleanable;
import javautils.mysql.MySQLConfiguration;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DiscordBot extends ListenerAdapter {

  /** The Discord Bot Token used to connect to Discord */
  private static String discordToken = "";

  public static MySQLConfiguration mysql;

  /** The Discord Bot Instance to be used to send messages */
  private static JDA bot;

  /** The ExecutorService used to spawn threads to respond to commands! */
  static ExecutorService exec;

  static TextChannel botChannel;

  public static void main(String[] args) throws GeneralSecurityException, IOException {
    // Get The Command Line Options
    CommandLine cmd = parseArguments(args);

    String sqlIP = "localhost:3306";
    if (cmd.hasOption("mysqlIP")) {
      sqlIP = cmd.getOptionValue("mysqlIP");
    }
    String user = cmd.getOptionValue("sqlUser");
    String pw2 = cmd.getOptionValue("sqlPassword");
    String db = cmd.getOptionValue("sqlDB");
    try {
      mysql = new MySQLConfiguration(user, pw2, sqlIP, db);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(
              "We were unable to connect to the MySQL Server! Please check your Settings!");
      System.exit(0);
    }

    discordToken = cmd.getOptionValue("dT");

    try {
      bot = new JDABuilder(AccountType.BOT)
              .setToken(discordToken).setAudioEnabled(false).addEventListener(new DiscordBot())
              .buildBlocking();
    } catch (
            LoginException
            | IllegalArgumentException
            | InterruptedException
            | RateLimitedException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    CommandExecutor.setChatLog(new ChatLog(new File("chatLogs")));

    // Extract ChatLog if the "ext" flag is true
    if (cmd.hasOption("ext")) {
      String guild = cmd.getOptionValue("ext");
      String chan = null;
      if (cmd.hasOption("exChan")) {
        chan = cmd.getOptionValue("exChan");
      }

      File log = new File("GuildChatLog-" + guild + (chan != null ? "-" + chan : "") + ".log");
      PrintWriter pw = null;
      try {
        log.createNewFile();
        pw = new PrintWriter(new FileWriter(log));
      } catch (Exception e) {

      }
      // Get Every Channel of the Server
      HashMap<String, ChatLogChannel> c =
              CommandExecutor.getChatLog().listChannels(bot.getGuildsByName(guild, true).get(0));
      int msgs = 0;
      for (ChatLogChannel cl : c.values()) {
        if (chan != null) {
          if (!cl.name.equalsIgnoreCase(chan)) {
            continue;
          }
        }
        cl.load();
        for (ChatLogMessage clm : cl.clm) {
          // Write Every Message's content to the File
          pw.println(new Date(clm.time).toString() + ":" + clm.user + ":" + clm.content);
          msgs++;
        }
      }
      pw.flush();
      pw.close();
      System.out.println("Successfully extracted " + msgs + " Messages!");
      System.exit(0);
    }
    // Set the current Game of the Bot
    bot.getPresence().setGame(Game.of(cmd.getOptionValue("game", "Rule34")));

    int threadCount = 5;

    if (cmd.hasOption("threads")) {
      String t = cmd.getOptionValue("threads", "5");
      try {
        threadCount = Integer.valueOf(t);
        if (threadCount < 1) {
          throw new Exception("");
        }
      } catch (Exception e) {
        System.err.println("The Thread Count needs to be a positive Integer bigger than 0!");
      }
    }

    // Initialise The Executor Service
    exec = Executors.newFixedThreadPool(threadCount);
    CommandExecutor.refresh();
    String input = "";
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    while((input=br.readLine())!=null) {
    	if(input.trim().equalsIgnoreCase("/reload")) {
    		CommandExecutor.refresh();
    	}
    }
  }
  
  static ArrayList<DiscordService> services = new ArrayList<DiscordService>();
  
  public static void startService(DiscordService service) {
	  services.add(service);
	  new Thread(service).start();
  }
  
  public static void stopServices() {
	  for(DiscordService ds : services) {
		  ds.shutdown();
	  }
	  services.clear();
  }

  /**
   * Parse Arguments and Display Help if either the -help switch is activated or
   * no arguments were passed down.
   *
   * @param args
   *          The CommandLine Arguments
   * @return The Resulting CommandLine Object
   */
  @SuppressWarnings("deprecation")
  private static CommandLine parseArguments(String[] args) {

    Options options = new Options();

    Option help = new Option("help", "Displays Help");
    Option discordToken = OptionBuilder
            .withLongOpt("discordToken").withArgName("token")
            .withDescription("The Discord Token of the Bot instance").hasArg().isRequired()
            .create("dT");
    Option extractChatLog = OptionBuilder
            .withLongOpt("extractChat")
            .withDescription("Extracts the ChatLog of a given Discord Server").withArgName("server")
            .hasArg().create("ext");
    Option extractChannel = OptionBuilder
            .withLongOpt("extractChannel").withDescription("Extracts only a single Channel")
            .withArgName("channel").hasArg().create("exChan");

    Option gameDisplay = OptionBuilder
            .withLongOpt("displayedGame").withArgName("game").hasArg()
            .withDescription("Displays given Game as currently playing (Default: Rule34)")
            .create("game");
    Option threads = OptionBuilder
            .withArgName("threadCount").hasArg()
            .withDescription("Sets the Threads used by the Bot to reply to commands (Default: 5)")
            .create("threads");
    Option mysql = OptionBuilder
            .withArgName("ip:port").hasArg()
            .withDescription("Sets ip and port of the used MySQL Server (Default: localhost:3306)")
            .create("mysqlIP");
    Option sqluser = OptionBuilder
            .isRequired().withArgName("user").hasArg()
            .withDescription("Sets the user used for the MySQL Server").create("sqlUser");
    Option sqlPW = OptionBuilder
            .isRequired().withArgName("pw").hasArg()
            .withDescription("Sets the password used for the MySQL Server").create("sqlPassword");
    Option sqlDB = OptionBuilder
            .isRequired().withArgName("db").hasArg()
            .withDescription("Sets the database used for the MySQL Server").create("sqlDB");

    options.addOption(help);
    options.addOption(discordToken);
    options.addOption(extractChatLog);
    options.addOption(extractChannel);
    options.addOption(gameDisplay);
    options.addOption(threads);
    options.addOption(mysql);
    options.addOption(sqluser);
    options.addOption(sqlPW);
    options.addOption(sqlDB);
    CommandLineParser clp = new DefaultParser();

    CommandLine cmd = null;
    try {
      cmd = clp.parse(options, args);
    } catch (ParseException e) {
      HelpFormatter hf = new HelpFormatter();
      hf.printHelp("java -jar DiscordBot.jar", options, true);
      System.exit(0);
    }

    return cmd;
  }

  public static JDA getBot() {
    return bot;
  }

  static Gson gson = new Gson();
  
  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    // Don't do anything if you're the original Author
    if (event.getAuthor() == event.getJDA().getSelfUser()) {
      return;
    }
    if (event.getChannelType() == ChannelType.TEXT) {
      ChatLogChannel clc = CommandExecutor
              .getChatLog().getChannel(event.getGuild(), event.getChannel().getName());
      clc.addChatMessage(event.getAuthor(), event.getMessage());
      System.out.println(
              "["
                      + event.getTextChannel().getName() + "] " + event.getAuthor().getName() + ": "
                      + event.getMessage().getContent());

      if (event.getMessage().getContent().toLowerCase().contains("alia")) {
        botChannel = event.getTextChannel();
        exec.execute(new NLPCommand(event));
      }
    }

    if (event.getMessage().getContent().startsWith("\\")) {
      exec.execute(new CommandExecutor(event));
    }
  }

}
