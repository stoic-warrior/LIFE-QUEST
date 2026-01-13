package com.lifequest.api.advice;

import com.lifequest.api.dto.response.ApiResponse;
import com.lifequest.api.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleApiException(ApiException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.error("API Exception: {} - {}", errorCode.getCode(), ex.getMessage());
        return ResponseEntity.status(errorCode.getStatus())
            .body(ApiResponse.error(ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(ex.getMessage())
                .build()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation Exception: {}", ex.getMessage());
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(ErrorResponse.builder()
                .code(ErrorCode.INVALID_INPUT.getCode())
                .message("Invalid input")
                .build()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {
        log.error("Constraint Violation: {}", ex.getMessage());
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(ErrorResponse.builder()
                .code(ErrorCode.INVALID_INPUT.getCode())
                .message("Invalid input")
                .build()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnexpected(Exception ex) {
        // 실제 에러 로그 출력!
        log.error("Unexpected error: ", ex);
        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getStatus())
            .body(ApiResponse.error(ErrorResponse.builder()
                .code(ErrorCode.INTERNAL_ERROR.getCode())
                .message(ex.getMessage())  // 실제 메시지 반환
                .build()));
    }
}
