package com.sprint.mission.discodeit.service.binarycontent;

import com.sprint.mission.discodeit.entity.binarycontent.UploadBinaryContent;
import com.sprint.mission.discodeit.exception.binary.BinaryContentException;
import com.sprint.mission.discodeit.exception.binary.BinaryContentNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class BinaryContentServiceManager {
    @Value("${file.dir}")
    private String fileDir;

    public UploadBinaryContent saveFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new BinaryContentNotFoundException();
        }

        // 파일 이름 가져오기
        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename.isEmpty()) {
            throw new BinaryContentException("파일 이름이 잘못되었습니다.");
        }

        try {
            // 파일 이름을 uuid로 변환
            String savedFileName = getUploadFileName(originalFilename);

            // 저장 디렉토리 확인 및 생성
            File directory = new File(fileDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 업로드
            File destFile = new File(getFullPath(savedFileName));
            multipartFile.transferTo(destFile);

            log.info("파일 저장 완료, 파일 이름: {}", savedFileName);
            return new UploadBinaryContent(originalFilename, savedFileName);

        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", originalFilename, e);
            throw new BinaryContentException("파일 업로드 실패: " + e.getMessage());
        }
    }

    public List<UploadBinaryContent> saveFiles(List<MultipartFile> multipartFiles) throws IOException {
        if (multipartFiles == null) {
            return new ArrayList<>();
        }

        List<UploadBinaryContent> uploadList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            if (multipartFile != null && !multipartFile.isEmpty()) {
                try {
                    UploadBinaryContent uploadBinaryContent = saveFile(multipartFile);
                    uploadList.add(uploadBinaryContent);
                } catch (IOException e) {
                    log.error("Failed to save one of multiple files", e);
                    // 개별 파일 실패는 전체 업로드를 중단하지 않음
                }
            }
        }

        return uploadList;
    }

    private static String getUploadFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String ext = getExt(originalFilename);
        return uuid + "." + ext;
    }

    private static String getExt(String originalFilename) {
        int idx = originalFilename.lastIndexOf(".");
        if (idx == -1) {
            return ""; // 확장자가 없는 경우
        }
        return originalFilename.substring(idx + 1);
    }

    public String getFullPath(String fileName) {
        return Paths.get(fileDir, fileName).toString();
    }
}