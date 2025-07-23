package project.gymnawa.domain.common.error.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.gymnawa.auth.jwt.error.CustomAuthException;
import project.gymnawa.domain.common.error.dto.ErrorCode;
import project.gymnawa.domain.common.error.exception.CustomException;
import project.gymnawa.domain.common.error.dto.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.info("GlobalExceptionHandler");
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode.getCode(), errorCode.getErrorMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.info("GlobalExceptionHandler");
        Map<String, String> errorMap = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("VALIDATION_ERROR", "입력값이 유효하지 않습니다.", errorMap));
    }

    // ReissueController에서 발생하는 인증오류를 해결하기 위해 추가
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleCustomAuthException(CustomAuthException e) {
        log.info("GlobalExceptionHandler");
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode.getCode(), errorCode.getErrorMessage()));
    }
}
