package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.role.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserAuthService implements com.sprint.mission.discodeit.service.auth.LoginRequest {

  private final UserService userService;
  private final UserRepository userRepository;
  private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;


  /**
   * @methodName : updateRole
   * @date : 2025. 5. 19. 15:42
   * @author : wongil
   * @Description: 사용자 역할 수정
   **/
  @Override
  @Transactional
  public void updateRole(RoleUpdateRequest request, HttpServletRequest httpRequest) {

    User user = userRepository.findById(request.userId()).orElseThrow(
        () -> new UserNotFoundException(Instant.now(), ErrorCode.USER_NOT_FOUND,
            Map.of(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage())));

    user.changeRole(request.newRole());

    if (user.isThereHere()) {
      Map<String, ? extends Session> sessions = sessionRepository.findByIndexNameAndIndexValue(
          FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME,
          user.getUsername()
      );

      sessions.keySet()
          .forEach(session -> sessionRepository.deleteById(session));
    }
  }

  public UserDto sessionMe(HttpSession session) {

    SecurityContext context = (SecurityContext) session.getAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
    if (context == null || context.getAuthentication() == null || !context.getAuthentication()
        .isAuthenticated()) {
      return null;
    }

    Authentication authentication = context.getAuthentication();
    return userService.findByUsername(authentication.getName());
  }
}
