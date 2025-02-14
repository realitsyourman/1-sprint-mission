package com.sprint.mission.discodeit.entity.message;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record MessageCreateRequest(
        UUID channelId,

        @NotBlank
        String title,
        String content,

        @NotBlank
        String sender,

        @NotBlank
        String receiver
) {
}
