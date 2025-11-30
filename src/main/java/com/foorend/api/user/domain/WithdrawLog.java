package com.foorend.api.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원 탈퇴 로그 도메인
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 탈퇴 로그")
public class WithdrawLog {

    @Schema(description = "탈퇴 로그 ID (PK)")
    private Long logId;

    @Schema(description = "사용자 ID")
    private Long userId;

    @Schema(description = "탈퇴 전 이메일")
    private String originalEmail;

    @Schema(description = "탈퇴 사유")
    private String reason;

    @Schema(description = "탈퇴 일시")
    private LocalDateTime createdAt;
}

