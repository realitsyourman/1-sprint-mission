package com.sprint.mission.discodeit.redis;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public abstract class RedisRememberMeTokenMixin {

  @JsonCreator
  public RedisRememberMeTokenMixin(
      @JsonProperty("username") String username,
      @JsonProperty("series") String series,
      @JsonProperty("tokenValue") String tokenValue,
      @JsonProperty("date") Date date) {

  }
}
