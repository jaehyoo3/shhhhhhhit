package com.foorend.api.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 프로시저 호출 시 공통 응답 도메인
 * - errMsg : 에러 메시지 반환
 * - retVal : 반환 코드 (보통 0: 성공, 음수: 실패)
 *
 * @author foodinko
 * @since 2025-08-05
 */
@Data
public class SPCommonDomain {

    @Schema(description = "에러 메시지 (null이 아니면 오류 발생)", hidden = true)
    private String errMsg;

    @Schema(description = "프로시저 반환 값 (0=성공, 기타=실패)", hidden = true)
    private Integer retVal;
}
