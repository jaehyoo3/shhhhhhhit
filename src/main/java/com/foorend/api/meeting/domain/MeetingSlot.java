package com.foorend.api.meeting.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 모임 일정 도메인
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "모임 일정")
public class MeetingSlot {

    @Schema(description = "슬롯 ID (PK)")
    private Long slotId;

    @Schema(description = "확정된 식당 ID")
    private Integer confirmedRestaurantId;

    @Schema(description = "모임 지역")
    private String locationArea;

    @Schema(description = "모임 날짜")
    private LocalDate meetDate;

    @Schema(description = "모임 시간")
    private LocalTime meetTime;

    @Schema(description = "최대 정원")
    private Integer maxCapacity;

    @Schema(description = "현재 예약 인원")
    @Builder.Default
    private Integer currentCount = 0;

    @Schema(description = "모임 상태")
    @Builder.Default
    private MeetingSlotStatus status = MeetingSlotStatus.OPEN;

    @Schema(description = "생성 일시")
    private LocalDateTime createdAt;
}

