package com.sprint.mission.discodeit.repository.file.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sprint.mission.discodeit.entity.status.user.UserStatus;
import com.sprint.mission.discodeit.exception.user.IllegalUserException;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
public class JsonUserStatusRepository implements UserStatusRepository {

    private static final String USER_PATH = "usersStatus.json";
    private final ObjectMapper objectMapper;
    private Map<UUID, UserStatus> userStatusMap = new HashMap<>();

    public JsonUserStatusRepository() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        if (userStatus == null) {
            throw new IllegalUserException("유저를 저장하는데 문제가 생겼습니다.");
        }

        userStatusMap.put(userStatus.getUserId(), userStatus);
        saveToJson();
        return userStatusMap.get(userStatus.getUserId());
    }

    @Override
    public UserStatus findById(UUID userId) {
         return userStatusMap.get(userId);
    }

    @Override
    public Map<UUID, UserStatus> findAll() {
        return userStatusMap;
    }

    @Override
    public UserStatus updateState(UUID userId, UserStatus userStatus) {
        UserStatus findState = findById(userId);
        if (findState == null) {
            throw new IllegalUserException("updateState 실패");
        }

        findState.updateUserStatus();

        userStatusMap.put(findState.getUserId(), findState);

        return userStatusMap.get(findState.getUserId());
    }

    @Override
    public void remove(UUID userId) {
        if(userId == null) {
            throw new IllegalUserException("삭제할 UserStatus가 없습니다.");
        }

        userStatusMap.remove(userId);
    }

    private void saveToJson() {
        try {
            objectMapper.writeValue(new File(USER_PATH), userStatusMap);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save users to JSON", e);
        }
    }

    private void loadFromJson() {
        File file = new File(USER_PATH);
        if (file.exists()) {
            try {
                userStatusMap = objectMapper.readValue(file,
                        new TypeReference<HashMap<UUID, UserStatus>>() {});
            } catch (IOException e) {
                throw new RuntimeException("Failed to load users from JSON", e);
            }
        }
    }
}
