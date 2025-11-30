package com.foorend.api.common.service;

import com.foorend.api.common.domain.BaseGenericListRes;
import com.foorend.api.common.domain.CodeItem;
import com.foorend.api.user.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommonService 테스트")
class CommonServiceTest {

    @InjectMocks
    private CommonService commonService;

    @Test
    @DisplayName("국적 목록 조회 - 성공")
    void getNationalities_Success() {
        // when
        BaseGenericListRes<CodeItem> result = commonService.getNationalities();

        // then
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData()).isNotEmpty();
        assertThat(result.getTotalCount()).isEqualTo(Nationality.values().length);

        // 한국 확인
        CodeItem korea = result.getData().stream()
                .filter(item -> "KR".equals(item.code()))
                .findFirst()
                .orElse(null);

        assertThat(korea).isNotNull();
        assertThat(korea.name()).isEqualTo("대한민국");
        assertThat(korea.dialCode()).isEqualTo("+82");
    }

    @Test
    @DisplayName("성별 목록 조회 - 성공")
    void getGenders_Success() {
        // when
        BaseGenericListRes<CodeItem> result = commonService.getGenders();

        // then
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData()).hasSize(Gender.values().length);

        // MALE 확인
        CodeItem male = result.getData().stream()
                .filter(item -> "MALE".equals(item.code()))
                .findFirst()
                .orElse(null);

        assertThat(male).isNotNull();
        assertThat(male.name()).isEqualTo("남성");
    }

    @Test
    @DisplayName("연애 상태 목록 조회 - 성공")
    void getRelationshipStatuses_Success() {
        // when
        BaseGenericListRes<CodeItem> result = commonService.getRelationshipStatuses();

        // then
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData()).hasSize(RelationshipStatus.values().length);

        // SINGLE 확인
        CodeItem single = result.getData().stream()
                .filter(item -> "SINGLE".equals(item.code()))
                .findFirst()
                .orElse(null);

        assertThat(single).isNotNull();
    }

    @Test
    @DisplayName("가격대 목록 조회 - 성공")
    void getPriceTiers_Success() {
        // when
        BaseGenericListRes<CodeItem> result = commonService.getPriceTiers();

        // then
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData()).hasSize(PriceTier.values().length);
        assertThat(result.getData()).hasSize(3);  // LOW, MID, HIGH
    }

    @Test
    @DisplayName("직업 카테고리 목록 조회 - 성공")
    void getJobCategories_Success() {
        // when
        BaseGenericListRes<CodeItem> result = commonService.getJobCategories();

        // then
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData()).hasSize(JobCategory.values().length);
        assertThat(result.getData()).hasSize(9);  // 9개 카테고리

        // TECH 확인
        CodeItem tech = result.getData().stream()
                .filter(item -> "TECH".equals(item.code()))
                .findFirst()
                .orElse(null);

        assertThat(tech).isNotNull();
        assertThat(tech.name()).isEqualTo("기술/IT");
    }

    @Test
    @DisplayName("직업 카테고리 목록 - 모든 값 포함 확인")
    void getJobCategories_AllValues() {
        // when
        BaseGenericListRes<CodeItem> result = commonService.getJobCategories();

        // then
        assertThat(result.getData().stream().map(CodeItem::code).toList())
                .containsExactlyInAnyOrder(
                        "OFFICE", "TECH", "SERVICE", "FOOD", "MEDICAL",
                        "EDUCATION", "LAW_FINANCE", "ART_CULTURE", "OTHER"
                );
    }
}

