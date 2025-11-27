package com.foorend.api.common.domain;

import java.io.Serializable;

/**
 * JWT 토큰 검증 응답 도메인
 *
 * @param userId         사용자 ID
 * @param expiryDuration 토큰 만료까지 남은 시간 (밀리초 단위)
 * @param valid          토큰 유효 여부
 */
public record JwtCheckResponse(
        String userId,
        Long expiryDuration,
        boolean valid
) implements Serializable {

    /**
     * Builder 패턴 대체 - 정적 팩토리 메서드
     */
    public static JwtCheckResponse of(String userId, Long expiryDuration, boolean valid) {
        return new JwtCheckResponse(userId, expiryDuration, valid);
    }

    /**
     * 유효한 토큰 응답 생성
     */
    public static JwtCheckResponse valid(String userId, Long expiryDuration) {
        return new JwtCheckResponse(userId, expiryDuration, true);
    }

    /**
     * 만료된 토큰 응답 생성
     */
    public static JwtCheckResponse expired(String userId) {
        return new JwtCheckResponse(userId, 0L, false);
    }

    /**
     * 유효하지 않은 토큰 응답 생성
     */
    public static JwtCheckResponse invalid() {
        return new JwtCheckResponse("", -1L, false);
    }
}
