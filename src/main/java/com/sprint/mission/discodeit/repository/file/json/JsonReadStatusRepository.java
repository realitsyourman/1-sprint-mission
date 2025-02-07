package com.sprint.mission.discodeit.repository.file.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sprint.mission.discodeit.entity.status.read.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class JsonReadStatusRepository implements ReadStatusRepository {
    private static final String USER_PATH = "readStatus.json";
    private final ObjectMapper objectMapper;
    private Map<UUID, ReadStatus> readStatusMap = new HashMap<>();

    public JsonReadStatusRepository() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        if (readStatus == null) {
            throw new IllegalArgumentException("readStatus가 null입니다.");
        }

        readStatusMap.put(readStatus.getChannelId(), readStatus);

        saveToJson();

        return readStatusMap.get(readStatus.getChannelId());
    }

    @Override
    public ReadStatus findByUserId(UUID userId) {
        return readStatusMap.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 채널 아이디로 찾기
     */
    @Override
    public ReadStatus findByChannelId(UUID channelId) {
        return readStatusMap.get(channelId);
    }

    @Override
    public Map<UUID, ReadStatus> findAll() {
        if(readStatusMap.isEmpty()) {
            throw new IllegalArgumentException("readStatus가 비어있음");
        }

        return readStatusMap;
    }

    @Override
    public void remove(UUID channelId) {
        readStatusMap.remove(channelId);
        saveToJson();
    }

    private void saveToJson() {
        try {
            objectMapper.writeValue(new File(USER_PATH), readStatusMap);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save users to JSON", e);
        }
    }

    private void loadFromJson() {
        File file = new File(USER_PATH);
        if (file.exists()) {
            try {
                readStatusMap = objectMapper.readValue(file,
                        new TypeReference<HashMap<UUID, ReadStatus>>() {});
            } catch (IOException e) {
                throw new RuntimeException("Failed to load users from JSON", e);
            }
        }
    }
}
