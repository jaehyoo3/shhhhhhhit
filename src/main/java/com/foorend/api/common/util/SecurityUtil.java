package com.foorend.api.common.util;

import com.foorend.api.common.constants.ErrorCode;
import com.foorend.api.common.exception.GlobalException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 보안 관련 유틸리티
 * - 현재 로그인 사용자 정보 조회
 */
public class SecurityUtil {

    private SecurityUtil() {
        // 인스턴스화 방지
    }

    /**
     * 현재 로그인한 사용자 ID 조회
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new GlobalException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        String principal = authentication.getName();

        if (principal == null || "anonymousUser".equals(principal)) {
            throw new GlobalException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        try {
            return Long.parseLong(principal);
        } catch (NumberFormatException e) {
            throw new GlobalException(ErrorCode.AUTH_INVALID_JWT);
        }
    }

    /**
     * 현재 로그인 여부 확인
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null 
                && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getName());
    }
}



