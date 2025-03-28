package com.sprint.mission.discodeit.entity.status.read;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusRequest(
    @NotBlank
    UUID userId,

    @NotBlank
    UUID channelId,

    @NotBlank
    Instant lastReadAt
) {

}
