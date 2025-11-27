package com.foorend.api.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**-------------------------------------------------------------
 # FileName : DTOPage.java
 # Author   : foodinko
 # Desc     : 공통 요청 모델
 # Date     : 2025-08-04
 /**-----------------------------------------------------------**/
@Data
@Schema(description = "공통 페이징 요청 모델")
public class DTOPage {

    @Schema(description = "페이지 번호", example = "1", defaultValue = "1")
    private Integer pageNo = 1;

    @Schema(description = "페이지 크기", example = "10", defaultValue = "10")
    private Integer pageSize = 10;

    @Schema(description = "정렬 컬럼", example = "createdAt")
    private String sortColumn;

    @Schema(description = "정렬 타입", example = "desc")
    private String sortType;

    @JsonIgnore
    @Schema(hidden = true)
    private Integer pageFetchNo;
}
