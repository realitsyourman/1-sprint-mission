package com.sprint.mission.discodeit.entity.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessageCreateRequest(
    @NotBlank
    String content,

    @NotNull
    UUID channelId,

    @NotNull
    UUID authorId
) {

}
