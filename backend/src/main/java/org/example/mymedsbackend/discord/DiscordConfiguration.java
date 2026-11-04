package org.example.mymedsbackend.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordConfiguration {

    @Bean
    @ConditionalOnExpression("'${DISCORD_TOKEN:}' != ''")
    public JDA jda() throws InterruptedException {
        return JDABuilder.createLight(System.getenv("DISCORD_TOKEN"))
                .build()
                .awaitReady();
    }
}
