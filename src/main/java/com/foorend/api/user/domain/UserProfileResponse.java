package com.foorend.api.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * 회원 프로필 상세 조회 응답
 */
@Schema(description = "회원 프로필 상세 정보")
public record UserProfileResponse(

        @Schema(description = "사용자 ID")
        Long userId,

        @Schema(description = "이메일")
        String email,

        @Schema(description = "이름")
        String name,

        @Schema(description = "핸드폰 번호")
        String phoneNumber,

        @Schema(description = "성별")
        Gender gender,

        @Schema(description = "생년월일")
        LocalDate birthday,

        @Schema(description = "프로필 이미지 URL")
        String profileImageUrl,

        @Schema(description = "연애 상태")
        RelationshipStatus relationshipStatus,

        @Schema(description = "국적")
        String nationality,

        @Schema(description = "직업 카테고리")
        JobCategory jobCategory,

        @Schema(description = "성향 답변 (JSON)")
        Object traitsAnswers
) {
    /**
     * User 엔티티로부터 UserProfileResponse 생성
     */
    public static UserProfileResponse from(User user, Object traitsAnswers) {
        return new UserProfileResponse(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getGender(),
                user.getBirthday(),
                user.getProfileImageUrl(),
                user.getRelationshipStatus(),
                user.getNationality(),
                user.getJobCategory(),
                traitsAnswers
        );
    }
}
