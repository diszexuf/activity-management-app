package com.github.diszexuf.activitymanagementbackend.exception;

import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IntervalOverlapException.class)
    public ResponseEntity<ErrorResponse> handleIntervalOverlap(IntervalOverlapException exception) {
        log.error("Пересечение интервалов: {}", exception.getMessage());

        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setError("INTERVAL_OVERLAP");
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setTimestamp(OffsetDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(InvalidIntervalException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInterval(InvalidIntervalException exception) {
        log.error("Невалидный интервал: {}", exception.getMessage());

        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setError("INVALID_INTERVAL");
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setTimestamp(OffsetDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        log.error("Ошибка валидации: {}", exception.getMessage());

        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setError("VALIDATION_ERROR");
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setTimestamp(OffsetDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handlerGeneral(Exception exception) {
        log.error("Непредвиденная ошибка: {}", exception.getMessage());

        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setError("INTERNAL_SERVER_ERROR");
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setTimestamp(OffsetDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}
