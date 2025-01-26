package com.sprint.mission.discodeit.repository.file.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JsonMessageRepository implements MessageRepository {
    private static final String MESSAGE_PATH = "messages.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<UUID, Message> messageMap = new HashMap<>();

    @Override
    public Message saveMessage(Message message) {
        messageMap.put(message.getMessageId(), message);
        saveToJson();
        return message;
    }

    @Override
    public Message findMessageById(UUID messageId) {
        loadFromJson();
        return messageMap.get(messageId);
    }

    @Override
    public Map<UUID, Message> findAllMessage() {
        loadFromJson();
        return messageMap;
    }

    @Override
    public void removeMessageById(UUID messageId) {
        messageMap.remove(messageId);
        saveToJson();
    }

    private void saveToJson() {
        try {
            objectMapper.writeValue(new File(MESSAGE_PATH), messageMap);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save messages to JSON", e);
        }
    }

    private void loadFromJson() {
        File file = new File(MESSAGE_PATH);
        if (file.exists()) {
            try {
                messageMap = objectMapper.readValue(file,
                        new TypeReference<HashMap<UUID, Message>>() {});
            } catch (IOException e) {
                throw new RuntimeException("Failed to load messages from JSON", e);
            }
        }
    }
}
