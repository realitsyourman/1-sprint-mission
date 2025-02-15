package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.binarycontent.BinaryContentServiceManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/bin")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;
    private final BinaryContentServiceManager fileManager;

    @ResponseBody
    @RequestMapping(value = "/{fileName}", method = RequestMethod.GET)
    public Resource showImage(@PathVariable("fileName") String fileName) throws MalformedURLException {
        return new UrlResource("file:" + fileManager.getFullPath(fileName));
    }

    // 파일 1개 다운로드
    @RequestMapping(value = "/download/{fileId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> findFile(@PathVariable("fileId") UUID fileId) throws MalformedURLException {
        BinaryContent findFile = binaryContentService.findBinaryContentById(fileId);

        String savedFileName = findFile.getUploadFile().getSavedFileName();
        String uploadFileName = findFile.getUploadFile().getUploadFileName();

        UrlResource urlResource = new UrlResource("file:" + fileManager.getFullPath(savedFileName));
        log.warn("uploadFileName:{}", uploadFileName);

        // 파일 이름 인코딩
        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);

        String disposition = "attach; filename=\"" + encodedUploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                .body(urlResource);
    }
}
