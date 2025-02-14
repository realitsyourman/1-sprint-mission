package com.sprint.mission.discodeit.entity.message;

import jakarta.validation.constraints.NotBlank;

public record MessageCreateRequest(

        @NotBlank
        String title,
        String content,

        @NotBlank
        String sender,

        @NotBlank
        String receiver
) {
}
