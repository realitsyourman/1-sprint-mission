package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentSControllerV2 {

  private final BinaryContentService binaryContentService;

  /**
   * 첨부파일 조회
   */
  @GetMapping("/{binaryContentId}")
  public BinaryContentResponse findFile(@PathVariable("binaryContentId") String binaryContentId) {
    return binaryContentService.find(binaryContentId);
  }
}
