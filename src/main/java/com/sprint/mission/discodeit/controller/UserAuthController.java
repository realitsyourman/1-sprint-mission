package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.auth.RequestLogin;
import com.sprint.mission.discodeit.entity.auth.ResponseLogin;
import com.sprint.mission.discodeit.service.auth.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Auth Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserAuthController {

  private final UserAuthService userAuthService;

  @Operation(summary = "유저 로그인")
  @PostMapping("/login")
  public ResponseLogin login(@RequestBody @Validated RequestLogin request) {
    return userAuthService.login(request);
  }

  @GetMapping("/csrf-token")
  public CsrfToken publishCsrfToken(CsrfToken csrfToken) {
    return csrfToken;
  }
}
