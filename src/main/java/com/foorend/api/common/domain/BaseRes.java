package com.foorend.api.common.domain;

import com.foorend.api.common.constants.ErrorCode;
import com.foorend.api.common.constants.GlobalConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NonNull;

/**
 * 기본 응답 모델
 */
@Data
public class BaseRes {

    @NotEmpty
    @Schema(description = "리턴 코드 0:정상, 그 외 비정상", example = "0", required = true)
    private Integer code;

    @NonNull
    @Schema(description = "리턴 메시지", example = "Success", required = true)
    private String message;

    public BaseRes() {
        code = 0;
        message = "Success";
    }

    public BaseRes(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public void setSuccessCode() {
        this.code = GlobalConstants.SYSTEM_SUCCESS_CODE;
    }

    public void setSuccessMessage() {
        this.message = GlobalConstants.SYSTEM_SUCCESS_MSG;
    }

    public void setSuccess() {
        this.setSuccessCode();
        this.setSuccessMessage();
    }

    public void setSuccess(String successMessage) {
        this.message = successMessage;
        this.setSuccessCode();
    }

    public void setBaseResData(Integer returnCode, String returnMessage) {
        this.code = returnCode;
        this.message = returnMessage;
    }

    public void setNoData() {
        this.code = GlobalConstants.FAILED_DATA_SELECT_998;
        this.message = GlobalConstants.FAILED_DATA_SELECT_998_MSG;
    }

    public void setError(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public String toLogString() {
        return this.code + GlobalConstants.COMMON_EXCEPTION_DELIMETER + this.message;
    }

    public boolean resultIsSuccess() {
        return this.code.equals(GlobalConstants.SYSTEM_SUCCESS_CODE);
    }
}
