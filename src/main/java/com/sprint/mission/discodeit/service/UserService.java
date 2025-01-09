package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

public interface UserService {
    User createUser(User createUser);
    void readUserInfo(User readUser);
    void readAllUsers();
    User updateUser(User exUser, User updateUser);
    User deleteUser(User deleteUser);
}
