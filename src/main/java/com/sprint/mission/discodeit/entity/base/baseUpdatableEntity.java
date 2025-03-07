package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
public abstract class baseUpdatableEntity extends BaseEntity {

  @LastModifiedDate
  private Instant updatedAt;

}
