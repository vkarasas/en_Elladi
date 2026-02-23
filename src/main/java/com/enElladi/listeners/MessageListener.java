package com.enElladi.listeners;

import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

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

        for(Channel channel: channels) {
            if(event.getChannel().getId().equals(channel.getId())) {
                String msg = event.getMessage().getContentRaw();
                System.out.println(channel.getName() +" "+ msg);
            }
        }
    }
}
