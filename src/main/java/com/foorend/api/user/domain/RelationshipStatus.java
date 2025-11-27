package com.foorend.api.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 연애 상태
 */
@Getter
@RequiredArgsConstructor
public enum RelationshipStatus {
    SINGLE("싱글"),
    COUPLE("연애중"),
    MARRIED("기혼");

    private final String description;
}


