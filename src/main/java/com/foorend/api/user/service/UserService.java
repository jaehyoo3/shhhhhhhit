package com.foorend.api.user.service;

import com.foorend.api.common.constants.ErrorCode;
import com.foorend.api.common.domain.BaseGenericRes;
import com.foorend.api.common.domain.BaseRes;
import com.foorend.api.common.exception.GlobalException;
import com.foorend.api.common.repository.GenericDAO;
import com.foorend.api.common.util.SecurityUtil;
import com.foorend.api.user.domain.User;
import com.foorend.api.user.domain.UserBasicInfoRequest;
import com.foorend.api.user.domain.UserSimpleInfo;
import com.foorend.api.user.domain.UserTraits;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 사용자 관리 Service
 */
@Slf4j
@Service
public class UserService {

    private final GenericDAO genericDAO;

    public UserService(@Qualifier("mainDB") GenericDAO genericDAO) {
        this.genericDAO = genericDAO;
    }

    /**
     * Refresh Token 업데이트
     */
    @SuppressWarnings("unchecked")
    public BaseRes updateRefreshToken(Long userId, String refreshToken) {
        BaseRes response = new BaseRes();

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("refreshToken", refreshToken);

        genericDAO.update("user.updateRefreshToken", params);

        response.setSuccess();
        return response;
    }

    /**
     * Refresh Token으로 사용자 조회 (토큰 갱신 시 사용)
     */
    @SuppressWarnings("unchecked")
    public BaseGenericRes<User> findUserByRefreshToken(String refreshToken) {
        BaseGenericRes<User> response = new BaseGenericRes<>();

        User user = (User) genericDAO.selectOne("user.findByRefreshToken", refreshToken);

        if (user == null) {
            response.setNoData();
            return response;
        }

        response.setSuccessResultData(user);
        return response;
    }

    /**
     * 사용자 간단 정보 조회 (userId로)
     */
    @SuppressWarnings("unchecked")
    public BaseGenericRes<UserSimpleInfo> findUserSimpleInfo(Long userId) {
        BaseGenericRes<UserSimpleInfo> response = new BaseGenericRes<>();

        UserSimpleInfo userInfo = (UserSimpleInfo) genericDAO.selectOne("user.findSimpleInfoByUserId", userId);

        if (userInfo == null) {
            response.setNoData();
            return response;
        }

        response.setSuccessResultData(userInfo);
        return response;
    }

    /**
     * 내 정보 조회 (현재 로그인 사용자)
     */
    public BaseGenericRes<UserSimpleInfo> findMyInfo() {
        Long userId = SecurityUtil.getCurrentUserId();
        return findUserSimpleInfo(userId);
    }

    /**
     * 내 기본정보 입력 (현재 로그인 사용자)
     */
    @Transactional
    public BaseGenericRes<Boolean> saveMyBasicInfo(UserBasicInfoRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        return saveBasicInfo(userId, request);
    }

    /**
     * 회원 기본정보 입력 (User + Traits)
     * - User 업데이트 + Traits 저장이 하나의 트랜잭션
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public BaseGenericRes<Boolean>  saveBasicInfo(Long userId, UserBasicInfoRequest request) {
        BaseGenericRes<Boolean> response = new BaseGenericRes<>();

        // 사용자 존재 여부 확인
        User user = (User) genericDAO.selectOne("user.findByUserId", userId);
        if (user == null) {
            throw new GlobalException(ErrorCode.USER_NOT_FOUND);
        }

        // 1. 사용자 기본정보 업데이트
        Map<String, Object> userParams = new HashMap<>();
        userParams.put("userId", userId);
        userParams.put("name", request.getName());
        userParams.put("phoneNumber", request.getPhoneNumber());
        userParams.put("gender", request.getGender() != null ? request.getGender().name() : null);
        userParams.put("birthday", request.getBirthday());
        userParams.put("relationshipStatus", request.getRelationshipStatus() != null ? request.getRelationshipStatus().name() : null);
        userParams.put("nationality", request.getNationality());
        userParams.put("jobTitle", request.getJobTitle());

        genericDAO.update("user.updateBasicInfo", userParams);

        // 2. 성향 정보 저장/업데이트
        if (request.getTraitsAnswers() != null && !request.getTraitsAnswers().isEmpty()) {
            Boolean traitsExists = (Boolean) genericDAO.selectOne("userTraits.existsByUserId", userId);

            if (traitsExists != null && traitsExists) {
                Map<String, Object> traitsParams = new HashMap<>();
                traitsParams.put("userId", userId);
                traitsParams.put("answers", request.getTraitsAnswers());
                genericDAO.update("userTraits.updateTraits", traitsParams);
            } else {
                // 신규 성향 등록
                UserTraits traits = UserTraits.builder()
                        .userId(userId)
                        .answers(request.getTraitsAnswers())
                        .build();
                genericDAO.insert("userTraits.insertTraits", traits);
            }
        }

        response.setSuccessResultData(true);
        return response;
    }
}

