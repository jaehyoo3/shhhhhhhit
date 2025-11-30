package com.foorend.api.user.domain;

import lombok.Getter;

/**
 * 선호 언어
 */
@Getter
public enum Language {
    KO("한국어"),
    EN("영어"),
    JP("일본어"),
    CN("중국어"),
    ES("스페인어"),
    FR("프랑스어"),
    DE("독일어"),
    PT("포르투갈어"),
    RU("러시아어"),
    AR("아랍어"),
    VI("베트남어"),
    TH("태국어"),
    ID("인도네시아어");

    private final String name;

    Language(String name) {
        this.name = name;
    }
}
