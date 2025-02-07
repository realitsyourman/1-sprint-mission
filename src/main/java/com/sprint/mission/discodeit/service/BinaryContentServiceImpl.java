package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.exception.BinaryContentException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("binaryContentService")
@RequiredArgsConstructor
public class BinaryContentServiceImpl implements BinaryContentService {
    private final BinaryContentRepository binaryRepository;

    @Override
    public BinaryContent create(BinaryContentCreateRequest request) {
        if (request == null) {
            throw new BinaryContentException("잘못된 request");
        }

        BinaryContent binaryContent = convertToBinaryContent(request);
        return binaryRepository.save(binaryContent);
    }

    @Override
    public BinaryContent update(BinaryContent binaryContent) {
        if (binaryContent == null) {
            throw new BinaryContentException();
        }

        return binaryRepository.save(binaryContent);
    }

    @Override
    public BinaryContent find(UUID id) {
        return getFindContent(id);
    }

    @Override
    public Map<UUID, BinaryContent> findAllById(UUID id) {
        Map<UUID, BinaryContent> binaryContentMap = binaryRepository.findAll();

        return binaryContentMap.entrySet().stream()
                .filter(entry -> entry.getKey().equals(id))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    public Map<UUID, BinaryContent> findByUserId(UUID userId) {
        Map<UUID, BinaryContent> byUserId = binaryRepository.findByUserId(userId);

        if (byUserId == null) {
            throw new BinaryContentException("userId에 대한 binary를 찾을 수 없음");
        }

        return byUserId;
    }

    @Override
    public void delete(UUID id) {
        BinaryContent findContent = getFindContent(id);

        binaryRepository.remove(id);
    }

    private static BinaryContent convertToBinaryContent(BinaryContentCreateRequest request) {
        return new BinaryContent(request.userId(), request.messageId(), request.fileName(), request.fileType());
    }


    private BinaryContent getFindContent(UUID id) {
        BinaryContent findContent = binaryRepository.findById(id);

        return findContent;
    }
}
