package com.example.springlibrary.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CodeExpire.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCodeException(CodeExpire ex) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", 401);
        error.put("error", "Unauthorized");
        error.put("message", ex.getMessage());
        error.put("timestamp", Instant.now());
        return new ResponseEntity<>(error, HttpStatus.GATEWAY_TIMEOUT);
    }
}
