package com.enElladi.listeners;

import com.enElladi.config.MessageFetcher;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class MessageListener extends ListenerAdapter {

    private final List<GuildChannel> channels;

    public MessageListener(List<GuildChannel> channels) {
        this.channels = channels;
    }

    /**
     * Listener method for discord server bot
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;

        if (!(event.getChannel() instanceof TextChannel textChannel)) {
            return;
        }

        Message message = event.getMessage();

        MessageFetcher.addNewMessage(
                textChannel,
                message
        );
    }

    @Override
    public void onMessageDelete(@NonNull MessageDeleteEvent event) {
        String messageId = event.getMessageId();
        String channelId = event.getChannel().getId();

        MessageFetcher.deleteMessage(channelId, messageId);
    }

    @Override
    public void onChannelCreate(@NonNull ChannelCreateEvent event) {
        if(event.getChannel() instanceof TextChannel textChannel) {
            MessageFetcher.addNewChannel(textChannel);
        }
    }
}
