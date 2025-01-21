package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.factory.BaseEntityFactory;
import com.sprint.mission.discodeit.factory.EntityFactory;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.Map;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private static final EntityFactory entityFactory = BaseEntityFactory.getInstance();

    public BasicUserService(UserRepository userRepository, ChannelRepository channelRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public User createUser(String userName, String userEmail, String userPassword) {
        User user = entityFactory.createUser(userName, userEmail, userPassword);

        userRepository.userSave(user);

        return user;
    }

    @Override
    public User getUserById(UUID userId) {
        return userRepository.findUserById(userId);
    }

    @Override
    public Map<UUID, User> getAllUsers() {
        return userRepository.findAllUser();
    }

    @Override
    public User updateUser(UUID userId, String newName, String newEmail, String newPassword) {
        User findUser = userRepository.findUserById(userId);

        findUser.updateName(newName);
        findUser.updateEmail(newEmail);
        findUser.updatePassword(newPassword);

        User updateUser = userRepository.userSave(findUser);

        // 유저의 정보가 업데이트 되면 채널에 있는 유저의 정보도 업데이트 되어야함

        return updateUser;
    }

    @Override
    public void deleteUser(UUID userId) {
        userRepository.removeUserById(userId);
    }
}
