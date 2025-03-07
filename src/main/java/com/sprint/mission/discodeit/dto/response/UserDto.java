package com.sprint.mission.discodeit.dto.response;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record UserDto(
    UUID id,

    @NotBlank
    String username,

    @NotBlank
    String email,

    BinaryContentDto profile,
    Boolean online
) {

}
