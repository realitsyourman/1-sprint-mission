package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.user.UserLoginRequest;
import com.sprint.mission.discodeit.exception.user.IllegalUserException;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
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
                .orElseThrow(() -> {
                    log.error("아이디 또는 비밀번호가 잘못되었습니다.");

                    return new IllegalUserException("userAuth login error!!");
                });
    }
}
