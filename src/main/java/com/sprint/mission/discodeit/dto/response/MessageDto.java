package com.sprint.mission.discodeit.dto.response;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
    UUID id,
    Instant createAt,
    Instant updatedAt,
    String content,

    @NotNull
    UUID channelId,

    UserDto author,
    List<BinaryContentDto> attachments
) {

}
