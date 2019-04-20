package de.DiscordBot;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;

import de.DiscordBot.Config.ConfigPropertyRepository;
import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

@Component
public class DiscordBotShell implements ApplicationRunner {

	@Autowired
	public ConfigPropertyRepository configPropertyRepository;

	@Getter
	public static ConfigPropertyRepository cpr;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		cpr = configPropertyRepository;
		System.out.println("TEST " + (cpr==null));
		File configFile = new File("discordBot.config");
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

		CommandExecutor.refresh(configPropertyRepository);
	}

	private void printConfigUsage() {
		System.out.println("Please create a Config File with following Keys:\ndiscordToken, threadCount, gameDisplay");
		System.exit(0);
	}

}
