package com.foorend.api.meeting.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 모임 참여 정보
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "모임 참여 정보")
public class SlotEntry {

    @Schema(description = "참여 ID (PK)")
    private Long entryId;

    @Schema(description = "모임 ID")
    private Long slotId;

    @Schema(description = "사용자 ID")
    private Long userId;

    @Schema(description = "참여 상태")
    @Builder.Default
    private SlotEntryStatus status = SlotEntryStatus.JOINED;

    @Schema(description = "취소 사유")
    private String cancelReason;

    @Schema(description = "생성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시")
    private LocalDateTime updatedAt;
}

