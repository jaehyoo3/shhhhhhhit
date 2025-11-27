package com.foorend.api.common.constants;

/**
 * 전역 상수 정의
 */
public final class GlobalConstants {

    private GlobalConstants() {
        // 인스턴스화 방지
    }

    // ==================== 시스템 응답 코드 ====================
    public static final Integer SYSTEM_SUCCESS_CODE = 0;
    public static final String SYSTEM_SUCCESS_MSG = "Success";

    // 데이터 조회 실패
    public static final Integer FAILED_DATA_SELECT_998 = 998;
    public static final String FAILED_DATA_SELECT_998_MSG = "조회된 데이터가 없습니다.";

    // 시스템 오류
    public static final Integer SYSTEM_ERROR_CODE = 9999;
    public static final String SYSTEM_ERROR_MSG = "시스템 오류가 발생했습니다.";

    // ==================== 공통 구분자 ====================
    public static final String COMMON_EXCEPTION_DELIMETER = " | ";

    // ==================== 사용자 관련 ====================
    public static final Long ADMIN_USER_SEQ = 1L;
    public static final String ADMIN_USER_ROLE = "ROLE_ADMIN";
    public static final String DEF_USER_ROLE = "ROLE_USER";

    public static final int    API_UNAUTHORIZED_401         = 401;
    public static final String API_UNAUTHORIZED_401_MSG     = "접근 권한 없음";

    // ==================== API 인증 관련 ====================
    public static final String API_AUTHORIZATION_500_MSG = "Authorization header is empty";
    public static final String API_AUTHORIZATION_501_MSG = "Authorization header is too short";
    public static final String API_AUTHORIZATION_502_MSG = "Invalid authorization header prefix";
}

