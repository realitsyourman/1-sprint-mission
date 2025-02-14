package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.binary.BinaryContentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class BinaryControllerExceptionAdvice {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(BinaryContentException.class)
    public ErrorResult userNotFound(BinaryContentException e) {
        log.error("Binary exception: {}", e.getMessage());

        return new ErrorResult(ErrorCode.INTERNAL_SERVER_ERROR_BINARY);
    }
}
