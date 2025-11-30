package com.foorend.api.common.exception;

import com.foorend.api.common.constants.ErrorCode;
import com.foorend.api.common.domain.BaseRes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 전역 예외 처리 핸들러
 * - Spring MVC 컨트롤러에서 발생하는 예외를 공통 처리
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 커스텀 글로벌 예외 처리
     */
    @ExceptionHandler(GlobalException.class)
    public BaseRes handleGlobalException(GlobalException ex, HttpServletRequest request, HttpServletResponse response) {
        log.error("GlobalException: {}", ex.getMessage(), ex);

        // HTTP 상태 코드 설정 (인증 관련 에러는 401/403, 그 외는 400)
        int httpStatus = getHttpStatus(ex.getErrorCode());
        response.setStatus(httpStatus);

        return new BaseRes(ex.getErrorCode());
    }

    /**
     * 시스템 예외 처리 (모든 예외의 fallback)
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseRes handleUnhandledException(Throwable ex, HttpServletRequest request) {
        log.error("Unhandled Exception: {}", ex.getMessage(), ex);
        return new BaseRes(ErrorCode.INTERNAL_ERR);
    }

    /**
     * @Valid, @RequestBody 유효성 검사 실패 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseRes handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.warn("Validation Failed (@RequestBody)");

        String errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("[%s] %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        log.warn("Validation Errors: {}", errorMessages);

        BaseRes baseRes = new BaseRes(ErrorCode.INVALID_PARAMETER);
        baseRes.setMessage(errorMessages);
        return baseRes;
    }

    /**
     * @Valid, @ModelAttribute 유효성 검사 실패 처리
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseRes handleBindException(BindException ex) {
        log.warn("Validation Failed (@ModelAttribute)");

        String errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("[%s] %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        log.warn("Validation Errors: {}", errorMessages);

        BaseRes baseRes = new BaseRes(ErrorCode.INVALID_PARAMETER);
        baseRes.setMessage(errorMessages);
        return baseRes;
    }

    /**
     * ErrorCode에 따른 HTTP 상태 코드 반환
     */
    private int getHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case AUTH_UNAUTHORIZED, AUTH_EXPIRED_JWT, AUTH_INVALID_JWT,
                 AUTH_MALFORMED_JWT, AUTH_UNSUPPORTED_JWT, AUTH_INVALID_SIGNATURE,
                 AUTH_EMPTY_JWT -> HttpStatus.UNAUTHORIZED.value();
            case AUTH_FORBIDDEN -> HttpStatus.FORBIDDEN.value();
            case USER_NOT_FOUND -> HttpStatus.NOT_FOUND.value();
            case INVALID_PARAMETER, DUPLICATION_ERROR -> HttpStatus.BAD_REQUEST.value();
            default -> HttpStatus.BAD_REQUEST.value();
        };
    }
}
