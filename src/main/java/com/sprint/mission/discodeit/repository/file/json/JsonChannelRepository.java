package com.sprint.mission.discodeit.repository.file.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JsonChannelRepository implements ChannelRepository {
    private static final String CHANNEL_PATH = "channels.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<UUID, Channel> channelMap = new HashMap<>();

    @Override
    public Channel saveChannel(Channel channel) {
        channelMap.put(channel.getChannelId(), channel);
        saveToJson();
        return channel;
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        loadFromJson();
        return channelMap.get(channelId);
    }

    @Override
    public Map<UUID, Channel> findAllChannel() {
        loadFromJson();
        return channelMap;
    }

    @Override
    public void removeChannelById(UUID channelId) {
        channelMap.remove(channelId);
        saveToJson();
    }

    private void saveToJson() {
        try {
            objectMapper.writeValue(new File(CHANNEL_PATH), channelMap);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save channels to JSON", e);
        }
    }

    private void loadFromJson() {
        File file = new File(CHANNEL_PATH);
        if (file.exists()) {
            try {
                channelMap = objectMapper.readValue(file,
                        new TypeReference<HashMap<UUID, Channel>>() {});
            } catch (IOException e) {
                throw new RuntimeException("Failed to load channels from JSON", e);
            }
        }
    }
}
