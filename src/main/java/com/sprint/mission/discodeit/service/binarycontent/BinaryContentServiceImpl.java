package com.sprint.mission.discodeit.service.binarycontent;

import com.sprint.mission.discodeit.entity.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.entity.binarycontent.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.exception.binary.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.entitymapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service("binaryContentService")
public class BinaryContentServiceImpl implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public BinaryContentResponse find(UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(BinaryContentNotFoundException::new);

    return new BinaryContentResponse(binaryContent.getId(), binaryContent.getFileName(),
        binaryContent.getSize(), binaryContent.getContentType());
  }

  @Override
  public List<BinaryContentResponse> findAll(List<String> binaryContentIds) {
    List<UUID> binaryContentsUUIDs = binaryContentIds.stream()
        .map(UUID::fromString)
        .toList();

    return binaryContentRepository.findAllById(binaryContentsUUIDs).stream()
        .map(bin -> new BinaryContentResponse(bin.getId(), bin.getFileName(), bin.getSize(),
            bin.getContentType()))
        .toList();
  }

  @Override
  public ResponseEntity<?> downloadBinaryContent(UUID binaryContentId) {
    BinaryContent file = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(BinaryContentNotFoundException::new);

    return binaryContentStorage.download(BinaryContentMapper.toDto(file));
  }
}
