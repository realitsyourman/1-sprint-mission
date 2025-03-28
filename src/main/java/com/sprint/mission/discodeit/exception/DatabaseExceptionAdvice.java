package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DatabaseExceptionAdvice {

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(ConstraintViolationException.class)
  public ErrorResponse duplicateRow(ConstraintViolationException e) {
    String errorMessage = e.getMessage();

    if (errorMessage.contains("username") || errorMessage.contains("always_have_username")) {
      return ErrorResponse.builder()
          .timestamp(Instant.now())
          .code(ErrorCode.DUPLICATE_USER_NAME.getCode())
          .message(ErrorCode.DUPLICATE_USER_NAME.getMessage())
          .details(
              Map.of(e.getClass().getSimpleName(), ErrorCode.DUPLICATE_USER_NAME.getMessage()))
          .exceptionType(e.getClass().getSimpleName())
          .status(HttpStatus.CONFLICT.value())
          .build();
    } else if (errorMessage.contains("email") || errorMessage.contains("always_have_email")) {
      return ErrorResponse.builder()
          .timestamp(Instant.now())
          .code(ErrorCode.DUPLICATE_USER_EMAIL.getCode())
          .message(ErrorCode.DUPLICATE_USER_EMAIL.getMessage())
          .details(
              Map.of(e.getClass().getSimpleName(), ErrorCode.DUPLICATE_USER_EMAIL.getMessage()))
          .exceptionType(e.getClass().getSimpleName())
          .status(HttpStatus.CONFLICT.value())
          .build();
    } else {
      return ErrorResponse.builder()
          .timestamp(Instant.now())
          .code(ErrorCode.DATA_INTEGRITY_VIOLATION.getCode())
          .message(ErrorCode.DATA_INTEGRITY_VIOLATION.getMessage())
          .details(
              Map.of(e.getClass().getSimpleName(), ErrorCode.DATA_INTEGRITY_VIOLATION.getMessage()))
          .exceptionType(e.getClass().getSimpleName())
          .status(HttpStatus.CONFLICT.value())
          .build();
    }
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public ErrorResponse exception(Exception e) {

    return ErrorResponse.builder()
        .timestamp(Instant.now())
        .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
        .message(e.getMessage())
        .details(Map.of(e.getClass().getSimpleName(), "알 수 없는 서버 오류가 발생했습니다."))
        .exceptionType(e.getClass().getSimpleName())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .build();
  }
}
