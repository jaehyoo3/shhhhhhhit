package com.foorend.api.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 사용자 선호 설정 요청 (가격대 + 언어)
 */
@Schema(description = "사용자 선호 설정 요청")
public record UserPreferenceRequest(

        @Schema(description = "선호 가격대 목록", example = "[\"LOW\", \"MID\"]")
        List<PriceTier> priceTiers,

        @Schema(description = "선호 언어 목록", example = "[\"KO\", \"EN\"]")
        List<Language> languages
) {}
