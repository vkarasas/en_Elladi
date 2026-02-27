package com.enElladi.services.impl;

import com.enElladi.models.Channel;
import com.enElladi.services.MessageService;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Override
    public List<Channel> listOfChannels()  {
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("discord_dump.json")) {
            if (is == null) throw new IllegalStateException("discord_dump.json not found in resources");
            return mapper.readValue(is, new TypeReference<>(){});
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return new ArrayList<>();
    }
}
