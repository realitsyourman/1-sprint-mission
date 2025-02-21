package com.sprint.mission.discodeit.service.binarycontent;

import com.sprint.mission.discodeit.entity.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.entity.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.binarycontent.UploadBinaryContent;
import com.sprint.mission.discodeit.exception.binary.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
      uploadFiles.add(uploadFile);
    } else if (request.getFiles() != null && !request.getFiles().isEmpty()) { // 다중 파일 처리
      uploadFiles.addAll(fileManager.saveFiles(request));
    }

//    UUID fileId = convertToUUID(uploadFiles.get(0).getSavedFileName());
    String savedFileName = uploadFiles.get(0).getSavedFileName();
    int index = savedFileName.lastIndexOf(".");

    UUID fileId = UUID.fromString(savedFileName.substring(0, index));

    BinaryContent binaryContent = BinaryContent.builder()
        .fileId(fileId)
        .binaryContentName(request.getFileName())
        .uploadFile(uploadFiles.get(0))
        .uploadFiles(uploadFiles)
        .build();

    binaryContentRepository.save(binaryContent);

    return uploadFiles;
  }


  /**
   * 스프린트 미션 5, 메세지에 파일 넣어서 전송
   */
  @Override
  public List<UploadBinaryContent> createFiles(List<MultipartFile> files, UUID messageId) {
    List<UploadBinaryContent> uploadFiles = files.stream()
        .map(file -> {
          try {
            return fileManager.saveFile(new BinaryContentRequest(
                file.getOriginalFilename(),
                file,
                files
            ));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .toList();

    //메세지 아이디, 사이즈, contentType, bytes도 만들어서 넣어야함
    uploadFiles.forEach(
        file -> binaryContentRepository.save(
            BinaryContent.createBinaryContent(messageId, file.getSavedFileName(), file.getSize(),
                file.getContentType(), file.getBytes(), file, uploadFiles)
        )
    );

    return uploadFiles;
  }

  @Override
  public BinaryContentResponse find(String fileName) {
    UUID id = UUID.fromString(fileName.substring(0, fileName.lastIndexOf(".")));

    BinaryContent findFile = binaryContentRepository.findById(id);

    if (findFile == null) {
      throw new BinaryContentNotFoundException();
    }

    return new BinaryContentResponse(
        findFile.getFileId(),
        findFile.getCreatedAt(),
        findFile.getBinaryContentName(),
        findFile.getUploadFile().getSize(),
        findFile.getUploadFile().getSavedFileName()
            .substring(findFile.getUploadFile().getSavedFileName().lastIndexOf(".") + 1),
        findFile.getUploadFile().getBytes()
    );
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

    return findAllFiles.values().stream()
        .filter(file -> file.getFileId().equals(id))
        .map(f -> new BinaryContentResponse(f.getFileId(), f.getCreatedAt(),
            f.getBinaryContentName(), f.getSize(), f.getContentType(), f.getBytes()))
        .toList();
//        .filter(entry -> entry.getKey().equals(id))
//        .flatMap(entry -> entry.getValue().getUploadFiles().stream())
//        .map(uploadFile -> new BinaryContentResponse(id, uploadFile.getSavedFileName()))
//        .toList();
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
