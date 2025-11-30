package com.foorend.api.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * 회원 기본정보 입력 요청
 */
@Schema(description = "회원 기본정보 입력 요청")
public record UserBasicInfoRequest(

        @NotBlank(message = "이름은 필수입니다.")
        @Schema(description = "이름", example = "홍길동")
        String name,

        @Schema(description = "핸드폰 번호", example = "01012345678")
        String phoneNumber,

        @NotNull(message = "성별은 필수입니다.")
        @Schema(description = "성별", example = "MALE")
        Gender gender,

        @NotNull(message = "생년월일은 필수입니다.")
        @Schema(description = "생년월일", example = "1995-05-15")
        LocalDate birthday,

        @Schema(description = "연애 상태", example = "SINGLE")
        RelationshipStatus relationshipStatus,

        @Schema(description = "국적", example = "KR")
        String nationality,

        @Schema(description = "직업 카테고리", example = "TECH")
        JobCategory jobCategory,

        @Schema(description = "성향 답변 (JSON)", example = "{\"q1\": \"A\", \"q2\": \"B\"}")
        Object traitsAnswers
) {}
