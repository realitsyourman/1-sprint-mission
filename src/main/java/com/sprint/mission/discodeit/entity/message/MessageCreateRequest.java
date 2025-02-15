package com.sprint.mission.discodeit.entity.message;

import java.util.UUID;

public record MessageCreateRequest(
        String title,
        String content,
        String sender,
        String receiver,
        UUID channelId
) {
}
