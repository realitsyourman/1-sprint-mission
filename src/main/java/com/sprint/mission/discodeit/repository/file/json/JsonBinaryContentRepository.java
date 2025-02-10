package com.sprint.mission.discodeit.repository.file.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sprint.mission.discodeit.entity.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.exception.BinaryContentException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class JsonBinaryContentRepository implements BinaryContentRepository {

    private static final String USER_PATH = "binaryContent.json";
    private final ObjectMapper objectMapper;
    private Map<UUID, BinaryContent> binaryContentMap = new HashMap<>();

    public JsonBinaryContentRepository() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        if (binaryContent == null) {
            throw new BinaryContentException("save 실패했습니다.");
        }

        binaryContentMap.put(binaryContent.getId(), binaryContent);
        saveToJson();

        return binaryContentMap.get(binaryContent.getId());
    }

    /**
     * @param messageId
     * @return
     * @Description: 메세지에 있는 첨부파일 찾기
     */
    @Override
    public BinaryContent findByMessageId(UUID messageId) {
        return binaryContentMap.values().stream()
                .filter(v -> v.getMessageId().equals(messageId))
                .findFirst()
                .orElseThrow(() -> new BinaryContentException("찾는 파일이 없습니다."));
    }

    /**
     * @param userId
     * @return
     * @Description: 유저가 보냈었던 파일을 찾기
     */
    @Override
    public Map<UUID, BinaryContent> findByUserId(UUID userId) {
        return binaryContentMap.entrySet().stream()
                .filter(entry -> entry.getValue().getUserId().equals(userId))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    @Override
    public BinaryContent findById(UUID id) {
        checkId(id);

        return binaryContentMap.get(id);
    }

    @Override
    public void remove(UUID id) {
        checkId(id);

        binaryContentMap.remove(id);
    }

    /**
     * @param userId
     * @return
     * @Description: 유저가 보낸 모든 파일 삭제
     */
    @Override
    public void removeAllContentOfUser(UUID userId) {
        binaryContentMap.entrySet().removeIf(entry ->
                entry.getValue().getUserId().equals(userId));
        saveToJson();
    }

    /**
     * @param messageId
     * @return
     * @Description: 유저가 보낸 메세지의 파일 삭제
     */
    @Override
    public Map<UUID, BinaryContent> removeContent(UUID messageId) {
        binaryContentMap.entrySet().removeIf(entry ->
                entry.getValue().getMessageId().equals(messageId));

        saveToJson();
        return findAll();
    }

    @Override
    public Map<UUID, BinaryContent> findAll() {
        return binaryContentMap;
    }

    private void saveToJson() {
        try {
            objectMapper.writeValue(new File(USER_PATH), binaryContentMap);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save users to JSON", e);
        }
    }

    private static void checkId(UUID id) {
        if (id == null) {
            throw new BinaryContentException("id가 유효하지 않습니다.");
        }
    }

    private void loadFromJson() {
        File file = new File(USER_PATH);
        if (file.exists()) {
            try {
                binaryContentMap = objectMapper.readValue(file,
                        new TypeReference<HashMap<UUID, BinaryContent>>() {});
            } catch (IOException e) {
                throw new RuntimeException("Failed to load users from JSON", e);
            }
        }
    }
}
