package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.user.UserLoginRequest;
import com.sprint.mission.discodeit.exception.user.IllegalUserException;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public User login(UserLoginRequest loginInfo) {
        Map<UUID, User> users = userRepository.findAllUser();

        return users.values().stream()
                .filter(allusers -> allusers.getUserName().equals(loginInfo.userName()))
                .filter(sameNameUser -> sameNameUser.getUserPassword().equals(loginInfo.userPassword()))
                .findFirst()
                .orElseThrow(IllegalUserException::new);
    }
}
