package com.foorend.api.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 회원 탈퇴 요청
 */
@Schema(description = "회원 탈퇴 요청")
public record WithdrawRequest(

        @NotBlank(message = "확인 텍스트를 입력해주세요.")
        @Schema(description = "확인 텍스트 (DELETE 입력 필요)", example = "DELETE")
        String confirmText,

        @Schema(description = "탈퇴 사유", example = "서비스를 더 이상 사용하지 않음")
        String reason
) {}
