package com.foorend.api.user.controller;

import com.foorend.api.common.domain.BaseGenericRes;
import com.foorend.api.common.domain.BaseRes;
import com.foorend.api.user.domain.RefreshTokenRequest;
import com.foorend.api.user.domain.TokenRefreshResponse;
import com.foorend.api.common.util.SecurityUtil;
import com.foorend.api.user.service.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 인증 API 컨트롤러
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User Auth", description = "사용자 인증 API")
public class UserAuthController {

    private final UserAuthService userAuthService;

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 로그인한 사용자를 로그아웃합니다. Refresh Token이 무효화됩니다.")
    public BaseRes logout() {
        Long userId = SecurityUtil.getCurrentUserId();
        return userAuthService.clearRefreshToken(userId);
    }

    /**
     * Refresh Token으로 새로운 Access Token과 Refresh Token 발급
     */
    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급합니다.")
    public BaseGenericRes<TokenRefreshResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return userAuthService.refreshToken(request.refreshToken());
    }
}

