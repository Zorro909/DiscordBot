package de.DiscordBot.Config;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Entity
@Table(name = "ConfigTable")
public class ConfigProperty {

    @Id
    @Getter
    private String optionGuild;

    @Getter
    @Setter
    private String value;

}
