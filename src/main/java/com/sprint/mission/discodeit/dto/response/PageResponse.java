package com.sprint.mission.discodeit.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class PageResponse<T> {

  private List<T> content;
  private int number;
  private int size;
  private boolean hasNext;
  private Long totalElements;

}
