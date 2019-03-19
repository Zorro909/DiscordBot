package de.DiscordBot.Config;

import org.springframework.data.repository.CrudRepository;

public interface ConfigPropertyRepository extends CrudRepository<ConfigProperty, String> {

    // List<String> findValueByOptionGuildRegex(String optionguild);

}
