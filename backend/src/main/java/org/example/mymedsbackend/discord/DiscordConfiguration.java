package org.example.mymedsbackend.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordConfiguration {

    @Bean
    public JDA jda() throws InterruptedException {
        if (System.getenv("DISCORD_TOKEN").isBlank()) { // running without discord bot
            System.out.println("Disabled discord bot :O");
            return null;
        }

        return JDABuilder.createLight(System.getenv("DISCORD_TOKEN"))
                .build().awaitReady();
    }
}
