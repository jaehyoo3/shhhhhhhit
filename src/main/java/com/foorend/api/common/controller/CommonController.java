package com.foorend.api.common.controller;

import com.foorend.api.common.domain.BaseGenericListRes;
import com.foorend.api.common.domain.CodeItem;
import com.foorend.api.common.service.CommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 공통 코드 API 컨트롤러
 */
@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
@Tag(name = "Common", description = "공통 코드 API")
public class CommonController {

    private final CommonService commonService;

    /**
     * 국적 목록 조회
     */
    @GetMapping("/nationalities")
    @Operation(summary = "국적 목록 조회", description = "전세계 국가 목록을 조회합니다. (국가코드 포함)")
    public BaseGenericListRes<CodeItem> getNationalities() {
        return commonService.getNationalities();
    }

    /**
     * 성별 목록 조회
     */
    @GetMapping("/genders")
    @Operation(summary = "성별 목록 조회", description = "성별 목록을 조회합니다.")
    public BaseGenericListRes<CodeItem> getGenders() {
        return commonService.getGenders();
    }

    /**
     * 연애 상태 목록 조회
     */
    @GetMapping("/relationship-statuses")
    @Operation(summary = "연애 상태 목록 조회", description = "연애 상태 목록을 조회합니다.")
    public BaseGenericListRes<CodeItem> getRelationshipStatuses() {
        return commonService.getRelationshipStatuses();
    }

    /**
     * 선호 가격대 목록 조회
     */
    @GetMapping("/price-tiers")
    @Operation(summary = "선호 가격대 목록 조회", description = "선호 가격대 목록을 조회합니다.")
    public BaseGenericListRes<CodeItem> getPriceTiers() {
        return commonService.getPriceTiers();
    }

    /**
     * 직업 카테고리 목록 조회
     */
    @GetMapping("/job-categories")
    @Operation(summary = "직업 카테고리 목록 조회", description = "직업 카테고리 목록을 조회합니다.")
    public BaseGenericListRes<CodeItem> getJobCategories() {
        return commonService.getJobCategories();
    }
}
