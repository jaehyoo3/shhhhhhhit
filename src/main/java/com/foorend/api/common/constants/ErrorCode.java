package com.foorend.api.common.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 에러 코드 정의
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통 에러
    SUCCESS(0, "성공"),
    INTERNAL_ERR(9999, "시스템 오류가 발생했습니다."),
    INVALID_PARAMETER(1001, "잘못된 파라미터입니다."),
    NO_DATA(998, "조회된 데이터가 없습니다."),
    DUPLICATION_ERROR(1002, "중복된 데이터가 존재합니다."),

    // 인증 관련 에러 (AUTH: 1xxx)
    AUTH_UNAUTHORIZED(1100, "인증이 필요합니다."),
    AUTH_FORBIDDEN(1101, "접근 권한이 없습니다."),
    AUTH_EXPIRED_JWT(1102, "토큰이 만료되었습니다."),
    AUTH_INVALID_JWT(1103, "유효하지 않은 토큰입니다."),
    AUTH_MALFORMED_JWT(1104, "잘못된 형식의 토큰입니다."),
    AUTH_UNSUPPORTED_JWT(1105, "지원하지 않는 토큰입니다."),
    AUTH_INVALID_SIGNATURE(1106, "토큰 서명이 유효하지 않습니다."),
    AUTH_EMPTY_JWT(1107, "토큰이 비어있습니다."),

    // 사용자 관련 에러 (USER: 2xxx)
    USER_NOT_FOUND(2001, "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(2002, "이미 존재하는 사용자입니다."),
    USER_INVALID_PASSWORD(2003, "비밀번호가 일치하지 않습니다."),

    // OAuth 관련 에러 (OAUTH: 3xxx)
    OAUTH_PROVIDER_NOT_SUPPORTED(3001, "지원하지 않는 소셜 로그인입니다."),
    OAUTH_EMAIL_NOT_FOUND(3002, "이메일 정보를 가져올 수 없습니다.");

    private final int code;
    private final String message;
}
