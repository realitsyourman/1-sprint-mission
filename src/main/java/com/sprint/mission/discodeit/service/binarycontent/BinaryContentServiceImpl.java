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
import java.util.*;

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
            UploadBinaryContent uploadFile = fileManager.saveFile(request);
            //uploadFile.updateUserId(request.getRequestUserId());
            uploadFiles.add(uploadFile);
        }

        // 다중 파일 처리
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            uploadFiles.addAll(fileManager.saveFiles(request));
        }

        if (uploadFiles.isEmpty()) {
            throw new BinaryContentException("업로드 할 파일이 없습니다.");
        }

        UUID fileId = convertToUUID(uploadFiles.get(0).getSavedFileName());


        BinaryContent binaryContent = new BinaryContent(
                fileId,
                request.getFileName(),
                uploadFiles.get(0),
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

    @Override
    public BinaryContent findBinaryContentById(UUID id) {
        BinaryContent findFile = binaryContentRepository.findById(id);

        if (findFile == null) {
            throw new BinaryContentNotFoundException();
        }

        return findFile;
    }

    @Override
    public Optional<BinaryContent> findBinaryContentByUserId(UUID userId) {
        return binaryContentRepository.findAll().values().stream()
                .filter(bin -> bin.getUploadFile().getRequestUserId().equals(userId))
                .findFirst();
    }

    // id로 검색

    @Override
    public List<BinaryContentResponse> findAllById(UUID id) {
        Map<UUID, BinaryContent> findAllFiles = binaryContentRepository.findAll();

        return findAllFiles.entrySet().stream()
                .filter(entry -> entry.getKey().equals(id))
                .flatMap(entry -> entry.getValue().getUploadFiles().stream())
                .map(uploadFile -> new BinaryContentResponse(id, uploadFile.getSavedFileName()))
                .toList();
    }

    @Override
    public UUID delete(UUID id) {
        binaryContentRepository.delete(id);

        return id;
    }

    public static UUID convertToUUID(String fileId) {
        int idx = fileId.lastIndexOf(".");
        String fileName = fileId.substring(0, idx);

        // 표준 UUID 형식으로 변환
        String formattedUUID = new StringBuilder(36)
                .append(fileName.substring(0, 8))
                .append('-')
                .append(fileName.substring(8, 12))
                .append('-')
                .append(fileName.substring(12, 16))
                .append('-')
                .append(fileName.substring(16, 20))
                .append('-')
                .append(fileName.substring(20))
                .toString();

        // 문자열을 UUID 객체로 변환
        return UUID.fromString(formattedUUID);
    }
}
