package com.foorend.api.meeting.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 모임 참여 상태
 */
@Schema(description = "모임 참여 상태")
public enum SlotEntryStatus {

    JOINED("참여"),
    CANCELED("취소"),
    LATE("지각"),
    NOSHOW("노쇼");

    private final String description;

    SlotEntryStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

