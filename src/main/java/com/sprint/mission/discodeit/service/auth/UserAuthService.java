package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.entity.auth.RequestLogin;
import com.sprint.mission.discodeit.entity.auth.ResponseLogin;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.exception.user.IllegalUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthService implements AuthService {

  private final UserRepository userRepository;

  /**
   * 스프린트 미션 5, 로그인
   */
  @Override
  public ResponseLogin login(RequestLogin request) {
    User findUser = userRepository.findUserByUsername(request.username());

    if (findUser == null) {
      throw new UserNotFoundException(request.username());
    }

    if (!findUser.getUsername().equals(request.username()) || !findUser.getPassword()
        .equals(request.password())) {
      throw new IllegalUserException("아이디 또는 비밀버호가 잘못 되었습니다.");
    }

    return ResponseLogin.builder()
        .id(findUser.getId())
        .username(findUser.getUsername())
        .email(findUser.getEmail())
        .profile(BinaryContentMapper.toDto(findUser.getProfile()))
        .online(true)
        .build();
  }
}
