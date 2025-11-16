package config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageFetcher {

    /**
     *  Loads old messages
     * @param jda JDA
     * @param guildChannels List<GuildChannel>
     */
    public static void listenOldMessages(JDA jda, List<GuildChannel> guildChannels) {
        for(GuildChannel guildChannel : guildChannels) {
            TextChannel textChannel = jda.getTextChannelById(guildChannel.getId());
            if (textChannel != null) {
                List<Message> messages = loadAllMessages(textChannel);
                messages.forEach(message -> System.out.println(guildChannel.getName() + " " + message.getContentRaw()));
            }
        }
    }

    private static List<Message> loadAllMessages(TextChannel channel) {
        List<Message> allMessages = new ArrayList<>();

        List<Message> batch;
        Message last = null;

        do {
            if (last == null) {
                batch = channel.getHistory().retrievePast(100).complete();
            } else {
                batch = channel.getHistoryBefore(last.getId(), 100).complete().getRetrievedHistory();
            }

            allMessages.addAll(batch);

            if (!batch.isEmpty()) {
                last = batch.get(batch.size() - 1);
            }

        } while (batch.size() == 100); // Discord max per batch

        // Reverse list → Oldest first
        Collections.reverse(allMessages);

        return allMessages;
    }
}
