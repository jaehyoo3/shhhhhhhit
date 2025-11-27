package com.foorend.api.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 간단 정보")
public record UserSimpleInfo(
        @Schema(description = "사용자 ID")
        Long userId,

        @Schema(description = "이메일")
        String email,

        @Schema(description = "이름")
        String name
) {
    /**
     * 정적 팩토리 메서드
     */
    public static UserSimpleInfo of(Long userId, String email, String name) {
        return new UserSimpleInfo(userId, email, name);
    }
}
