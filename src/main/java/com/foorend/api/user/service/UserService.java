package com.foorend.api.user.service;

import com.foorend.api.common.constants.ErrorCode;
import com.foorend.api.common.domain.BaseGenericRes;
import com.foorend.api.common.exception.GlobalException;
import com.foorend.api.common.repository.GenericDAO;
import com.foorend.api.common.util.CommonUtil;
import com.foorend.api.common.util.SecurityUtil;
import com.foorend.api.user.domain.User;
import com.foorend.api.user.domain.UserBasicInfoRequest;
import com.foorend.api.user.domain.UserProfileResponse;
import com.foorend.api.user.domain.UserProfileUpdateRequest;
import com.foorend.api.user.domain.UserSimpleInfo;
import com.foorend.api.user.domain.UserTraits;
import com.foorend.api.user.domain.WithdrawLog;
import com.foorend.api.user.domain.WithdrawRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 사용자 관리 Service
 */
@Service
public class UserService {

    private final GenericDAO genericDAO;

    public UserService(@Qualifier("mainDB") GenericDAO genericDAO) {
        this.genericDAO = genericDAO;
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
     * 내 프로필 상세 조회 (현재 로그인 사용자)
     */
    public BaseGenericRes<UserProfileResponse> findMyProfile() {
        Long userId = SecurityUtil.getCurrentUserId();
        return findUserProfile(userId);
    }

    /**
     * 사용자 프로필 상세 조회 (userId로)
     */
    @SuppressWarnings("unchecked")
    public BaseGenericRes<UserProfileResponse> findUserProfile(Long userId) {
        BaseGenericRes<UserProfileResponse> response = new BaseGenericRes<>();

        User user = (User) genericDAO.selectOne("user.findByUserId", userId);
        if (user == null) {
            response.setNoData();
            return response;
        }

        // 성향 정보 조회 및 JSON 파싱
        Object traitsAnswers = null;
        UserTraits traits = (UserTraits) genericDAO.selectOne("userTraits.findByUserId", userId);
        if (traits != null && traits.getAnswers() != null) {
            traitsAnswers = CommonUtil.convertFromJson(traits.getAnswers(), Object.class);
        }

        UserProfileResponse profile = UserProfileResponse.from(user, traitsAnswers);
        response.setSuccessResultData(profile);
        return response;
    }

    /**
     * 추가정보 입력 여부 확인
     * - 필수: name, gender, birthday
     */
    @SuppressWarnings("unchecked")
    public boolean hasBasicInfo(Long userId) {
        User user = (User) genericDAO.selectOne("user.findByUserId", userId);
        if (user == null) {
            return false;
        }

        return user.getName() != null && !user.getName().trim().isEmpty()
                && user.getGender() != null
                && user.getBirthday() != null;
    }

    /**
     * 추가정보 입력 여부 확인 (현재 로그인 사용자)
     */
    public boolean hasMyBasicInfo() {
        Long userId = SecurityUtil.getCurrentUserId();
        return hasBasicInfo(userId);
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
        userParams.put("name", request.name());
        userParams.put("phoneNumber", request.phoneNumber());
        userParams.put("gender", request.gender() != null ? request.gender().name() : null);
        userParams.put("birthday", request.birthday());
        userParams.put("relationshipStatus", request.relationshipStatus() != null ? request.relationshipStatus().name() : null);
        userParams.put("nationality", request.nationality());
        userParams.put("jobCategory", request.jobCategory() != null ? request.jobCategory().name() : null);

        genericDAO.update("user.updateBasicInfo", userParams);

        // 2. 성향 정보 저장/업데이트
        if (request.traitsAnswers() != null) {
            String traitsAnswersJson = CommonUtil.convertToJson(request.traitsAnswers());
            
            if (traitsAnswersJson != null && !traitsAnswersJson.isEmpty()) {
                Boolean traitsExists = (Boolean) genericDAO.selectOne("userTraits.existsByUserId", userId);

                if (traitsExists != null && traitsExists) {
                    Map<String, Object> traitsParams = new HashMap<>();
                    traitsParams.put("userId", userId);
                    traitsParams.put("answers", traitsAnswersJson);
                    genericDAO.update("userTraits.updateTraits", traitsParams);
                } else {
                    // 신규 성향 등록
                    UserTraits traits = UserTraits.builder()
                            .userId(userId)
                            .answers(traitsAnswersJson)
                            .build();
                    genericDAO.insert("userTraits.insertTraits", traits);
                }
            }
        }

        response.setSuccessResultData(true);
        return response;
    }

    /**
     * 내 프로필 수정 (현재 로그인 사용자)
     * - 보낸 값만 업데이트됨 (PATCH 방식)
     */
    @Transactional
    public BaseGenericRes<Boolean> updateMyProfile(UserProfileUpdateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        return updateProfile(userId, request);
    }

    /**
     * 회원 프로필 부분 수정 (PATCH)
     * - null이 아닌 값만 업데이트됨
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public BaseGenericRes<Boolean> updateProfile(Long userId, UserProfileUpdateRequest request) {
        BaseGenericRes<Boolean> response = new BaseGenericRes<>();

        // 모든 필드가 null이면 에러
        if (request.isEmpty()) {
            throw new GlobalException(ErrorCode.INVALID_PARAM, "수정할 항목이 없습니다.");
        }

        // 사용자 존재 여부 확인
        User user = (User) genericDAO.selectOne("user.findByUserId", userId);
        if (user == null) {
            throw new GlobalException(ErrorCode.USER_NOT_FOUND);
        }

        // 프로필 업데이트 (null 아닌 값만)
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("name", request.name());
        params.put("phoneNumber", request.phoneNumber());
        params.put("relationshipStatus", request.relationshipStatus() != null ? request.relationshipStatus().name() : null);
        params.put("jobCategory", request.jobCategory() != null ? request.jobCategory().name() : null);
        params.put("nationality", request.nationality());

        genericDAO.update("user.updateProfile", params);

        response.setSuccessResultData(true);
        return response;
    }

    /**
     * 내 계정 탈퇴 (현재 로그인 사용자)
     */
    @Transactional
    public BaseGenericRes<Boolean> withdrawMe(WithdrawRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        return withdraw(userId, request);
    }

    /**
     * 회원 탈퇴 (Soft Delete)
     * - confirmText가 "DELETE"인지 검증
     * - 탈퇴 로그 저장
     * - email, google_id 변경 (재가입 가능하도록)
     * - user_status 변경
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public BaseGenericRes<Boolean> withdraw(Long userId, WithdrawRequest request) {
        BaseGenericRes<Boolean> response = new BaseGenericRes<>();

        // 1. confirmText 검증
        if (!"DELETE".equals(request.confirmText())) {
            throw new GlobalException(ErrorCode.INVALID_PARAM, "탈퇴 확인 텍스트가 올바르지 않습니다.");
        }

        // 2. 사용자 조회
        User user = (User) genericDAO.selectOne("user.findByUserId", userId);
        if (user == null) {
            throw new GlobalException(ErrorCode.USER_NOT_FOUND);
        }

        // 3. 탈퇴 로그 저장
        WithdrawLog withdrawLog = WithdrawLog.builder()
                .userId(userId)
                .originalEmail(user.getEmail())
                .reason(request.reason())
                .build();
        genericDAO.insert("withdrawLog.insert", withdrawLog);

        // 4. 회원 탈퇴 처리 (email, google_id 변경 + status 변경)
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        int updatedRows = genericDAO.update("user.withdraw", params);

        if (updatedRows == 0) {
            throw new GlobalException(ErrorCode.INVALID_PARAM, "이미 탈퇴된 계정입니다.");
        }

        response.setSuccessResultData(true);
        return response;
    }
}
