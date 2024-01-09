package com.vention.notification_service.config;

import com.vention.general.lib.dto.response.GlobalResponseDTO;
import com.vention.general.lib.exceptions.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = DataNotFoundException.class)
    public ResponseEntity<GlobalResponseDTO> apiExceptionHandler(DataNotFoundException e) {
        log.warn(e.getMessage());
        return getResponse(e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    private ResponseEntity<GlobalResponseDTO> getResponse(String message, int status) {
        return ResponseEntity.status(status).body(
                GlobalResponseDTO.builder()
                        .status(status)
                        .message(message)
                        .time(ZonedDateTime.now())
                        .build()
        );
    }
}
