package com.sprint.mission.discodeit.entity.binarycontent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 업로드 파일 정보 저장 객체
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UploadBinaryContent {
    private UUID requestUserId;
    private String uploadFileName;
    private String savedFileName;

    public UUID updateUserId(UUID requestUserId) {
        this.requestUserId = requestUserId;

        return requestUserId;
    }
}
