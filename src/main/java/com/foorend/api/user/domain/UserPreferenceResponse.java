package com.foorend.api.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 사용자 선호 설정 응답 (가격대 + 언어)
 */
@Schema(description = "사용자 선호 설정 응답")
public record UserPreferenceResponse(

        @Schema(description = "선호 가격대 목록")
        List<String> priceTiers,

        @Schema(description = "선호 언어 목록")
        List<String> languages,

        @Schema(description = "데이터 존재 여부")
        boolean hasData
) {
    /**
     * 정적 팩토리 메서드
     */
    public static UserPreferenceResponse of(List<String> priceTiers, List<String> languages) {
        boolean hasData = (priceTiers != null && !priceTiers.isEmpty())
                || (languages != null && !languages.isEmpty());
        return new UserPreferenceResponse(priceTiers, languages, hasData);
    }

    /**
     * 빈 응답
     */
    public static UserPreferenceResponse empty() {
        return new UserPreferenceResponse(List.of(), List.of(), false);
    }
}
