package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.role.RoleUpdateRequest;
import com.sprint.mission.discodeit.service.auth.UserAuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Auth Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserAuthController {

  private final UserAuthService userAuthService;

  @GetMapping("/csrf-token")
  public CsrfToken publishCsrfToken(CsrfToken csrfToken) {
    return csrfToken;
  }

  /**
   * @methodName : sessionMe
   * @date : 2025. 5. 19. 15:17
   * @author : wongil
   * @Description: 세션 유지
   **/
  @GetMapping("/me")
  public UserDto sessionMe(HttpSession session) {

    return userAuthService.sessionMe(session);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/role")
  public void updateRole(@Valid @RequestBody RoleUpdateRequest roleUpdateRequest,
      HttpServletRequest request) {

    userAuthService.updateRole(roleUpdateRequest, request);
  }
}
