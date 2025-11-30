package com.foorend.api.meeting.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 내가 참여 중인 모임 응답
 */
@Schema(description = "내가 참여 중인 모임 정보")
public record MyMeetingResponse(

        @Schema(description = "참여 ID")
        Long entryId,

        @Schema(description = "모임 ID")
        Long slotId,

        @Schema(description = "모임 지역")
        String locationArea,

        @Schema(description = "모임 날짜")
        LocalDate meetDate,

        @Schema(description = "요일")
        String dayOfWeek,

        @Schema(description = "모임 시간")
        LocalTime meetTime,

        @Schema(description = "식당 이름 (하루 전 공개)")
        String restaurantName,

        @Schema(description = "식당 주소 (하루 전 공개)")
        String restaurantAddr,

        @Schema(description = "참석자 목록 - 직업카테고리/국적 (하루 전 공개, 중복 제거)")
        List<MeetingMember> members,

        @Schema(description = "늦어요 인원 수")
        Integer lateCount,

        @Schema(description = "정보 공개 여부 (모임 하루 전부터 true)")
        boolean isInfoRevealed,

        @Schema(description = "모임 참여 여부")
        boolean hasActiveMeeting
) {
    /**
     * 참여 중인 모임 없음
     */
    public static MyMeetingResponse empty() {
        return new MyMeetingResponse(
                null, null, null, null, null, null,
                null, null, List.of(), 0, false, false
        );
    }
}
