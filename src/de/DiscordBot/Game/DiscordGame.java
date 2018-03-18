package de.DiscordBot.Game;

import java.util.ArrayList;

import de.DiscordBot.Config.GameConfig;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

public abstract class DiscordGame {

	public ArrayList<Long> sqlConfigured = new ArrayList<Long>();

	public abstract void setupGameConfig(Guild g, TextChannel tc, GameConfig gameConfig);

}
