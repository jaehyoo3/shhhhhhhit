package com.foorend.api.user.controller;

import com.foorend.api.common.domain.BaseRes;
import com.foorend.api.common.util.SecurityUtil;
import com.foorend.api.user.service.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
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
}

