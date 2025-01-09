package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final List<User> userList;

    public JCFUserService(List<User> userList) {
        this.userList = userList;
    }

    public JCFUserService() {
        userList = new ArrayList<>();
    }

    @Override
    public User createUser(String userName, String userEmail, String userPassword) {
        User user = new User(userName, userEmail, userPassword);
        userList.add(user);

        return user;
    }

    @Override
    public User getUserById(UUID userId) {
        return userList.stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("찾는 유저가 없습니다."));
    }

    @Override
    public List<User> getAllUsers() {
        return userList.stream().toList();
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
        if(removeUser != null) {
            userList.remove(removeUser);
        }
        else {
            throw new IllegalArgumentException("삭제할 유저가 없습니다.");
        }
    }
}