package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.factory.BaseEntityFactory;
import com.sprint.mission.discodeit.factory.EntityFactory;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileUserService implements UserService, FileService<User> {
    private static final String USER_PATH = "user.ser";

    private Map<UUID, User> userList = new HashMap<>();

    private static EntityFactory ef;

    public FileUserService(EntityFactory ef) {
        FileUserService.ef = ef;
    }

    public FileUserService() {
        ef = BaseEntityFactory.getInstance();
    }

    @Override
    public void save() {

        try (FileOutputStream fos = new FileOutputStream(USER_PATH, false);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {

            out.writeObject(userList);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<UUID, User> load() {
        HashMap<UUID, User> map = new HashMap<>();

        try (FileInputStream fos = new FileInputStream(USER_PATH);
             ObjectInputStream ois = new ObjectInputStream(fos)) {

            map = (HashMap<UUID, User>) ois.readObject();

        } catch (FileNotFoundException e) {
            return map;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return map;
    }


    @Override
    public User createUser(String userName, String userEmail, String userPassword) {
        User user = ef.createUser(userName, userEmail, userPassword);
        userList.put(user.getUserId(), user);

        save();

        return user;
    }

    @Override
    public User getUserById(UUID userId) {
        return userList.get(userId);
    }

    @Override
    public Map<UUID, User> getAllUsers() {
        return new HashMap<>(userList);
    }

    @Override
    public User updateUser(UUID userId, String newName, String newEmail, String newPassword) {
        User findUser = getUserById(userId);

        if (findUser == null) {
            throw new IllegalArgumentException("찾는 유저가 없습니다");
        } else {
            findUser.updateName(newName);
            findUser.updateEmail(newEmail);
            findUser.updatePassword(newPassword);
        }

        save();

        return findUser;
    }

    @Override
    public void deleteUser(UUID userId) {
        userList.remove(userId);

        save();
    }

}
