package com.foorend.api.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 토큰 갱신 응답
 */
@Schema(description = "토큰 갱신 응답")
public record TokenRefreshResponse(
        @Schema(description = "Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,
        
        @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String refreshToken
) {}

