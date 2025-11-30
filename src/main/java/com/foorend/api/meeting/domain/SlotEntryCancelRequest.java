package com.foorend.api.meeting.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 모임 참여 취소 요청
 */
@Schema(description = "모임 참여 취소 요청")
public record SlotEntryCancelRequest(

        @NotBlank(message = "취소 사유를 입력해주세요.")
        @Schema(description = "취소 사유", example = "개인 사정으로 참여가 어렵습니다.")
        String cancelReason
) {}

