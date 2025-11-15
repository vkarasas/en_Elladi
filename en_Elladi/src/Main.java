
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Main {

    /**
     * Listener method for discord server bot
     */
    public static void listener() {
        String token = System.getenv("DISCORD_TOKEN"); // token is from system env you have to put your own!
        JDABuilder jdaBuilder = JDABuilder.createDefault(token, GatewayIntent.MESSAGE_CONTENT);

        jdaBuilder.addEventListeners(new ListenerAdapter() {
            @Override
            public void onReady(@NotNull ReadyEvent event) {
                // channels name of discord server
                List<TextChannel> textChannels = event.getJDA().getTextChannelsByName("category-one", true);

                if (!textChannels.isEmpty()) {
                    for(TextChannel textChannel: textChannels ) {
                        textChannel.getIterableHistory().forEach(msg ->
                                System.out.println(msg.getAuthor().getName() + " " + msg.getContentDisplay()));
                    }
                }
            }
        });

        jdaBuilder.build();
    }

    public static void main(String[] args)  {
        listener();
    }
}