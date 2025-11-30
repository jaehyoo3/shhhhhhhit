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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserPreferenceService 테스트")
class UserPreferenceServiceTest {

    @Mock
    private GenericDAO genericDAO;

    @InjectMocks
    private UserPreferenceService userPreferenceService;

    private User testUser;
    private Long testUserId = 1L;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(testUserId)
                .email("test@gmail.com")
                .userStatus(UserStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("선호 설정 조회 - 성공")
    void findPreference_Success() {
        // given
        List<String> priceTiers = List.of("LOW", "MID");
        List<String> languages = List.of("KO", "EN");

        when(genericDAO.selectList(eq("userPreference.findPriceTiersByUserId"), eq(testUserId)))
                .thenReturn(priceTiers);
        when(genericDAO.selectList(eq("userPreference.findLanguagesByUserId"), eq(testUserId)))
                .thenReturn(languages);

        // when
        BaseGenericRes<UserPreferenceResponse> result = userPreferenceService.findPreference(testUserId);

        // then
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().priceTiers()).containsExactly("LOW", "MID");
        assertThat(result.getData().languages()).containsExactly("KO", "EN");
        assertThat(result.getData().hasData()).isTrue();
    }

    @Test
    @DisplayName("선호 설정 조회 - 데이터 없음")
    void findPreference_NoData() {
        // given
        when(genericDAO.selectList(eq("userPreference.findPriceTiersByUserId"), eq(testUserId)))
                .thenReturn(List.of());
        when(genericDAO.selectList(eq("userPreference.findLanguagesByUserId"), eq(testUserId)))
                .thenReturn(List.of());

        // when
        BaseGenericRes<UserPreferenceResponse> result = userPreferenceService.findPreference(testUserId);

        // then
        assertThat(result.getData().hasData()).isFalse();
    }

    @Test
    @DisplayName("선호 설정 저장 - 성공")
    void savePreference_Success() {
        // given
        UserPreferenceRequest request = new UserPreferenceRequest(
                List.of(PriceTier.LOW, PriceTier.MID),
                List.of(Language.KO, Language.EN)
        );

        when(genericDAO.selectOne(eq("user.findByUserId"), eq(testUserId))).thenReturn(testUser);
        when(genericDAO.delete(anyString(), eq(testUserId))).thenReturn(1);
        when(genericDAO.insert(anyString(), any())).thenReturn(1);

        // when
        BaseGenericRes<Boolean> result = userPreferenceService.savePreference(testUserId, request);

        // then
        assertThat(result.getData()).isTrue();
        verify(genericDAO).delete(eq("userPreference.deletePriceTiersByUserId"), eq(testUserId));
        verify(genericDAO).delete(eq("userPreference.deleteLanguagesByUserId"), eq(testUserId));
        verify(genericDAO).insert(eq("userPreference.insertPriceTiers"), any());
        verify(genericDAO).insert(eq("userPreference.insertLanguages"), any());
    }

    @Test
    @DisplayName("선호 설정 저장 - 사용자 없음")
    void savePreference_UserNotFound() {
        // given
        UserPreferenceRequest request = new UserPreferenceRequest(
                List.of(PriceTier.LOW),
                List.of(Language.KO)
        );

        when(genericDAO.selectOne(eq("user.findByUserId"), eq(testUserId))).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> userPreferenceService.savePreference(testUserId, request))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("선호 설정 저장 - 가격대 미선택")
    void savePreference_NoPriceTiers() {
        // given
        UserPreferenceRequest request = new UserPreferenceRequest(
                List.of(),  // 빈 리스트
                List.of(Language.KO)
        );

        when(genericDAO.selectOne(eq("user.findByUserId"), eq(testUserId))).thenReturn(testUser);

        // when & then
        assertThatThrownBy(() -> userPreferenceService.savePreference(testUserId, request))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining("선호 가격대");
    }

    @Test
    @DisplayName("선호 설정 저장 - 언어 미선택")
    void savePreference_NoLanguages() {
        // given
        UserPreferenceRequest request = new UserPreferenceRequest(
                List.of(PriceTier.LOW),
                List.of()  // 빈 리스트
        );

        when(genericDAO.selectOne(eq("user.findByUserId"), eq(testUserId))).thenReturn(testUser);

        // when & then
        assertThatThrownBy(() -> userPreferenceService.savePreference(testUserId, request))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining("선호 언어");
    }

    @Test
    @DisplayName("선호 설정 저장 - null 요청")
    void savePreference_NullRequest() {
        // given
        UserPreferenceRequest request = new UserPreferenceRequest(null, null);

        when(genericDAO.selectOne(eq("user.findByUserId"), eq(testUserId))).thenReturn(testUser);

        // when & then
        assertThatThrownBy(() -> userPreferenceService.savePreference(testUserId, request))
                .isInstanceOf(GlobalException.class);
    }
}

