package com.foorend.api.common.exception;

import com.foorend.api.common.constants.ErrorCode;
import lombok.Getter;

/**
 * 사용자 정의 전역 예외 클래스
 */
@Getter
public class GlobalException extends RuntimeException {

    private final ErrorCode errorCode;
    private final boolean sendMailFlag;

    public GlobalException(ErrorCode errorCode) {
        this(errorCode, false);
    }

    public GlobalException(ErrorCode errorCode, boolean sendMailFlag) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.sendMailFlag = sendMailFlag;
    }

    public GlobalException(String errorMessage) {
        this(errorMessage, false);
    }

    public GlobalException(String errorMessage, boolean sendMailFlag) {
        super(errorMessage);
        this.errorCode = ErrorCode.INTERNAL_ERR;
        this.sendMailFlag = sendMailFlag;
    }

    /**
     * 에러 코드 반환 (int)
     */
    public int getCode() {
        return errorCode.getCode();
    }
}
