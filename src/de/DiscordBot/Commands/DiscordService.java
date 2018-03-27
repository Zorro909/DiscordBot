package de.DiscordBot.Commands;

public abstract class DiscordService implements Runnable {

	@Override
	public abstract void run();
	
	public abstract void shutdown();

}
