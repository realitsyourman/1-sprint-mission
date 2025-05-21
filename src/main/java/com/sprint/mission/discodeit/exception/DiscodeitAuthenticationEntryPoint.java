package com.sprint.mission.discodeit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscodeitAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {

    ErrorResponse error = ErrorResponse.builder()
        .timestamp(Instant.now())
        .code(HttpStatus.UNAUTHORIZED.toString())
        .message(ErrorCode.UNAUTHORIZED.getMessage())
        .details(
            Map.of(
                HttpStatus.UNAUTHORIZED.toString(),
                authException.getMessage()
            )
        )
        .exceptionType(ErrorCode.UNAUTHORIZED.getCode())
        .status(HttpStatus.UNAUTHORIZED.value())
        .build();

    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    objectMapper.writeValue(response.getWriter(), error);

    Object o = objectMapper.readValue(request.getInputStream(), Object.class);
    log.error("인증 실패: {}", o.toString());
  }
}
