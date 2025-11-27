package com.foorend.api.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 회원 정보 도메인
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 사용자 고유 ID (PK)
     */
    private Long userId;

    /**
     * 구글 고유 UID
     */
    private String googleId;

    /**
     * 이메일
     */
    private String email;

    /**
     * 이름
     */
    private String name;

    /**
     * 핸드폰 번호
     */
    private String phoneNumber;

    /**
     * 성별
     */
    private Gender gender;

    /**
     * 생년월일
     */
    private LocalDate birthday;

    /**
     * 프로필 이미지 URL
     */
    private String profileImageUrl;

    /**
     * 연애 상태
     */
    private RelationshipStatus relationshipStatus;

    /**
     * 국적 (기본: KR)
     */
    @Builder.Default
    private String nationality = "KR";

    /**
     * 직종
     */
    private String jobTitle;

    /**
     * 회원 상태
     */
    @Builder.Default
    private UserStatus userStatus = UserStatus.ACTIVE;

    /**
     * Refresh Token
     */
    private String refreshToken;

    /**
     * 최초 생성 일자
     */
    private LocalDateTime createdAt;

    /**
     * 최종 수정 일자
     */
    private LocalDateTime updatedAt;
}


