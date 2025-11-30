package com.foorend.api.user.service;

import com.foorend.api.common.constants.ErrorCode;
import com.foorend.api.common.domain.BaseGenericRes;
import com.foorend.api.common.exception.GlobalException;
import com.foorend.api.common.repository.GenericDAO;
import com.foorend.api.common.util.SecurityUtil;
import com.foorend.api.user.domain.Language;
import com.foorend.api.user.domain.PriceTier;
import com.foorend.api.user.domain.User;
import com.foorend.api.user.domain.UserPreferenceRequest;
import com.foorend.api.user.domain.UserPreferenceResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 사용자 선호 설정 Service
 */
@Service
public class UserPreferenceService {

    private final GenericDAO genericDAO;

    public UserPreferenceService(@Qualifier("mainDB") GenericDAO genericDAO) {
        this.genericDAO = genericDAO;
    }

    /**
     * 내 선호 설정 조회
     */
    public BaseGenericRes<UserPreferenceResponse> findMyPreference() {
        Long userId = SecurityUtil.getCurrentUserId();
        return findPreference(userId);
    }

    /**
     * 사용자 선호 설정 조회
     */
    @SuppressWarnings("unchecked")
    public BaseGenericRes<UserPreferenceResponse> findPreference(Long userId) {
        BaseGenericRes<UserPreferenceResponse> response = new BaseGenericRes<>();

        // 선호 가격대 조회
        List<String> priceTiers = (List<String>) genericDAO.selectList("userPreference.findPriceTiersByUserId", userId);

        // 선호 언어 조회
        List<String> languages = (List<String>) genericDAO.selectList("userPreference.findLanguagesByUserId", userId);

        UserPreferenceResponse preferenceResponse = UserPreferenceResponse.of(priceTiers, languages);

        response.setSuccessResultData(preferenceResponse);
        return response;
    }

    /**
     * 내 선호 설정 저장/수정
     */
    @Transactional
    public BaseGenericRes<Boolean> saveMyPreference(UserPreferenceRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        return savePreference(userId, request);
    }

    /**
     * 사용자 선호 설정 저장/수정
     * - 기존 데이터 삭제 후 새로 등록 (체크박스 특성)
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public BaseGenericRes<Boolean> savePreference(Long userId, UserPreferenceRequest request) {
        BaseGenericRes<Boolean> response = new BaseGenericRes<>();

        // 1. 사용자 존재 여부 확인
        User user = (User) genericDAO.selectOne("user.findByUserId", userId);
        if (user == null) {
            throw new GlobalException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 선호 가격대 필수 (최소 1개)
        if (request.priceTiers() == null || request.priceTiers().isEmpty()) {
            throw new GlobalException(ErrorCode.INVALID_PARAM, "선호 가격대를 최소 1개 이상 선택해야 합니다.");
        }

        // 3. 선호 언어 필수 (최소 1개)
        if (request.languages() == null || request.languages().isEmpty()) {
            throw new GlobalException(ErrorCode.INVALID_PARAM, "선호 언어를 최소 1개 이상 선택해야 합니다.");
        }

        // 4. 선호 가격대 저장
        genericDAO.delete("userPreference.deletePriceTiersByUserId", userId);
        Map<String, Object> priceParams = new HashMap<>();
        priceParams.put("userId", userId);
        priceParams.put("priceTiers", request.priceTiers().stream()
                .map(PriceTier::name)
                .toList());
        genericDAO.insert("userPreference.insertPriceTiers", priceParams);

        // 5. 선호 언어 저장
        genericDAO.delete("userPreference.deleteLanguagesByUserId", userId);
        Map<String, Object> langParams = new HashMap<>();
        langParams.put("userId", userId);
        langParams.put("languages", request.languages().stream()
                .map(Language::name)
                .toList());
        genericDAO.insert("userPreference.insertLanguages", langParams);

        response.setSuccessResultData(true);
        return response;
    }
}

