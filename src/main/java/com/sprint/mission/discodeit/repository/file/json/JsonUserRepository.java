package com.sprint.mission.discodeit.repository.file.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JsonUserRepository implements UserRepository {
    private static final String USER_PATH = "users.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<UUID, User> userMap = new HashMap<>();

    @Override
    public User userSave(User user) {
        userMap.put(user.getUserId(), user);
        saveToJson();
        return user;
    }

    @Override
    public User findUserById(UUID userId) {
        loadFromJson();
        return userMap.get(userId);
    }

    @Override
    public Map<UUID, User> findAllUser() {
        loadFromJson();
        return userMap;
    }

    @Override
    public void removeUserById(UUID userId) {
        userMap.remove(userId);
        saveToJson();
    }

    private void saveToJson() {
        try {
            objectMapper.writeValue(new File(USER_PATH), userMap);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save users to JSON", e);
        }
    }

    private void loadFromJson() {
        File file = new File(USER_PATH);
        if (file.exists()) {
            try {
                userMap = objectMapper.readValue(file,
                        new TypeReference<HashMap<UUID, User>>() {});
            } catch (IOException e) {
                throw new RuntimeException("Failed to load users from JSON", e);
            }
        }
    }
}
