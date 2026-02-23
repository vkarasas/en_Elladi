package com.enElladi.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MessageFetcher {

    /**
     *  Loads old messages
     * @param jda JDA
     * @param guildChannels List<GuildChannel>
     */
    public static void listenOldMessages(JDA jda, List<GuildChannel> guildChannels) {

        List<Map<String, Object>> channelListJson = new ArrayList<>();
        for(GuildChannel guildChannel : guildChannels) {
            TextChannel textChannel = jda.getTextChannelById(guildChannel.getId());
            if (textChannel != null) {
                List<Message> messages = loadAllMessages(textChannel);
                messages.forEach(message -> System.out.println(guildChannel.getName() + " " + message.getContentRaw()));
                channelListJson.add(mapChannelJson(messages, textChannel));
            }
        }

        extractToJson(channelListJson);
    }

    private static void extractToJson(List<Map<String, Object>> channelListJson) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("discord_dump.json")){
            gson.toJson(channelListJson, writer);
            System.out.println("Saved all messages to discord_dump.json JSON:" +   channelListJson);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static  Map<String, Object> mapChannelJson(List<Message> messages, TextChannel textChannel) {
        List<Map<String, Object>> msgJsonList = new ArrayList<>();

        for(Message message : messages) {
            Map<String, Object> msgJson = new LinkedHashMap<>();
            msgJson.put("id", message.getId());
            msgJson.put("author", message.getAuthor().getName());
            msgJson.put("content", message.getContentRaw());
            msgJsonList.add(msgJson);
        }

        Map<String, Object> channelJson = new LinkedHashMap<>();
        channelJson.put("id", textChannel.getId());
        channelJson.put("name", textChannel.getName());
        channelJson.put("messages", msgJsonList);
        return channelJson;
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
