package com.foorend.api.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 회원 상태
 */
@Getter
@RequiredArgsConstructor
public enum UserStatus {
    ACTIVE("활성"),
    SUSPENDED("정지"),
    WITHDRAWAL("탈퇴");

    private final String description;
}



