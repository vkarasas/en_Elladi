package config;

import listeners.MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.List;

public class Config {
    protected static final String GUILD_ID = "1439239459056124111";

    public static void jdaBuild() throws InterruptedException {
        String token = System.getenv("DISCORD_TOKEN"); // token is from system env you have to put your own!
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
