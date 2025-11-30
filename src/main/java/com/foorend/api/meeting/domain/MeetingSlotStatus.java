package com.foorend.api.meeting.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 모임 상태
 */
@Getter
@RequiredArgsConstructor
public enum MeetingSlotStatus {
    OPEN("모집중"),
    CONFIRMED("확정"),
    CANCELED("취소"),
    FINISHED("종료");

    private final String description;
}

