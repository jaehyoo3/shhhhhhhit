package com.foorend.api.meeting.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 모임 일정 등록 요청
 */
@Schema(description = "모임 일정 등록 요청")
public record MeetingSlotRequest(

        @NotBlank(message = "모임 지역은 필수입니다.")
        @Schema(description = "모임 지역", example = "강남")
        String locationArea,

        @NotNull(message = "모임 날짜는 필수입니다.")
        @Schema(description = "모임 날짜", example = "2025-12-01")
        LocalDate meetDate,

        @NotNull(message = "모임 시간은 필수입니다.")
        @Schema(description = "모임 시간", example = "19:00")
        LocalTime meetTime,

        @Schema(description = "최대 정원", example = "6")
        Integer maxCapacity
) {}
