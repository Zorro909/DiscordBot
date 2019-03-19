package de.DiscordBot.Commands;

import de.DiscordBot.DiscordBot;
import net.dv8tion.jda.core.JDA;

public abstract class DiscordService implements Runnable {

    @Override
    public abstract void run();

    public abstract void shutdown();

    public JDA getJDA() {
        return DiscordBot.discordJDABot();
    }
}
