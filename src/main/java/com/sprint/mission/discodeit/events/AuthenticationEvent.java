package com.sprint.mission.discodeit.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

@Slf4j
public class AuthenticationEvent {

  @EventListener
  public void authenticationSuccess(AuthenticationSuccessEvent authenticationSuccessEvent) {
    log.info("로그인 성공: {}", authenticationSuccessEvent.getAuthentication().getName());
  }

  @EventListener
  public void authenticationFail(AbstractAuthenticationFailureEvent failureEvent) {
    log.error("로그인 실패: {}, {}", failureEvent.getAuthentication().getName(),
        failureEvent.getException().getMessage());
  }
}
