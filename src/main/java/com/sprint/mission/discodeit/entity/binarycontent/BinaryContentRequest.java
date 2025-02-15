package com.sprint.mission.discodeit.entity.binarycontent;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * multipart file을 받아올 클래스
 */

@Getter
@AllArgsConstructor
public class BinaryContentRequest {
    @NotBlank
    private String fileName;
    private MultipartFile file;
    private List<MultipartFile> files;
}
