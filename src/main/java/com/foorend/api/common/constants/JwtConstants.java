package com.foorend.api.common.constants;

/**
 * JWT 관련 상수 정의
 */
public final class JwtConstants {
    
    private JwtConstants() {
        // 인스턴스화 방지
    }
    
    // JWT 발급자
    public static final String JWT_ISSUER = "foorend";
    
    // 인증 헤더
    public static final String AUTH_HEADER = "Authorization";
    public static final String AUTH_HEADER_PREFIX = "Bearer ";
    
    // 토큰 타입
    public static final String TOKEN_TYPE_ACCESS = "ACCESS";
    public static final String TOKEN_TYPE_REFRESH = "REFRESH";
    
    // Claims 키
    public static final String CLAIM_USER_SEQ = "userSeq";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_TOKEN_TYPE = "tokenType";
    public static final String CLAIM_PROVIDER = "provider";
    
    // 기본 역할
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
}



