package com.sprint.mission.discodeit.entity.binarycontent;

import java.util.UUID;

public record BinaryContentResponse(
        UUID fileId,
        String fileName
) {
}
