package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class JCFUserRepository implements UserRepository {

    private Map<UUID, User> userMap;

    public JCFUserRepository() {
        this.userMap = new HashMap<>();
    }

    public JCFUserRepository(Map<UUID, User> userMap) {
        this.userMap = userMap;
    }

    @Override
    public User userSave(User user) {
        userMap.put(user.getId(), user);


        return userMap.get(user.getId());
    }

    @Override
    public User findUserById(UUID userId) {
        return userMap.get(userId);
    }

    @Override
    public Map<UUID, User> findAllUser() {
        return userMap;
    }

    @Override
    public void removeUserById(UUID userId) {
        userMap.remove(userId);
    }
}
