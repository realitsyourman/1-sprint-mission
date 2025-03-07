package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path path;

  public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String path) {
    this.path = Paths.get(path);
  }

  /**
   * 디렉토리 초기화
   */
  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(path);
    } catch (IOException e) {
      throw new RuntimeException("local storage init fail: ", e);
    }
  }

  /**
   * 파일의 실제 저장 위치에 대한 규칙을 정의합니다. 파일 저장 위치 규칙 예시: {root}/{UUID} put, get 메소드에서 호출해 일관된 파일 경로 규칙을
   * 유지합니다.
   */
  @Override
  public Path resolvePath(UUID id) {
    return path.resolve(id.toString());
  }

  /**
   * UUID 키 정보를 바탕으로 byte[] 데이터를 저장합니다. UUID는 BinaryContent의 Id 입니다.
   */
  @Override
  public UUID put(UUID fileId, byte[] bytes) {
    Path filePath = resolvePath(fileId);
    try {
      Files.write(filePath, bytes);
    } catch (IOException e) {
      throw new RuntimeException("파일 저장 실패: ", e);
    }

    return fileId;
  }

  /**
   * 키 정보를 바탕으로 byte[] 데이터를 읽어 InputStream 타입으로 반환합니다. UUID는 BinaryContent의 Id 입니다.
   */
  @Override
  public InputStream get(UUID fileId) {
    Path filePath = resolvePath(fileId);

    try {
      return Files.newInputStream(filePath);
    } catch (IOException e) {
      throw new RuntimeException("파일 읽기 실패: ", e);
    }

  }

  /**
   * HTTP API로 다운로드 기능을 제공합니다. BinaryContentDto 정보를 바탕으로 파일을 다운로드할 수 있는 응답을 반환합니다.
   */
  @Override
  public ResponseEntity<?> download(BinaryContentDto file) {
    InputStream findFileStream = get(file.id());

    InputStreamResource fileResource = new InputStreamResource(findFileStream);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.fileName() + "\"")
        .contentLength(file.size())
        .contentType(MediaType.parseMediaType(file.contentType()))
        .body(fileResource);
  }
}
