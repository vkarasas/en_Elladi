package com.enElladi.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jspecify.annotations.NonNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MessageFetcher {

    public static final String SRC_MAIN_RESOURCES_DISCORD_DUMP_JSON = "src/main/resources/discord_dump.json";

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
        try (FileWriter writer = new FileWriter("src/main/resources/discord_dump.json")){
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
                last = batch.getLast();
            }

        } while (batch.size() == 100); // Discord max per batch

        // Reverse list → Oldest first
        Collections.reverse(allMessages);

        return allMessages;
    }

    public static synchronized void addNewChannel(TextChannel textChannel) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String path = "src/main/resources/discord_dump.json";

        List<Map<String, Object>> channels;

        try {
            java.io.File file = new java.io.File(path);

            if (file.exists()) {
                try (java.io.FileReader reader = new java.io.FileReader(file)) {

                    java.lang.reflect.Type type =
                            new com.google.gson.reflect.TypeToken<
                                    List<Map<String, Object>>>() {}.getType();

                    channels = gson.fromJson(reader, type);

                    if (channels == null) {
                        channels = new ArrayList<>();
                    }
                }
            } else {
                channels = new ArrayList<>();
            }

            // Check if channel already exists
            boolean exists = channels.stream()
                    .anyMatch(channel ->
                            textChannel.getId().equals(
                                    String.valueOf(channel.get("id"))
                            )
                    );

            if (exists) {
                return;
            }

            Map<String, Object> newChannel = new LinkedHashMap<>();

            newChannel.put("id", textChannel.getId());
            newChannel.put("name", textChannel.getName());
            newChannel.put("messages", new ArrayList<>());

            channels.add(newChannel);

            try (FileWriter writer = new FileWriter(path)) {
                gson.toJson(channels, writer);
            }

            System.out.println(
                    "Added new channel to JSON: "
                            + textChannel.getName()
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not add new Discord channel to JSON",
                    e
            );
        }
    }

    public static synchronized void addNewMessage(TextChannel textChannel, Message message) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            java.lang.reflect.Type type =
                    new com.google.gson.reflect.TypeToken<
                            List<Map<String, Object>>>() {}.getType();

            List<Map<String, Object>> channels;

            try (java.io.FileReader reader = new java.io.FileReader(SRC_MAIN_RESOURCES_DISCORD_DUMP_JSON)) {
                channels = gson.fromJson(reader, type);
            }

            if (channels == null) {
                channels = new ArrayList<>();
            }

            for (Map<String, Object> channel : channels) {

                if (textChannel.getId().equals(String.valueOf(channel.get("id")))) {

                    List<Map<String, Object>> messages =
                            (List<Map<String, Object>>) channel.get("messages");

                    if (messages == null) {
                        messages = new ArrayList<>();
                        channel.put("messages", messages);
                    }

                    boolean exists = messages.stream()
                            .anyMatch(existing ->
                                    message.getId().equals(
                                            String.valueOf(existing.get("id"))
                                    )
                            );

                    if (exists) {
                        return;
                    }

                    Map<String, Object> newMessage = new LinkedHashMap<>();

                    newMessage.put("id", message.getId());
                    newMessage.put("author", message.getAuthor().getName());
                    newMessage.put("content", message.getContentRaw());

                    messages.add(newMessage);

                    try (FileWriter writer = new FileWriter(SRC_MAIN_RESOURCES_DISCORD_DUMP_JSON)) {
                        gson.toJson(channels, writer);
                    }

                    System.out.println(
                            "Added message to JSON: "
                                    + message.getContentRaw()
                    );

                    return;
                }
            }

            // Channel is not in JSON yet
            Map<String, Object> newChannel = getStringObjectMap(textChannel, message);
            channels.add(newChannel);

            try (FileWriter writer = new FileWriter(SRC_MAIN_RESOURCES_DISCORD_DUMP_JSON)) {
                gson.toJson(channels, writer);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static @NonNull Map<String, Object> getStringObjectMap(TextChannel textChannel, Message message) {
        Map<String, Object> newChannel = new LinkedHashMap<>();
        newChannel.put("id", textChannel.getId());
        newChannel.put("name", textChannel.getName());

        List<Map<String, Object>> messages = new ArrayList<>();

        Map<String, Object> newMessage = new LinkedHashMap<>();
        newMessage.put("id", message.getId());
        newMessage.put("author", message.getAuthor().getName());
        newMessage.put("content", message.getContentRaw());

        messages.add(newMessage);

        newChannel.put("messages", messages);
        return newChannel;
    }

    public static synchronized void deleteMessage(String channelId, String messageId) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {

            java.lang.reflect.Type type =
                    new com.google.gson.reflect.TypeToken<
                            List<Map<String, Object>>>() {}.getType();

            List<Map<String, Object>> channels;

            try (java.io.FileReader reader = new java.io.FileReader(SRC_MAIN_RESOURCES_DISCORD_DUMP_JSON)) {
                channels = gson.fromJson(reader, type);
            }

            if (channels == null) {
                return;
            }

            for (Map<String, Object> channel : channels) {

                if (!channelId.equals(
                        String.valueOf(channel.get("id"))
                )) {
                    continue;
                }

                List<Map<String, Object>> messages = (List<Map<String, Object>>) channel.get("messages");

                if (messages == null) {
                    return;
                }

                boolean removed = messages.removeIf(message ->
                        messageId.equals(
                                String.valueOf(message.get("id"))
                        )
                );

                if (!removed) {
                    return;
                }

                try (FileWriter writer = new FileWriter(SRC_MAIN_RESOURCES_DISCORD_DUMP_JSON)) {
                    gson.toJson(channels, writer);
                }

                System.out.println(
                        "Deleted message from JSON: " + messageId
                );

                return;
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not delete message from JSON", e);
        }
    }
}
