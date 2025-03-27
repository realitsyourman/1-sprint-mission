package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class UserExistsException extends UserException {

  public UserExistsException(Instant timestamp, ErrorCode errorCode, Map<String, Object> details) {
    super(timestamp, errorCode, details);
  }
}
