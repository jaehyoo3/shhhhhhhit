package com.foorend.api.user.controller;

import com.foorend.api.common.domain.BaseGenericRes;
import com.foorend.api.user.domain.UserPreferenceRequest;
import com.foorend.api.user.domain.UserPreferenceResponse;
import com.foorend.api.user.service.UserPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 선호 설정 API 컨트롤러
 */
@RestController
@RequestMapping("/api/user/preference")
@RequiredArgsConstructor
@Tag(name = "User Preference", description = "사용자 선호 설정 API")
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;

    /**
     * 내 선호 설정 조회
     */
    @GetMapping
    @Operation(summary = "내 선호 설정 조회", description = "현재 로그인한 사용자의 선호 가격대/언어를 조회합니다. hasData가 false면 등록 필요.")
    public BaseGenericRes<UserPreferenceResponse> getMyPreference() {
        return userPreferenceService.findMyPreference();
    }

    /**
     * 내 선호 설정 저장/수정
     */
    @PostMapping
    @Operation(summary = "내 선호 설정 저장", description = "선호 가격대/언어를 저장합니다. 기존 데이터가 있으면 덮어씁니다.")
    public BaseGenericRes<Boolean> saveMyPreference(@RequestBody UserPreferenceRequest request) {
        return userPreferenceService.saveMyPreference(request);
    }
}

