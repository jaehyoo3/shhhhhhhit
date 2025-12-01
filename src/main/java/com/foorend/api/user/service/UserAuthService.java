package com.foorend.api.user.service;

import com.foorend.api.common.constants.ErrorCode;
import com.foorend.api.common.domain.BaseGenericRes;
import com.foorend.api.common.domain.BaseRes;
import com.foorend.api.common.exception.GlobalException;
import com.foorend.api.common.jwt.JwtTokenProvider;
import com.foorend.api.common.repository.GenericDAO;
import com.foorend.api.user.domain.TokenRefreshResponse;
import com.foorend.api.user.domain.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 사용자 인증 Service
 * - Refresh Token 관련 로직
 */
@Service
public class UserAuthService {

    private final GenericDAO genericDAO;
    private final JwtTokenProvider jwtTokenProvider;

    public UserAuthService(@Qualifier("mainDB") GenericDAO genericDAO,
                          JwtTokenProvider jwtTokenProvider) {
        this.genericDAO = genericDAO;
        this.jwtTokenProvider = jwtTokenProvider;
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

    /**
     * Refresh Token으로 새로운 Access Token과 Refresh Token 발급
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public BaseGenericRes<TokenRefreshResponse> refreshToken(String refreshToken) {
        BaseGenericRes<TokenRefreshResponse> response = new BaseGenericRes<>();

        // 1. Refresh Token 검증
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new GlobalException(ErrorCode.AUTH_EMPTY_JWT, "Refresh Token이 없습니다.");
        }

        // 2. 토큰 유효성 검증
        jwtTokenProvider.validateToken(refreshToken);

        // 3. Refresh Token 타입 확인
        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new GlobalException(ErrorCode.AUTH_INVALID_JWT, "Refresh Token이 아닙니다.");
        }

        // 4. DB에서 Refresh Token으로 사용자 조회
        User user = (User) genericDAO.selectOne("user.findByRefreshToken", refreshToken);
        if (user == null) {
            throw new GlobalException(ErrorCode.AUTH_INVALID_JWT, "유효하지 않은 Refresh Token입니다.");
        }

        // 5. 사용자 상태 확인 (탈퇴/정지 체크)
        if (user.getUserStatus() == null || user.getUserStatus() != com.foorend.api.user.domain.UserStatus.ACTIVE) {
            throw new GlobalException(ErrorCode.USER_NOT_FOUND, "사용할 수 없는 계정입니다.");
        }

        // 6. 새로운 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getEmail());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getUserId(), user.getEmail());

        // 7. 새로운 Refresh Token을 DB에 저장
        Map<String, Object> params = new HashMap<>();
        params.put("userId", user.getUserId());
        params.put("refreshToken", newRefreshToken);
        int updatedRows = genericDAO.update("user.updateRefreshToken", params);

        if (updatedRows == 0) {
            throw new GlobalException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        // 8. 응답 생성
        TokenRefreshResponse tokenResponse = new TokenRefreshResponse(newAccessToken, newRefreshToken);
        response.setSuccessResultData(tokenResponse);
        return response;
    }
}

