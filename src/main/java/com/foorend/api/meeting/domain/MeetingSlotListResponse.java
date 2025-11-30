package com.foorend.api.meeting.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * 모임 리스트 응답
 */
@Schema(description = "모임 리스트 응답")
public record MeetingSlotListResponse(

        @Schema(description = "모임 ID")
        Long slotId,

        @Schema(description = "모임 지역")
        String locationArea,

        @Schema(description = "모임 날짜")
        LocalDate meetDate,

        @Schema(description = "요일", example = "금")
        String dayOfWeek,

        @Schema(description = "모임 시간")
        LocalTime meetTime
) {
    /**
     * MeetingSlot -> MeetingSlotListResponse 변환
     */
    public static MeetingSlotListResponse from(MeetingSlot slot) {
        String dayOfWeek = slot.getMeetDate()
                .getDayOfWeek()
                .getDisplayName(TextStyle.SHORT, Locale.KOREAN);

        return new MeetingSlotListResponse(
                slot.getSlotId(),
                slot.getLocationArea(),
                slot.getMeetDate(),
                dayOfWeek,
                slot.getMeetTime()
        );
    }
}
