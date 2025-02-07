package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.status.user.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> storage = new HashMap<>();

    @Override
    public UserStatus save(UserStatus userStatus) {
        if (userStatus == null) {
            throw new IllegalArgumentException("UserStatus cannot be null");
        }
        storage.put(userStatus.getUserId(), userStatus);
        return userStatus;
    }

    @Override
    public UserStatus findById(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        UserStatus userStatus = storage.get(userId);
        if (userStatus == null) {
            throw new IllegalStateException("UserStatus not found for userId: " + userId);
        }
        return userStatus;
    }

    @Override
    public Map<UUID, UserStatus> findAll() {
        // 불변 Map을 반환하여 외부에서 직접 수정하는 것을 방지
        return Collections.unmodifiableMap(new HashMap<>(storage));
    }

    @Override
    public UserStatus updateState(UUID userId, UserStatus userStatus) {
        if (userId == null || userStatus == null) {
            throw new IllegalArgumentException("UserId and UserStatus cannot be null");
        }
        if (!storage.containsKey(userId)) {
            throw new IllegalStateException("UserStatus not found for userId: " + userId);
        }
        storage.put(userId, userStatus);
        return userStatus;
    }

    @Override
    public void remove(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        if (!storage.containsKey(userId)) {
            throw new IllegalStateException("UserStatus not found for userId: " + userId);
        }
        storage.remove(userId);
    }
}