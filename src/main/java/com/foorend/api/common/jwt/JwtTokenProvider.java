package com.foorend.api.common.jwt;

import com.foorend.api.common.constants.ErrorCode;
import com.foorend.api.common.constants.JwtConstants;
import com.foorend.api.common.domain.JwtCheckResponse;
import com.foorend.api.common.exception.GlobalException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * JWT 토큰 생성 및 검증 Provider
 * - Access/Refresh 토큰 분리
 * - 역할(roles) 기반 권한 처리
 * - 세분화된 에러 처리
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // ==================== 토큰 생성 ====================

    /**
     * Access Token 생성
     */
    public String createAccessToken(Long userSeq, String email, String role) {
        return createToken(userSeq, email, role, JwtConstants.TOKEN_TYPE_ACCESS, accessExpiration);
    }

    /**
     * Access Token 생성 (기본 역할)
     */
    public String createAccessToken(Long userSeq, String email) {
        return createAccessToken(userSeq, email, JwtConstants.ROLE_USER);
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(Long userSeq, String email) {
        return createToken(userSeq, email, null, JwtConstants.TOKEN_TYPE_REFRESH, refreshExpiration);
    }

    /**
     * 토큰 생성 (내부)
     */
    private String createToken(Long userSeq, String email, String role, String tokenType, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        var builder = Jwts.builder()
                .subject(String.valueOf(userSeq))
                .issuer(JwtConstants.JWT_ISSUER)
                .claim(JwtConstants.CLAIM_USER_SEQ, userSeq)
                .claim(JwtConstants.CLAIM_TOKEN_TYPE, tokenType)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key);

        // email 추가
        if (StringUtils.hasText(email)) {
            builder.claim("email", email);
        }

        // 역할 추가 (Access Token만)
        if (StringUtils.hasText(role)) {
            builder.claim(JwtConstants.CLAIM_ROLES, role);
        }

        return builder.compact();
    }

    // ==================== 토큰 검증 ====================

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(cleanToken(token));
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다: {}", e.getMessage());
            throw new GlobalException(ErrorCode.AUTH_MALFORMED_JWT);
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다: {}", e.getMessage());
            throw new GlobalException(ErrorCode.AUTH_EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다: {}", e.getMessage());
            throw new GlobalException(ErrorCode.AUTH_UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다: {}", e.getMessage());
            throw new GlobalException(ErrorCode.AUTH_INVALID_JWT);
        }
    }

    /**
     * 토큰 유효성 검증 (예외 없이 boolean 반환)
     */
    public boolean validateTokenSilently(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(cleanToken(token));
            return true;
        } catch (Exception e) {
            log.debug("토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰 상세 검증 (유효성 + 상세 정보 반환)
     */
    public JwtCheckResponse validateTokenDetail(String token) {
        try {
            Claims claims = getClaims(token);
            return JwtCheckResponse.valid(claims.getSubject(), claims.getExpiration().getTime());
        } catch (ExpiredJwtException e) {
            return JwtCheckResponse.expired(e.getClaims().getSubject());
        } catch (Exception e) {
            return JwtCheckResponse.invalid();
        }
    }

    // ==================== 토큰 정보 추출 ====================

    /**
     * 토큰에서 사용자 고유번호(userSeq) 추출
     */
    public Long getUserSeq(String token) {
        Claims claims = getClaims(token);
        Object userSeq = claims.get(JwtConstants.CLAIM_USER_SEQ);
        
        if (userSeq == null) {
            throw new GlobalException(ErrorCode.USER_NOT_FOUND);
        }
        
        return Long.valueOf(String.valueOf(userSeq));
    }

    /**
     * 토큰에서 이메일 추출
     */
    public String getEmail(String token) {
        Claims claims = getClaims(token);
        return (String) claims.get("email");
    }

    /**
     * 토큰에서 역할 추출
     */
    public String getRole(String token) {
        Claims claims = getClaims(token);
        String role = (String) claims.get(JwtConstants.CLAIM_ROLES);
        return role != null ? role : JwtConstants.ROLE_USER;
    }

    /**
     * 토큰에서 발급자(issuer) 추출
     */
    public String getIssuer(String token) {
        Claims claims = getClaims(token);
        return claims.getIssuer();
    }

    /**
     * 토큰 타입 확인 (ACCESS/REFRESH)
     */
    public String getTokenType(String token) {
        Claims claims = getClaims(token);
        return (String) claims.get(JwtConstants.CLAIM_TOKEN_TYPE);
    }

    /**
     * Access Token인지 확인
     */
    public boolean isAccessToken(String token) {
        return JwtConstants.TOKEN_TYPE_ACCESS.equals(getTokenType(token));
    }

    /**
     * Refresh Token인지 확인
     */
    public boolean isRefreshToken(String token) {
        return JwtConstants.TOKEN_TYPE_REFRESH.equals(getTokenType(token));
    }

    // ==================== Authentication 생성 ====================

    /**
     * 토큰에서 Authentication 객체 생성
     */
    public Authentication getAuthentication(String token) {
        token = cleanToken(token);
        
        Long userSeq = getUserSeq(token);
        String role = getRole(token);
        
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(role)
        );
        
        UserDetails userDetails = new User(
                String.valueOf(userSeq),
                "",
                authorities
        );
        
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    // ==================== 유틸리티 ====================

    /**
     * Request Header에서 토큰 추출
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtConstants.AUTH_HEADER);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConstants.AUTH_HEADER_PREFIX)) {
            return bearerToken.substring(JwtConstants.AUTH_HEADER_PREFIX.length()).trim();
        }
        
        return null;
    }

    /**
     * 토큰 만료 시간 반환 (밀리초)
     */
    public long getAccessExpiration() {
        return accessExpiration;
    }

    public long getRefreshExpiration() {
        return refreshExpiration;
    }

    // ==================== Private Methods ====================

    /**
     * Claims 추출
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(cleanToken(token))
                .getPayload();
    }

    /**
     * 토큰 공백 제거
     */
    private String cleanToken(String token) {
        return (token == null) ? null : token.trim();
    }
}
