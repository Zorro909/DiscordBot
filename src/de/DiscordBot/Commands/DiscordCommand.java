package de.DiscordBot.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.DiscordBot.Config.Config;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

public abstract class DiscordCommand {

    private String cmdName, description, usage;
    private String[] commandAliases;
    public ArrayList<Long> sqlConfigured = new ArrayList<Long>();

    public abstract Object execute(String command, String[] args, Message m);

    public abstract void setupCommandConfig(Guild g, Config cfg);

    public abstract boolean isRemoteConfigurable();

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public String getCommandName() {
        return cmdName;
    }

    public String[] getCommandAliases() {
        return commandAliases;
    }

    protected void addCommandAlias(String alias) {
        commandAliases = Arrays.copyOf(commandAliases, commandAliases.length + 1);
        commandAliases[commandAliases.length - 1] = alias;
    }

    public DiscordCommand(String cmdName, String[] commandAliases, String description, String usage) {
        this.cmdName = cmdName;
        this.commandAliases = commandAliases;
        this.description = description;
        this.usage = usage;
    }

    private HashMap<Long, Config> conf = new HashMap<Long, Config>();

    public Config getConfig(Guild g) {
        if (!conf.containsKey(g.getIdLong())) {
            conf.put(g.getIdLong(), new Config(cmdName, g, this));
        }
        return conf.get(g.getIdLong());
    }

    public Config getGlobalConfig() {
        if (!conf.containsKey(null)) {
            conf.put(null, new Config(cmdName, null, this));
        }
        return conf.get(null);
    }

}
