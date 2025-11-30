package com.foorend.api.user.controller;

import com.foorend.api.common.domain.BaseGenericRes;
import com.foorend.api.user.domain.UserBasicInfoRequest;
import com.foorend.api.user.domain.UserProfileResponse;
import com.foorend.api.user.domain.UserProfileUpdateRequest;
import com.foorend.api.user.domain.UserSimpleInfo;
import com.foorend.api.user.domain.WithdrawRequest;
import com.foorend.api.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 API 컨트롤러
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 API")
public class UserController {

    private final UserService userService;

    /**
     * 내 정보 조회 (간단)
     */
    @GetMapping
    @Operation(summary = "내 정보 조회 (간단)", description = "현재 로그인한 사용자의 간단 정보를 조회합니다.")
    public BaseGenericRes<UserSimpleInfo> getMyInfo() {
        return userService.findMyInfo();
    }

    /**
     * 내 프로필 상세 조회
     */
    @GetMapping("/profile")
    @Operation(summary = "내 프로필 상세 조회", description = "현재 로그인한 사용자의 상세 프로필 정보를 조회합니다.")
    public BaseGenericRes<UserProfileResponse> getMyProfile() {
        return userService.findMyProfile();
    }

    /**
     * 회원 기본정보 입력
     */
    @PostMapping("/basic-info")
    @Operation(summary = "회원 기본정보 입력", description = "회원가입 후 기본정보와 성향 데이터를 저장합니다.")
    public BaseGenericRes<Boolean> saveBasicInfo(@Valid @RequestBody UserBasicInfoRequest request) {
        return userService.saveMyBasicInfo(request);
    }

    /**
     * 회원 프로필 수정 (부분 업데이트)
     */
    @PatchMapping("/profile")
    @Operation(summary = "회원 프로필 수정", description = "회원 프로필을 부분 수정합니다. 보낸 값만 업데이트됩니다.")
    public BaseGenericRes<Boolean> updateProfile(@RequestBody UserProfileUpdateRequest request) {
        return userService.updateMyProfile(request);
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 처리합니다. confirmText에 'DELETE'를 입력해야 합니다.")
    public BaseGenericRes<Boolean> withdraw(@Valid @RequestBody WithdrawRequest request) {
        return userService.withdrawMe(request);
    }
}
