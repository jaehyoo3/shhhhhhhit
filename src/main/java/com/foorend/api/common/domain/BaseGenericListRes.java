package com.foorend.api.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * BaseRes + 제네릭 리스트 응답 모델
 *
 * @param <T> 리스트 아이템의 타입
 * @author foodinko
 * @since 2025-08-02=5
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BaseGenericListRes<T> extends BaseRes {

    @Schema(description = "응답 데이터 목록")
    private List<T> data;

    @Schema(description = "전체 건수")
    private int totalCount;

    public BaseGenericListRes() {}

    public void setResultData(Integer code, String message, List<T> data, int totalCount){
        this.setBaseResData(code, message);
        this.data = data;
        this.totalCount = totalCount;
    }

    public void setSuccessResultData(String message, List<T> data, int totalCount){
        this.setSuccess(message);
        this.data = data;
        this.totalCount = totalCount;
    }

    public void setSuccessResultData(List<T> data, int totalCount){
        this.setSuccess();
        this.data = data;
        this.totalCount = totalCount;
    }

    public void setSuccessResultData(List<T> data){
        this.setSuccess();
        this.data = data;
        this.totalCount = data != null ? data.size() : 0;
    }

    public void setNoData(){
        this.setNoData(); // BaseRes의 setNoData() 호출
        this.totalCount = 0;
    }
}
