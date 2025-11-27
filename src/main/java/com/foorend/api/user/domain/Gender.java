package com.foorend.api.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 성별
 */
@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("남성"),
    FEMALE("여성");

    private final String description;
}


