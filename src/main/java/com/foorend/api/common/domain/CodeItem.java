package com.foorend.api.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 공통 코드 항목
 */
@Schema(description = "공통 코드 항목")
public record CodeItem(

        @Schema(description = "코드", example = "KR")
        String code,

        @Schema(description = "이름", example = "대한민국")
        String name,

        @Schema(description = "국가 전화번호 코드", example = "+82")
        String dialCode
) {
    /**
     * dialCode 없는 경우
     */
    public static CodeItem of(String code, String name) {
        return new CodeItem(code, name, null);
    }

    /**
     * dialCode 포함
     */
    public static CodeItem of(String code, String name, String dialCode) {
        return new CodeItem(code, name, dialCode);
    }
}
