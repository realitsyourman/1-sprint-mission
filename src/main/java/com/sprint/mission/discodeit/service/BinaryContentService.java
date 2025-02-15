package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.entity.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.binarycontent.UploadBinaryContent;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    List<UploadBinaryContent> create(BinaryContentRequest request) throws IOException;

    BinaryContentResponse find(UUID id);

    BinaryContent findBinaryContentById(UUID id);

    List<BinaryContentResponse> findAllById(UUID id);

    UUID delete(UUID id);
}
