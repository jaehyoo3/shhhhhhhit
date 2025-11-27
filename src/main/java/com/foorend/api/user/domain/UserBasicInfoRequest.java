package com.foorend.api.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 회원 기본정보 입력 요청
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 기본정보 입력 요청")
public class UserBasicInfoRequest {

    @NotBlank(message = "이름은 필수입니다.")
    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "핸드폰 번호", example = "01012345678")
    private String phoneNumber;

    @NotNull(message = "성별은 필수입니다.")
    @Schema(description = "성별", example = "MALE")
    private Gender gender;

    @NotNull(message = "생년월일은 필수입니다.")
    @Schema(description = "생년월일", example = "1995-05-15")
    private LocalDate birthday;

    @Schema(description = "연애 상태", example = "SINGLE")
    private RelationshipStatus relationshipStatus;

    @Schema(description = "국적", example = "KR")
    private String nationality;

    @Schema(description = "직종", example = "개발자")
    private String jobTitle;

    @Schema(description = "성향 답변 (JSON)", example = "{\"q1\": \"A\", \"q2\": \"B\"}")
    private String traitsAnswers;
}


