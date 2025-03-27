package com.sprint.mission.discodeit.entity.channel;

import com.sprint.mission.discodeit.entity.base.baseUpdatableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "channels")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends baseUpdatableEntity {

  private String name;
  private String description;

  @Enumerated(EnumType.STRING)
  private ChannelType type;

  public Channel modifying(String name, String description) {
    this.name = name;
    this.description = description;

    return this;
  }
}

