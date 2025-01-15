package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.factory.BaseEntityFactory;
import com.sprint.mission.discodeit.factory.EntityFactory;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final Map<UUID, User> userList;
    private final EntityFactory entityFactory;

    public JCFUserService(EntityFactory entityFactory, Map<UUID, User> userList) {
        this.entityFactory = entityFactory;
        this.userList = new HashMap<>(userList);
    }

    public JCFUserService() {
        this.entityFactory = new BaseEntityFactory();
        userList = new HashMap<>();
    }

    @Override
    public User createUser(String userName, String userEmail, String userPassword) {
        User user = entityFactory.createUser(userName, userEmail, userPassword);
        userList.put(user.getUserId(), user);

        return user;
    }

    @Override
    public User getUserById(UUID userId) {
        return userList.entrySet().stream()
                .filter(entry -> entry.getKey().equals(userId))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new IllegalAccessError("찾는 유저가 없습니다."));
    }

    @Override
    public Map<UUID, User> getAllUsers() {
        return new HashMap<>(userList);
    }

    @Override
    public User updateUser(UUID userId, String newName, String newEmail, String newPassword) {
        User findUser = getUserById(userId);

        findUser.updateName(newName);
        findUser.updateEmail(newEmail);
        findUser.updatePassword(newPassword);

        return findUser;
    }

    @Override
    public void deleteUser(UUID userId) {
        User removeUser = getUserById(userId);
        if (removeUser != null) {
            userList.remove(removeUser.getUserId());
        } else {
            throw new IllegalArgumentException("삭제할 유저가 없습니다.");
        }
    }
}