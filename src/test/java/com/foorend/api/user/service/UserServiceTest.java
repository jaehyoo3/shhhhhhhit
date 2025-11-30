package com.foorend.api.user.service;

import com.foorend.api.common.constants.ErrorCode;
import com.foorend.api.common.domain.BaseGenericRes;
import com.foorend.api.common.exception.GlobalException;
import com.foorend.api.common.repository.GenericDAO;
import com.foorend.api.user.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import com.foorend.api.common.util.SecurityUtil;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class UserServiceTest {

    @Mock
    private GenericDAO genericDAO;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Long testUserId = 1L;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(testUserId)
                .email("test@gmail.com")
                .name("테스트")
                .gender(Gender.MALE)
                .birthday(LocalDate.of(1995, 5, 15))
                .nationality("KR")
                .jobCategory(JobCategory.TECH)
                .userStatus(UserStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("프로필 조회 - 성공")
    void findUserProfile_Success() {
        // given
        when(genericDAO.selectOne(eq("user.findByUserId"), eq(testUserId))).thenReturn(testUser);
        when(genericDAO.selectOne(eq("userTraits.findByUserId"), eq(testUserId))).thenReturn(null);

        // when
        BaseGenericRes<UserProfileResponse> result = userService.findUserProfile(testUserId);

        // then
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().email()).isEqualTo("test@gmail.com");
        assertThat(result.getData().name()).isEqualTo("테스트");
        assertThat(result.getData().jobCategory()).isEqualTo(JobCategory.TECH);
    }

    @Test
    @DisplayName("프로필 조회 - 사용자 없음")
    void findUserProfile_UserNotFound() {
        // given
        when(genericDAO.selectOne(eq("user.findByUserId"), eq(testUserId))).thenReturn(null);

        // when
        BaseGenericRes<UserProfileResponse> result = userService.findUserProfile(testUserId);

        // then
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("기본정보 입력 - 성공")
    void saveBasicInfo_Success() {
        // given
        UserBasicInfoRequest request = new UserBasicInfoRequest(
                "홍길동",
                "01012345678",
                Gender.MALE,
                LocalDate.of(1995, 5, 15),
                RelationshipStatus.SINGLE,
                "KR",
                JobCategory.TECH,
                null
        );

        when(genericDAO.selectOne(eq("user.findByUserId"), eq(testUserId))).thenReturn(testUser);
        when(genericDAO.update(eq("user.updateBasicInfo"), any())).thenReturn(1);

        // when
        BaseGenericRes<Boolean> result = userService.saveBasicInfo(testUserId, request);

        // then
        assertThat(result.getData()).isTrue();
        verify(genericDAO).update(eq("user.updateBasicInfo"), any());
    }

    @Test
    @DisplayName("기본정보 입력 - 사용자 없음")
    void saveBasicInfo_UserNotFound() {
        // given
        UserBasicInfoRequest request = new UserBasicInfoRequest(
                "홍길동", null, Gender.MALE, LocalDate.of(1995, 5, 15),
                null, null, null, null
        );

        when(genericDAO.selectOne(eq("user.findByUserId"), eq(testUserId))).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> userService.saveBasicInfo(testUserId, request))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("프로필 수정 - 성공")
    void updateProfile_Success() {
        // given
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                "수정된이름",
                null,
                null,
                JobCategory.ART_CULTURE,
                null
        );

        when(genericDAO.selectOne(eq("user.findByUserId"), eq(testUserId))).thenReturn(testUser);
        when(genericDAO.update(eq("user.updateProfile"), any())).thenReturn(1);

        // when
        BaseGenericRes<Boolean> result = userService.updateProfile(testUserId, request);

        // then
        assertThat(result.getData()).isTrue();
    }

    @Test
    @DisplayName("프로필 수정 - 빈 요청")
    void updateProfile_EmptyRequest() {
        // given
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(null, null, null, null, null);

        // when & then
        assertThatThrownBy(() -> userService.updateProfile(testUserId, request))
                .isInstanceOf(GlobalException.class);
    }

    @Test
    @DisplayName("회원 탈퇴 - 성공")
    void withdraw_Success() {
        // given
        WithdrawRequest request = new WithdrawRequest("DELETE", "서비스 이용 종료");

        when(genericDAO.selectOne(eq("user.findByUserId"), eq(testUserId))).thenReturn(testUser);
        when(genericDAO.insert(eq("withdrawLog.insert"), any())).thenReturn(1);
        when(genericDAO.update(eq("user.withdraw"), any())).thenReturn(1);

        // when
        BaseGenericRes<Boolean> result = userService.withdraw(testUserId, request);

        // then
        assertThat(result.getData()).isTrue();
        verify(genericDAO).insert(eq("withdrawLog.insert"), any());
        verify(genericDAO).update(eq("user.withdraw"), any());
    }

    @Test
    @DisplayName("회원 탈퇴 - 확인 텍스트 불일치")
    void withdraw_InvalidConfirmText() {
        // given
        WithdrawRequest request = new WithdrawRequest("WRONG", "탈퇴 사유");

        // when & then
        assertThatThrownBy(() -> userService.withdraw(testUserId, request))
                .isInstanceOf(GlobalException.class);
    }

    @Test
    @DisplayName("회원 탈퇴 - 이미 탈퇴된 계정")
    void withdraw_AlreadyWithdrawn() {
        // given
        WithdrawRequest request = new WithdrawRequest("DELETE", "탈퇴 사유");

        when(genericDAO.selectOne(eq("user.findByUserId"), eq(testUserId))).thenReturn(testUser);
        when(genericDAO.insert(eq("withdrawLog.insert"), any())).thenReturn(1);
        when(genericDAO.update(eq("user.withdraw"), any())).thenReturn(0);

        // when & then
        assertThatThrownBy(() -> userService.withdraw(testUserId, request))
                .isInstanceOf(GlobalException.class);
    }
}

