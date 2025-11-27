package com.foorend.api.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**-------------------------------------------------------------
 # FileName : BaseGenericRes.java
 # Author   : foodinko
 # Desc     : BaseRes + 제네릭 추가 응답 모델
 # Date     : 2025-08-05
 /**----------------------------------------------------------**/
@EqualsAndHashCode(callSuper=false)
@Data
public class BaseGenericRes<T> extends BaseRes {

    @Schema(description = "응답 데이터")
    private T data;

    public BaseGenericRes() {}

    public void setResultData(Integer code, String message, T data){
        this.setBaseResData(code, message);
        this.setData(data);
    }

    public void setSuccessResultData(String message, T data){
        this.setSuccess(message);
        this.setData(data);
    }

    public void setSuccessResultData(T data){
        this.setSuccess();
        this.setData(data);
    }
}
