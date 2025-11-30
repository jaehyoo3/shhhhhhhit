package com.foorend.api.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원 성향 도메인
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 성향 정보")
public class UserTraits {

    @Schema(description = "성향 ID (PK)")
    private Long traitsId;

    @Schema(description = "사용자 ID (FK)")
    private Long userId;

    @Schema(description = "답변 원본 데이터 (JSON)")
    private String answers;

    @Schema(description = "최초 생성일")
    private LocalDateTime createdAt;

    @Schema(description = "최종 수정일")
    private LocalDateTime updatedAt;
}



