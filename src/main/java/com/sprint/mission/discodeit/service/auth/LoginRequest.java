package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.entity.auth.ResponseLogin;

public interface LoginRequest {

  ResponseLogin login(com.sprint.mission.discodeit.entity.auth.LoginRequest request);
}
