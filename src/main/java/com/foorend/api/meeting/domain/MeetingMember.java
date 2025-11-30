package com.foorend.api.meeting.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 모임 참석자 정보 (직업 카테고리, 국적만)
 */
@Schema(description = "모임 참석자 정보")
public record MeetingMember(

        @Schema(description = "직업 카테고리", example = "TECH")
        String jobCategory,

        @Schema(description = "국적", example = "KR")
        String nationality
) {}
