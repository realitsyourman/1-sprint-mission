package com.sprint.mission.discodeit.service.binarycontent;

import com.sprint.mission.discodeit.entity.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.entity.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.binarycontent.UploadBinaryContent;
import com.sprint.mission.discodeit.exception.binary.BinaryContentException;
import com.sprint.mission.discodeit.exception.binary.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("binaryContentService")
@RequiredArgsConstructor
public class BinaryContentServiceImpl implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentServiceManager fileManager;

    @Override
    public List<UploadBinaryContent> create(BinaryContentRequest request) throws IOException {
        List<UploadBinaryContent> uploadFiles = new ArrayList<>();

        // 단일 파일 처리
        if (request.getFile() != null) {
            UploadBinaryContent uploadFile = fileManager.saveFile(request.getFile());
            uploadFiles.add(uploadFile);
        }

        // 다중 파일 처리
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            uploadFiles.addAll(fileManager.saveFiles(request.getFiles()));
        }

        if (uploadFiles.isEmpty()) {
            throw new BinaryContentException("No files were provided for upload");
        }

        BinaryContent binaryContent = new BinaryContent(
                UUID.randomUUID(),
                request.getFileName(),
                uploadFiles.get(0),  // 첫 번째 파일을 대표 파일로
                uploadFiles
        );

        binaryContentRepository.save(binaryContent);

        return uploadFiles;
    }

    @Override
    public BinaryContentResponse find(UUID id) {
        BinaryContent findFile = binaryContentRepository.findById(id);

        if (findFile == null) {
            throw new BinaryContentNotFoundException();
        }

        return new BinaryContentResponse(findFile.getFileId(), findFile.getBinaryContentName());
    }

    // id로 검색
    @Override
    public List<BinaryContentResponse> findAllById(UUID id) {
        Map<UUID, BinaryContent> findAllFiles = binaryContentRepository.findAll();

        return findAllFiles.values().stream()
                .filter(upload -> upload.getFileId().equals(id))
                .map(file -> new BinaryContentResponse(file.getFileId(), file.getBinaryContentName()))
                .toList();
    }

    @Override
    public UUID delete(UUID id) {
        binaryContentRepository.delete(id);

        return id;
    }
}
