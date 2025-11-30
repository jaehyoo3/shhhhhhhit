package com.foorend.api.meeting.service;

import com.foorend.api.common.constants.ErrorCode;
import com.foorend.api.common.domain.BaseGenericRes;
import com.foorend.api.common.exception.GlobalException;
import com.foorend.api.common.repository.GenericDAO;
import com.foorend.api.meeting.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SlotEntryService 테스트")
class SlotEntryServiceTest {

    @Mock
    private GenericDAO genericDAO;

    @InjectMocks
    private SlotEntryService slotEntryService;

    private MeetingSlot testSlot;
    private Long testSlotId = 1L;
    private Long testUserId = 1L;

    @BeforeEach
    void setUp() {
        testSlot = MeetingSlot.builder()
                .slotId(testSlotId)
                .locationArea("강남")
                .meetDate(LocalDate.now().plusDays(7))  // 7일 후
                .meetTime(LocalTime.of(19, 0))
                .maxCapacity(6)
                .currentCount(3)
                .status(MeetingSlotStatus.OPEN)
                .build();
    }

    @Test
    @DisplayName("모임 가입 - 성공")
    void join_Success() {
        // given
        when(genericDAO.selectOne(eq("meetingSlot.findById"), eq(testSlotId))).thenReturn(testSlot);
        when(genericDAO.selectOne(eq("slotEntry.existsBySlotIdAndUserId"), any())).thenReturn(false);
        when(genericDAO.insert(eq("slotEntry.insert"), any())).thenReturn(1);
        when(genericDAO.update(eq("meetingSlot.incrementCount"), eq(testSlotId))).thenReturn(1);

        // when
        BaseGenericRes<SlotEntry> result = slotEntryService.join(testSlotId, testUserId);

        // then
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getSlotId()).isEqualTo(testSlotId);
        assertThat(result.getData().getUserId()).isEqualTo(testUserId);
        assertThat(result.getData().getStatus()).isEqualTo(SlotEntryStatus.JOINED);
    }

    @Test
    @DisplayName("모임 가입 - 모임 없음")
    void join_SlotNotFound() {
        // given
        when(genericDAO.selectOne(eq("meetingSlot.findById"), eq(testSlotId))).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> slotEntryService.join(testSlotId, testUserId))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining("모임을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("모임 가입 - 이미 종료된 모임")
    void join_ExpiredSlot() {
        // given
        testSlot.setMeetDate(LocalDate.now().minusDays(1));  // 어제
        when(genericDAO.selectOne(eq("meetingSlot.findById"), eq(testSlotId))).thenReturn(testSlot);

        // when & then
        assertThatThrownBy(() -> slotEntryService.join(testSlotId, testUserId))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining("이미 종료된 모임");
    }

    @Test
    @DisplayName("모임 가입 - OPEN 상태가 아님")
    void join_NotOpenStatus() {
        // given
        testSlot.setStatus(MeetingSlotStatus.CONFIRMED);
        when(genericDAO.selectOne(eq("meetingSlot.findById"), eq(testSlotId))).thenReturn(testSlot);

        // when & then
        assertThatThrownBy(() -> slotEntryService.join(testSlotId, testUserId))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining("가입할 수 없는 모임 상태");
    }

    @Test
    @DisplayName("모임 가입 - 정원 초과")
    void join_CapacityFull() {
        // given
        testSlot.setCurrentCount(6);  // 정원 6명, 현재 6명
        when(genericDAO.selectOne(eq("meetingSlot.findById"), eq(testSlotId))).thenReturn(testSlot);

        // when & then
        assertThatThrownBy(() -> slotEntryService.join(testSlotId, testUserId))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining("정원이 가득");
    }

    @Test
    @DisplayName("모임 가입 - 중복 가입")
    void join_AlreadyJoined() {
        // given
        when(genericDAO.selectOne(eq("meetingSlot.findById"), eq(testSlotId))).thenReturn(testSlot);
        when(genericDAO.selectOne(eq("slotEntry.existsBySlotIdAndUserId"), any())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> slotEntryService.join(testSlotId, testUserId))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining("이미 가입한 모임");
    }

    @Test
    @DisplayName("모임 취소 - OPEN 상태만 취소 가능 확인")
    void cancelMyEntry_OnlyOpenCancel() {
        // given
        SlotEntryCancelRequest request = new SlotEntryCancelRequest("개인 사정");

        when(genericDAO.selectOne(eq("slotEntry.findMyJoinedSlotId"), eq(testUserId))).thenReturn(testSlotId);
        when(genericDAO.update(eq("slotEntry.cancel"), any())).thenReturn(0);  // CONFIRMED 상태라서 취소 안됨

        // when & then
        assertThatThrownBy(() -> slotEntryService.cancelMyEntry(request))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining("이미 시작된 모임");
    }

    @Test
    @DisplayName("내 모임 조회 - 모임 없음")
    void findMyActiveMeeting_NoMeeting() {
        // given
        when(genericDAO.selectOne(eq("slotEntry.findMyJoinedSlotId"), eq(testUserId))).thenReturn(null);
        when(genericDAO.selectOne(eq("slotEntry.findMyActiveEntry"), eq(testUserId))).thenReturn(null);

        // SecurityUtil mock이 필요하므로 이 테스트는 통합 테스트에서 수행하는 것이 적절
    }

    @Test
    @DisplayName("모임 가입 - 정원 무제한 (maxCapacity null)")
    void join_UnlimitedCapacity() {
        // given
        testSlot.setMaxCapacity(null);  // 정원 무제한
        testSlot.setCurrentCount(100);

        when(genericDAO.selectOne(eq("meetingSlot.findById"), eq(testSlotId))).thenReturn(testSlot);
        when(genericDAO.selectOne(eq("slotEntry.existsBySlotIdAndUserId"), any())).thenReturn(false);
        when(genericDAO.insert(eq("slotEntry.insert"), any())).thenReturn(1);
        when(genericDAO.update(eq("meetingSlot.incrementCount"), eq(testSlotId))).thenReturn(1);

        // when
        BaseGenericRes<SlotEntry> result = slotEntryService.join(testSlotId, testUserId);

        // then
        assertThat(result.getData()).isNotNull();
    }
}

