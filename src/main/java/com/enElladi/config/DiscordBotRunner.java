package com.enElladi.config;


import com.enElladi.listeners.MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DiscordBotRunner implements CommandLineRunner {

    protected static final String GUILD_ID = "1439239459056124111";

    @Override
    public void run(String @NonNull ... args) throws Exception {
        String token = System.getenv("DISCORD_TOKEN"); // token is from system env you have to put your own!

        if(token == null || token.isBlank()) {
            System.out.println("args: " + Arrays.toString(args));
            throw new IllegalStateException("DISCORD_TOKEN env var is missing");
        }

        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .build();

        jda.awaitReady();

        System.out.println("Bot is online!");

        Guild guild = jda.getGuildById(GUILD_ID);

        if(guild == null) return;

        List<GuildChannel> guildChannels = guild.getChannels();

        MessageFetcher.listenOldMessages(jda, guildChannels);

        jda.addEventListener(new MessageListener(guildChannels));
    }
}
