package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentCreateRequest;

import java.util.Map;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent create(BinaryContentCreateRequest request);

    default BinaryContent update(BinaryContent binaryContent) {
        return null;
    }

    BinaryContent find(UUID id);

    Map<UUID, BinaryContent> findAllById(UUID id);

    default Map<UUID, BinaryContent> findByUserId(UUID userId) {
        return null;
    }

    void delete(UUID id);
}
