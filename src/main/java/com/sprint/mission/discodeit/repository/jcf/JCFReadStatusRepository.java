package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.status.read.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFReadStatusRepository implements ReadStatusRepository {

    private final Map<UUID, ReadStatus> storage = new HashMap<>();

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        if (readStatus == null) {
            throw new IllegalArgumentException("ReadStatus cannot be null");
        }
        storage.put(readStatus.getChannelId(), readStatus);
        return readStatus;
    }

    @Override
    public ReadStatus findByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }

        return storage.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("ReadStatus not found for userId: " + userId));
    }

    @Override
    public ReadStatus findByChannelId(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("ChannelId cannot be null");
        }

        ReadStatus readStatus = storage.get(channelId);
        if (readStatus == null) {
            throw new IllegalStateException("ReadStatus not found for channelId: " + channelId);
        }
        return readStatus;
    }

    @Override
    public Map<UUID, ReadStatus> findAll() {
        // 불변 Map을 반환하여 외부에서 직접 수정하는 것을 방지
        return Collections.unmodifiableMap(new HashMap<>(storage));
    }

    @Override
    public void remove(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("ChannelId cannot be null");
        }

        if (!storage.containsKey(channelId)) {
            throw new IllegalStateException("ReadStatus not found for channelId: " + channelId);
        }

        storage.remove(channelId);
    }
}