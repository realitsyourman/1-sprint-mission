package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.auth.UserLoginRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Auth Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserAuthController {

  private final UserLoginRequest userAuthService;
  private final UserService userService;

//  @Operation(summary = "유저 로그인")
//  @PostMapping("/login")
//  public ResponseLogin login(@RequestBody @Validated LoginRequest request) {
//    return userAuthService.login(request);
//  }

  @GetMapping("/csrf-token")
  public CsrfToken publishCsrfToken(CsrfToken csrfToken) {
    return csrfToken;
  }

  @GetMapping("/me")
  public ResponseEntity<UserDto> sessionMe(HttpSession session) {

    SecurityContext context = (SecurityContext)
        session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

    if (context == null || context.getAuthentication() == null || !context.getAuthentication()
        .isAuthenticated()) {

      return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .build();
    }

    Authentication authentication = context.getAuthentication();
    UserDto findUser = userService.findByUsername(authentication.getName());

    return ResponseEntity.status(HttpStatus.OK)
        .body(findUser);
  }
}
