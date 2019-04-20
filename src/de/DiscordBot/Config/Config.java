package de.DiscordBot.Config;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import de.DiscordBot.Commands.DiscordCommand;
import lombok.NonNull;
import net.dv8tion.jda.core.entities.Guild;

public class Config {

    private ConfigPropertyRepository configPropertyRepository;

    private String cmdName = "";
    private Long gID;

    public Config(String cmdName, Guild g, DiscordCommand d, ConfigPropertyRepository cpr) {
    	configPropertyRepository = cpr;
    	this.cmdName = cmdName;
        if (g != null) {
            gID = g.getIdLong();
        } else {
            gID = 0L;
        }
        if (!d.sqlConfigured.contains(gID)) {
            d.setupCommandConfig(null, this);
            d.sqlConfigured.add(gID);
        }
    }

    public String getValue(@NonNull String option) throws SQLException, InterruptedException {
        option = option.toLowerCase();
        return configPropertyRepository.findById(option + cmdName + gID).orElseGet(() -> new ConfigProperty(null, null))
                .getValue();
    }

    public HashMap<String, String> getValues(String... keys) {
        HashMap<String, String> vals = new HashMap<String, String>();
        configPropertyRepository.findAllById(
                Lists.newArrayList(keys).stream().map((str) -> str + cmdName + gID).collect(Collectors.toList()))
                .forEach((value) -> {
                    vals.put(
                            value.getOptionGuild().substring(0,
                                    value.getOptionGuild().length() - cmdName.length() - String.valueOf(gID).length()),
                            value.getValue());
                });
        return vals;
    }

    /*
     * public List<String> getKeys(String regex) { return configPropertyRepository.findValueByOptionGuildRegex(regex +
     * cmdName + gID).stream() .map((str) -> str.substring(0, str.length() - cmdName.length() -
     * String.valueOf(gID).length())) .collect(Collectors.toList()); }
     */

    public void setValue(String option, String value) {
        System.out.println("Repo ist null ? " + (configPropertyRepository == null));
    	ConfigProperty cp = configPropertyRepository.findById(option + cmdName + gID)
                .orElse(new ConfigProperty(option + cmdName + gID, value));
        cp.setValue(value);
        configPropertyRepository.save(cp);
    }

    public void setValue(String option, boolean value) {
        setValue(option, value ? "true" : "false");
    }

    public boolean getBooleanValue(String option) throws SQLException, InterruptedException {
        String val = getValue(option);
        if (val.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

    public Map<String, Boolean> getBooleanValues(String... keys) {
        HashMap<String, String> vals = getValues(keys);
        Map<String, Boolean> bool = vals.entrySet().stream()
                .collect(Collectors.toMap((Map.Entry<String, String> entry) -> entry.getKey(),
                        (entry) -> entry.getValue().equals("true") ? true : false));
        return bool;
    }

    public String getValue(String option, String defaultValue) {
        try {
            return getValue(option);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public int getIntValue(String string, int defaultValue) {
        try {
            return Integer.valueOf(getValue(string, defaultValue + ""));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Map<String, Integer> getIntValues(String... keys) {
        HashMap<String, String> vals = getValues(keys);
        Map<String, Integer> ints = vals.entrySet().stream().collect(Collectors.toMap(
                (Map.Entry<String, String> entry) -> entry.getKey(), (entry) -> Integer.valueOf(entry.getValue())));
        return ints;
    }

    public void setIntValue(String string, int i) {
        setValue(string, i + "");
    }

}
