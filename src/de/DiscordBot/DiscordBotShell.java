package de.DiscordBot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.security.auth.login.LoginException;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

@ShellComponent
public class DiscordBotShell {

    @ShellMethod(value = "Starts the Discord Bot with a given Config File", key = "start")
    public void list(@ShellOption(defaultValue = "discordBot.config") File configFile)
            throws FileNotFoundException, IOException {
        if (!configFile.exists()) {
            printConfigUsage();
        }

        Properties configProperties = new Properties();
        configProperties.load(new FileInputStream(configFile));

        if (!configProperties.keySet().contains("discordToken")) {
            printConfigUsage();
        }

        String discordToken = configProperties.getProperty("discordToken");
        JDA bot = null;

        int threadCount = 5;
        String t = configProperties.getProperty("threadCount", "5");
        try {
            threadCount = Integer.valueOf(t);
            if (threadCount < 1) {
                throw new Exception("");
            }
        } catch (Exception e) {
            System.err.println("The Thread Count needs to be a positive Integer bigger than 0!");
        }

        try {
            bot = new JDABuilder(AccountType.BOT).setToken(discordToken).setAudioEnabled(true)
                    .setAudioSendFactory(new NativeAudioSendFactory()).buildBlocking();
        } catch (LoginException | IllegalArgumentException | InterruptedException | RateLimitedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        DiscordBot discordBot = new DiscordBot();
        discordBot.setThreadCount(threadCount);
        discordBot.setDiscordBot(bot);

        bot.addEventListener(discordBot);

        // Set the current Game of the Bot
        bot.getPresence().setGame(Game.of(configProperties.getProperty("gameDisplay", "RandomRule34")));
    }

    private void printConfigUsage() {
        System.out.println("Please create a Config File with following Keys:\ndiscordToken, threadCount, gameDisplay");
        System.exit(0);
    }

}
