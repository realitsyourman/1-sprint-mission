package com.sprint.mission.discodeit.entity.user;

import java.util.UUID;

public record UserCreateWithBinaryContentRequest(
        UUID userId,
        UUID messageId,
        String fileName,
        String fileType
) {
}
