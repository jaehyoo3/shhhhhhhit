package com.foorend.api.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Refresh Token 요청
 */
@Schema(description = "Refresh Token 요청")
public record RefreshTokenRequest(
        @NotBlank(message = "Refresh Token은 필수입니다.")
        @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String refreshToken
) {}

