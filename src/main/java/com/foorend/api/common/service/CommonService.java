package com.foorend.api.common.service;

import com.foorend.api.common.domain.BaseGenericListRes;
import com.foorend.api.common.domain.CodeItem;
import com.foorend.api.user.domain.Gender;
import com.foorend.api.user.domain.JobCategory;
import com.foorend.api.user.domain.Nationality;
import com.foorend.api.user.domain.PriceTier;
import com.foorend.api.user.domain.RelationshipStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 공통 코드 Service
 */
@Service
public class CommonService {

    /**
     * 국적 목록 조회 (국가코드 포함)
     */
    public BaseGenericListRes<CodeItem> getNationalities() {
        BaseGenericListRes<CodeItem> response = new BaseGenericListRes<>();

        List<CodeItem> nationalities = Arrays.stream(Nationality.values())
                .map(n -> CodeItem.of(n.name(), n.getName(), n.getDialCode()))
                .toList();

        response.setSuccessResultData(nationalities);
        return response;
    }

    /**
     * 성별 목록 조회
     */
    public BaseGenericListRes<CodeItem> getGenders() {
        BaseGenericListRes<CodeItem> response = new BaseGenericListRes<>();

        List<CodeItem> genders = Arrays.stream(Gender.values())
                .map(g -> CodeItem.of(g.name(), g.getDescription()))
                .toList();

        response.setSuccessResultData(genders);
        return response;
    }

    /**
     * 연애 상태 목록 조회
     */
    public BaseGenericListRes<CodeItem> getRelationshipStatuses() {
        BaseGenericListRes<CodeItem> response = new BaseGenericListRes<>();

        List<CodeItem> statuses = Arrays.stream(RelationshipStatus.values())
                .map(r -> CodeItem.of(r.name(), r.getDescription()))
                .toList();

        response.setSuccessResultData(statuses);
        return response;
    }

    /**
     * 선호 가격대 목록 조회
     */
    public BaseGenericListRes<CodeItem> getPriceTiers() {
        BaseGenericListRes<CodeItem> response = new BaseGenericListRes<>();

        List<CodeItem> priceTiers = Arrays.stream(PriceTier.values())
                .map(p -> CodeItem.of(p.name(), p.getDescription()))
                .toList();

        response.setSuccessResultData(priceTiers);
        return response;
    }

    /**
     * 직업 카테고리 목록 조회
     */
    public BaseGenericListRes<CodeItem> getJobCategories() {
        BaseGenericListRes<CodeItem> response = new BaseGenericListRes<>();

        List<CodeItem> jobCategories = Arrays.stream(JobCategory.values())
                .map(j -> CodeItem.of(j.name(), j.getDescription()))
                .toList();

        response.setSuccessResultData(jobCategories);
        return response;
    }
}
