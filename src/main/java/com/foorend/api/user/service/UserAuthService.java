package com.foorend.api.user.service;

import com.foorend.api.common.constants.ErrorCode;
import com.foorend.api.common.domain.BaseGenericRes;
import com.foorend.api.common.domain.BaseRes;
import com.foorend.api.common.exception.GlobalException;
import com.foorend.api.common.repository.GenericDAO;
import com.foorend.api.user.domain.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 사용자 인증 Service
 * - Refresh Token 관련 로직
 */
@Service
public class UserAuthService {

    private final GenericDAO genericDAO;

    public UserAuthService(@Qualifier("mainDB") GenericDAO genericDAO) {
        this.genericDAO = genericDAO;
    }

    /**
     * Refresh Token 업데이트
     */
    public BaseRes updateRefreshToken(Long userId, String refreshToken) {
        BaseRes response = new BaseRes();

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("refreshToken", refreshToken);

        int updatedRows = genericDAO.update("user.updateRefreshToken", params);

        if (updatedRows == 0) {
            throw new GlobalException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        response.setSuccess();
        return response;
    }

    /**
     * Refresh Token으로 사용자 조회 (토큰 갱신 시 사용)
     */
    @SuppressWarnings("unchecked")
    public BaseGenericRes<User> findUserByRefreshToken(String refreshToken) {
        BaseGenericRes<User> response = new BaseGenericRes<>();

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new GlobalException(ErrorCode.AUTH_EMPTY_JWT, "Refresh Token이 없습니다.");
        }

        User user = (User) genericDAO.selectOne("user.findByRefreshToken", refreshToken);

        if (user == null) {
            throw new GlobalException(ErrorCode.AUTH_INVALID_JWT, "유효하지 않은 Refresh Token입니다.");
        }

        response.setSuccessResultData(user);
        return response;
    }

    /**
     * Refresh Token 삭제 (로그아웃 시)
     */
    public BaseRes clearRefreshToken(Long userId) {
        BaseRes response = new BaseRes();

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("refreshToken", null);

        int updatedRows = genericDAO.update("user.updateRefreshToken", params);

        if (updatedRows == 0) {
            throw new GlobalException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        response.setSuccess();
        return response;
    }
}

