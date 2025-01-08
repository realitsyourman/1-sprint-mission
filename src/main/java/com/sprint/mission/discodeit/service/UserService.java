package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public interface UserService {
    User createUser(User createUser);
    User readUserInfo(User readUser);
    List<User> readAllUsers();
    User updateUser(User updateUser);
    void kickUser(User kickUser);
}
