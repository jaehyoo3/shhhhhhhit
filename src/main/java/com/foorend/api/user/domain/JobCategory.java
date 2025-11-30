package com.foorend.api.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 직업 카테고리
 */
@Schema(description = "직업 카테고리")
public enum JobCategory {

    OFFICE("관리/사무직"),
    TECH("기술/IT"),
    SERVICE("서비스/판매"),
    FOOD("요식업"),
    MEDICAL("의료/보건"),
    EDUCATION("교육/연구"),
    LAW_FINANCE("법률/금융"),
    ART_CULTURE("예술/문화"),
    OTHER("기타");

    private final String description;

    JobCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

