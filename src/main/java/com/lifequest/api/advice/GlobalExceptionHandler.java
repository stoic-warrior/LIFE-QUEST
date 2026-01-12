package com.lifequest.api.advice;

import com.lifequest.api.dto.response.ApiResponse;
import com.lifequest.api.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleApiException(ApiException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
            .body(ApiResponse.error(ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(ex.getMessage())
                .build()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(ErrorResponse.builder()
                .code(ErrorCode.INVALID_INPUT.getCode())
                .message("Invalid input")
                .build()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(ErrorResponse.builder()
                .code(ErrorCode.INVALID_INPUT.getCode())
                .message("Invalid input")
                .build()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnexpected(Exception ex) {
        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getStatus())
            .body(ApiResponse.error(ErrorResponse.builder()
                .code(ErrorCode.INTERNAL_ERROR.getCode())
                .message("Unexpected error")
                .build()));
    }
}
