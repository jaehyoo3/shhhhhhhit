package com.foorend.api.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 선호 가격대
 */
@Getter
@RequiredArgsConstructor
public enum PriceTier {
    LOW("저가"),
    MID("중가"),
    HIGH("고가");

    private final String description;
}

