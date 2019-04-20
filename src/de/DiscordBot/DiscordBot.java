package de.DiscordBot;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import de.DiscordBot.ChatLog.ChatLogChannel;
import de.DiscordBot.Commands.DiscordService;
import de.DiscordBot.Config.ConfigPropertyRepository;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

@SpringBootApplication
@EnableScheduling
@Component
@Configuration
public class DiscordBot extends ListenerAdapter {

	@Setter
	/** The Discord Bot Instance to be used to send messages */
	private JDA discordBot;

	@Setter
	@Getter
	private int threadCount = 5;

	@Getter
	private static DiscordBot instance;

	@Autowired
	@Getter
	public ConfigPropertyRepository configPropertyRepository;
	
	public DiscordBot() {
		instance = this;
	}
	
	/** The ExecutorService used to spawn threads to respond to commands! */
	@Getter
	private static ExecutorService execService;

	public static void main(String[] args) throws GeneralSecurityException, IOException {
		execService = Executors.newFixedThreadPool(5);
		new SpringApplicationBuilder(DiscordBot.class).web(WebApplicationType.NONE).build()
				.run(args);
	}

	static ArrayList<DiscordService> services = new ArrayList<DiscordService>();

	public static void startService(DiscordService service) {
		services.add(service);
		new Thread(service).start();
	}

	public static void stopServices() {
		for (DiscordService ds : services) {
			ds.shutdown();
		}
		services.clear();
	}

	@Bean
	public static JDA discordJDABot() {
		if (instance == null) {
			return null;
		}
		return instance.discordBot;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		// Don't do anything if you're the original Author
		if (event.getAuthor() == event.getJDA().getSelfUser()) {
			return;
		}
		if (event.getChannelType() == ChannelType.TEXT) {
			ChatLogChannel clc = CommandExecutor.getChatLog().getChannel(event.getGuild(),
					event.getChannel().getName());
			clc.addChatMessage(event.getAuthor(), event.getMessage());
			System.out.println("[" + event.getTextChannel().getName() + "] " + event.getAuthor().getName() + ": "
					+ event.getMessage().getContent());

			if (event.getMessage().getContent().toLowerCase().contains("alia")) {
				execService.execute(new NLPCommand(event));
				return;
			}
		}

		if (event.getMessage().getContent().startsWith("\\")) {
			execService.execute(new CommandExecutor(event));
		}
	}

	public static void registerEmoteChangeListener(Message m, ListenerAdapter la) {
		registerEmoteChangeListener(m, la, 60000);
	}

	public static void registerEmoteChangeListener(Message m, ListenerAdapter la, long time) {
		registeredListeners.put(m.getId(), la, time);
	}

	static ExpiringMap<String, ListenerAdapter> registeredListeners = new ExpiringMap<>();

	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent gmrae) {
		if (registeredListeners.containsKey(gmrae.getMessageId())) {
			ListenerAdapter la = registeredListeners.get(gmrae.getMessageId());
			if (la == null) {
				registeredListeners.remove(gmrae.getMessageId());
			} else {
				la.onGuildMessageReactionAdd(gmrae);
			}
		}
	}

	@Override
	public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent gmrre) {
		if (registeredListeners.containsKey(gmrre.getMessageId())) {
			ListenerAdapter la = registeredListeners.get(gmrre.getMessageId());
			if (la == null) {
				registeredListeners.remove(gmrre.getMessageId());
			} else {
				la.onGuildMessageReactionRemove(gmrre);
			}
		}
	}

}
