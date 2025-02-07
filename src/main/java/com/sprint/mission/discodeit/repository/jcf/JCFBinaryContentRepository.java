package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> storage = new HashMap<>();

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        if (binaryContent == null) {
            throw new IllegalArgumentException("BinaryContent cannot be null");
        }
        storage.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    @Override
    public BinaryContent findByMessageId(UUID messageId) {
        if (messageId == null) {
            throw new IllegalArgumentException("MessageId cannot be null");
        }

        return storage.values().stream()
                .filter(content -> content.getMessageId().equals(messageId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("BinaryContent not found for messageId: " + messageId));
    }

    @Override
    public Map<UUID, BinaryContent> findByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }

        Map<UUID, BinaryContent> result = storage.values().stream()
                .filter(content -> content.getUserId().equals(userId))
                .collect(Collectors.toMap(
                        BinaryContent::getId,
                        content -> content
                ));

        if (result.isEmpty()) {
            throw new IllegalStateException("No BinaryContent found for userId: " + userId);
        }

        return Collections.unmodifiableMap(result);
    }

    @Override
    public Map<UUID, BinaryContent> findAll() {
        return Collections.unmodifiableMap(new HashMap<>(storage));
    }

    @Override
    public BinaryContent findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        BinaryContent content = storage.get(id);
        if (content == null) {
            throw new IllegalStateException("BinaryContent not found for id: " + id);
        }

        return content;
    }

    @Override
    public void remove(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        if (!storage.containsKey(id)) {
            throw new IllegalStateException("BinaryContent not found for id: " + id);
        }

        storage.remove(id);
    }

    @Override
    public void removeAllContentOfUser(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }

        // userId에 해당하는 모든 content의 id를 수집
        List<UUID> contentIdsToRemove = storage.values().stream()
                .filter(content -> content.getUserId().equals(userId))
                .map(BinaryContent::getId)
                .collect(Collectors.toList());

        if (contentIdsToRemove.isEmpty()) {
            throw new IllegalStateException("No BinaryContent found for userId: " + userId);
        }

        // 수집된 id들을 이용해 content 삭제
        contentIdsToRemove.forEach(storage::remove);
    }

    @Override
    public Map<UUID, BinaryContent> removeContent(UUID messageId) {
        if (messageId == null) {
            throw new IllegalArgumentException("MessageId cannot be null");
        }

        // messageId에 해당하는 모든 content를 찾아서 맵으로 저장
        Map<UUID, BinaryContent> removedContents = storage.values().stream()
                .filter(content -> content.getMessageId().equals(messageId))
                .collect(Collectors.toMap(
                        BinaryContent::getId,
                        content -> content
                ));

        if (removedContents.isEmpty()) {
            throw new IllegalStateException("No BinaryContent found for messageId: " + messageId);
        }

        // 찾은 content들을 저장소에서 삭제
        removedContents.keySet().forEach(storage::remove);

        return Collections.unmodifiableMap(removedContents);
    }
}