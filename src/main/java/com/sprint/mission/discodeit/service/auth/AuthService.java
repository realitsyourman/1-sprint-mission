package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.user.UserLoginRequest;

public interface AuthService {
    User login(UserLoginRequest loginInfo);
}
