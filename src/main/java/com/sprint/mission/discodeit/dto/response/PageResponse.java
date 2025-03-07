package com.sprint.mission.discodeit.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@ToString
@Getter
public class PageResponse<T> {

  private List<T> content;
  private int number;
  private int size;
  private boolean hasNext;
  private Long totalElements;

}
