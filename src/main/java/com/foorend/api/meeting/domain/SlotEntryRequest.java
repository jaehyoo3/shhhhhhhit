package com.foorend.api.meeting.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 모임 가입 요청
 */
@Schema(description = "모임 가입 요청")
public record SlotEntryRequest(

        @NotNull(message = "모임 ID는 필수입니다.")
        @Schema(description = "모임 ID", example = "1")
        Long slotId
) {}

