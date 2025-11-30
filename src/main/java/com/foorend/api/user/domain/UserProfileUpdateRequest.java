package com.foorend.api.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 회원 프로필 수정 요청 (PATCH)
 * - 모든 필드가 optional
 * - 보낸 값만 업데이트됨
 */
@Schema(description = "회원 프로필 수정 요청")
public record UserProfileUpdateRequest(

        @Schema(description = "이름", example = "홍길동")
        String name,

        @Schema(description = "핸드폰 번호", example = "01012345678")
        String phoneNumber,

        @Schema(description = "연애 상태", example = "SINGLE")
        RelationshipStatus relationshipStatus,

        @Schema(description = "직업 카테고리", example = "TECH")
        JobCategory jobCategory,

        @Schema(description = "국적", example = "KR")
        String nationality
) {
    /**
     * 모든 필드가 null인지 확인
     */
    public boolean isEmpty() {
        return name == null
                && phoneNumber == null
                && relationshipStatus == null
                && jobCategory == null
                && nationality == null;
    }
}
