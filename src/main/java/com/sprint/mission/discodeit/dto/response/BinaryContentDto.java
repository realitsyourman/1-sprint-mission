package com.sprint.mission.discodeit.dto.response;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record BinaryContentDto(
    UUID id,
    String fileName,
    Long size,

    @NotBlank
    String contentType
) {

}
