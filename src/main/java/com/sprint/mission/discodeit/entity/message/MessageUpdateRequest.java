package com.sprint.mission.discodeit.entity.message;

import java.util.UUID;

public record MessageUpdateRequest (
        UUID updateMessageId,
        String newTitle,
        String newContent
) {
}
