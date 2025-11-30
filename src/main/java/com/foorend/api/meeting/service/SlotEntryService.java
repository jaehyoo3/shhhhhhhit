package com.foorend.api.meeting.service;

import com.foorend.api.common.constants.ErrorCode;
import com.foorend.api.common.domain.BaseGenericRes;
import com.foorend.api.common.exception.GlobalException;
import com.foorend.api.common.repository.GenericDAO;
import com.foorend.api.common.util.SecurityUtil;
import com.foorend.api.meeting.domain.MeetingMember;
import com.foorend.api.meeting.domain.MeetingSlot;
import com.foorend.api.meeting.domain.MyMeetingResponse;
import com.foorend.api.meeting.domain.SlotEntry;
import com.foorend.api.meeting.domain.SlotEntryCancelRequest;
import com.foorend.api.meeting.domain.SlotEntryRequest;
import com.foorend.api.meeting.domain.SlotEntryStatus;
import com.foorend.api.user.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 모임 참여 Service
 */
@Service
public class SlotEntryService {

    private final GenericDAO genericDAO;
    private final UserService userService;

    public SlotEntryService(@Qualifier("mainDB") GenericDAO genericDAO, UserService userService) {
        this.genericDAO = genericDAO;
        this.userService = userService;
    }

    /**
     * 추가정보 입력 여부 확인
     */
    private void checkBasicInfoRequired() {
        if (!userService.hasMyBasicInfo()) {
            throw new GlobalException(ErrorCode.INVALID_PARAM, "모임 서비스를 이용하려면 추가정보를 입력해주세요.");
        }
    }

    /**
     * 모임 가입 (현재 로그인 사용자)
     */
    @Transactional
    public BaseGenericRes<SlotEntry> joinSlot(SlotEntryRequest request) {
        checkBasicInfoRequired();
        Long userId = SecurityUtil.getCurrentUserId();
        return join(request.slotId(), userId);
    }

    /**
     * 모임 가입
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public BaseGenericRes<SlotEntry> join(Long slotId, Long userId) {
        BaseGenericRes<SlotEntry> response = new BaseGenericRes<>();

        // 1. 모임 존재 및 상태 확인
        MeetingSlot slot = (MeetingSlot) genericDAO.selectOne("meetingSlot.findById", slotId);
        if (slot == null) {
            throw new GlobalException(ErrorCode.NO_DATA, "모임을 찾을 수 없습니다.");
        }

        // 2. 모임 시간 지났는지 확인
        LocalDateTime meetDateTime = LocalDateTime.of(slot.getMeetDate(), slot.getMeetTime());
        if (meetDateTime.isBefore(LocalDateTime.now())) {
            throw new GlobalException(ErrorCode.INVALID_PARAM, "이미 종료된 모임입니다.");
        }

        // 3. 모임 상태 확인 (OPEN만 가입 가능)
        if (slot.getStatus() != com.foorend.api.meeting.domain.MeetingSlotStatus.OPEN) {
            throw new GlobalException(ErrorCode.INVALID_PARAM, "가입할 수 없는 모임 상태입니다.");
        }

        // 4. 정원 체크
        if (slot.getMaxCapacity() != null && slot.getCurrentCount() >= slot.getMaxCapacity()) {
            throw new GlobalException(ErrorCode.INVALID_PARAM, "모임 정원이 가득 찼습니다.");
        }

        // 5. 이미 가입했는지 확인
        Map<String, Object> checkParams = new HashMap<>();
        checkParams.put("slotId", slotId);
        checkParams.put("userId", userId);

        Boolean alreadyJoined = (Boolean) genericDAO.selectOne("slotEntry.existsBySlotIdAndUserId", checkParams);
        if (alreadyJoined != null && alreadyJoined) {
            throw new GlobalException(ErrorCode.DUPLICATION_ERROR, "이미 가입한 모임입니다.");
        }

        // 6. 취소했던 모임인지 확인
        SlotEntry canceledEntry = (SlotEntry) genericDAO.selectOne("slotEntry.findCanceledEntry", checkParams);
        SlotEntry entry;

        if (canceledEntry != null) {
            // 취소했던 모임 재가입 (UPDATE)
            genericDAO.update("slotEntry.rejoin", checkParams);
            entry = canceledEntry;
            entry.setStatus(SlotEntryStatus.JOINED);
            entry.setCancelReason(null);
        } else {
            // 신규 가입 (INSERT)
            entry = SlotEntry.builder()
                    .slotId(slotId)
                    .userId(userId)
                    .status(SlotEntryStatus.JOINED)
                    .build();
            genericDAO.insert("slotEntry.insert", entry);
        }

        // 7. 모임 현재 인원 증가
        int updated = genericDAO.update("meetingSlot.incrementCount", slotId);
        if (updated == 0) {
            throw new GlobalException(ErrorCode.INVALID_PARAM, "모임 정원이 가득 찼습니다.");
        }

        response.setSuccessResultData(entry);
        return response;
    }

    /**
     * 내가 참여 중인 모임 조회 (메인 화면용)
     * - 모임 시작 시간 → CONFIRMED 또는 CANCELED
     * - 시작 + 12시간 후 → FINISHED
     * - 식당/멤버 정보는 하루 전부터 공개
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public BaseGenericRes<MyMeetingResponse> findMyActiveMeeting() {
        checkBasicInfoRequired();
        Long userId = SecurityUtil.getCurrentUserId();
        BaseGenericRes<MyMeetingResponse> response = new BaseGenericRes<>();

        // 1. 내가 참여 중인 모임 ID 조회 (OPEN 또는 CONFIRMED 상태)
        Long slotId = (Long) genericDAO.selectOne("slotEntry.findMyJoinedSlotId", userId);

        if (slotId != null) {
            // 2. 모임 시작 시간 됐으면 CONFIRMED 처리 (인원 있을 때)
            genericDAO.update("meetingSlot.confirmIfStarted", slotId);

            // 3. 시작 + 12시간 지났으면 FINISHED 처리
            int finished = genericDAO.update("meetingSlot.finishIfExpired", slotId);

            if (finished > 0) {
                // 모임이 FINISHED 됨 → 빈 응답
                response.setSuccessResultData(MyMeetingResponse.empty());
                return response;
            }
        }

        // 4. 유효한 모임 상세 조회 (OPEN 또는 CONFIRMED)
        Map<String, Object> result = (Map<String, Object>) genericDAO.selectOne("slotEntry.findMyActiveEntry", userId);

        if (result == null) {
            response.setSuccessResultData(MyMeetingResponse.empty());
            return response;
        }

        // 5. 기본 정보 파싱
        Long entryId = result.get("entry_id") != null ? ((Number) result.get("entry_id")).longValue() : null;
        Long resultSlotId = result.get("slot_id") != null ? ((Number) result.get("slot_id")).longValue() : null;
        String locationArea = (String) result.get("location_area");

        LocalDate meetDate = result.get("meet_date") != null
                ? LocalDate.parse(result.get("meet_date").toString())
                : null;
        String dayOfWeek = meetDate != null
                ? meetDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN)
                : null;
        LocalTime meetTime = result.get("meet_time") != null
                ? LocalTime.parse(result.get("meet_time").toString())
                : null;

        // 6. 하루 전 공개 여부 체크
        boolean isInfoRevealed = false;
        if (meetDate != null) {
            LocalDate revealDate = meetDate.minusDays(1);
            isInfoRevealed = !LocalDate.now().isBefore(revealDate);
        }

        // 7. 식당 정보 (하루 전부터 공개)
        String restaurantName = null;
        String restaurantAddr = null;
        if (isInfoRevealed) {
            restaurantName = (String) result.get("restaurant_name");
            restaurantAddr = (String) result.get("restaurant_addr");
        }

        // 8. 멤버 정보 (하루 전부터 공개)
        List<MeetingMember> members = List.of();
        int lateCount = 0;
        if (isInfoRevealed && resultSlotId != null) {
            List<Map<String, Object>> memberMaps = (List<Map<String, Object>>) genericDAO.selectList("slotEntry.findMembersBySlotId", resultSlotId);
            members = memberMaps.stream()
                    .map(m -> new MeetingMember(
                            (String) m.get("job_category"),
                            (String) m.get("nationality")
                    ))
                    .toList();

            // 9. 늦어요 인원 수 조회
            Integer count = (Integer) genericDAO.selectOne("slotEntry.countLateBySlotId", resultSlotId);
            lateCount = count != null ? count : 0;
        }

        MyMeetingResponse meetingResponse = new MyMeetingResponse(
                entryId,
                resultSlotId,
                locationArea,
                meetDate,
                dayOfWeek,
                meetTime,
                restaurantName,
                restaurantAddr,
                members,
                lateCount,
                isInfoRevealed,
                true
        );

        response.setSuccessResultData(meetingResponse);
        return response;
    }

    /**
     * 모임 참여 취소 (현재 로그인 사용자)
     * - OPEN 상태 모임만 취소 가능
     * - CONFIRMED(진행중) 모임은 취소 불가
     */
    @Transactional
    public BaseGenericRes<Boolean> cancelMyEntry(SlotEntryCancelRequest request) {
        checkBasicInfoRequired();
        Long userId = SecurityUtil.getCurrentUserId();
        BaseGenericRes<Boolean> response = new BaseGenericRes<>();

        // 1. 내가 참여 중인 모임 ID 조회
        Long slotId = (Long) genericDAO.selectOne("slotEntry.findMyJoinedSlotId", userId);
        if (slotId == null) {
            throw new GlobalException(ErrorCode.NO_DATA, "참여 중인 모임이 없습니다.");
        }

        // 2. 취소 처리 (OPEN 상태 모임만)
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("cancelReason", request.cancelReason());

        int updated = genericDAO.update("slotEntry.cancel", params);
        if (updated == 0) {
            throw new GlobalException(ErrorCode.INVALID_PARAM, "이미 시작된 모임은 취소할 수 없습니다.");
        }

        // 3. 모임 현재 인원 감소
        genericDAO.update("meetingSlot.decrementCount", slotId);

        response.setSuccessResultData(true);
        return response;
    }

    /**
     * 늦어요 상태로 변경 (현재 로그인 사용자)
     * - JOINED 상태에서만 가능
     * - OPEN 또는 CONFIRMED 상태 모임만
     */
    @Transactional
    public BaseGenericRes<Boolean> setMyLate() {
        checkBasicInfoRequired();
        Long userId = SecurityUtil.getCurrentUserId();
        BaseGenericRes<Boolean> response = new BaseGenericRes<>();

        // 1. 내가 참여 중인 모임 ID 조회
        Long slotId = (Long) genericDAO.selectOne("slotEntry.findMyJoinedSlotId", userId);
        if (slotId == null) {
            throw new GlobalException(ErrorCode.NO_DATA, "참여 중인 모임이 없습니다.");
        }

        // 2. 늦어요 상태로 변경
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        int updated = genericDAO.update("slotEntry.setLate", params);
        if (updated == 0) {
            throw new GlobalException(ErrorCode.INVALID_PARAM, "늦어요 상태로 변경할 수 없습니다.");
        }

        response.setSuccessResultData(true);
        return response;
    }
}

