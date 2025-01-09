package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;

public class JCFUserService implements UserService {
    private final List<User> userList;

    public JCFUserService(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public User createUser(User createUser) {
        userList.add(createUser);
        return createUser;
    }

    @Override
    public void readUserInfo(User readUser) {
        System.out.println("유저 이름: " + readUser.getUserName());
        System.out.println("유저 이메일: " + readUser.getUserEmail());
    }

    @Override
    public void readAllUsers() {
        userList.forEach(this::readUserInfo);
    }

    @Override
    public User updateUser(User exUser ,User updateUser) {
        User findUser = userList.stream()
                .filter(users -> users.getUserId().equals(exUser.getUserId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않음"));

        findUser.updateName(updateUser.getUserName());
        findUser.updateEmail(updateUser.getUserEmail());
        findUser.updatePassword(updateUser.getUserPassword());

        deleteUser(exUser);

        return findUser;
    }

    @Override
    public User deleteUser(User deleteUser) {
        userList.remove(deleteUser);
        return deleteUser;
    }
}