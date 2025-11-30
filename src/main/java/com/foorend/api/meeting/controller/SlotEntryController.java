package com.foorend.api.meeting.controller;

import com.foorend.api.common.domain.BaseGenericRes;
import com.foorend.api.meeting.domain.MyMeetingResponse;
import com.foorend.api.meeting.domain.SlotEntry;
import com.foorend.api.meeting.domain.SlotEntryCancelRequest;
import com.foorend.api.meeting.domain.SlotEntryRequest;
import com.foorend.api.meeting.service.SlotEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 모임 참여 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meeting/entry")
@Tag(name = "모임 참여", description = "모임 참여 관련 API")
public class SlotEntryController {

    private final SlotEntryService slotEntryService;

    @GetMapping("/my")
    @Operation(summary = "내 모임 조회", description = "현재 참여 중인 모임을 조회합니다. 메인 화면에서 사용합니다.")
    public BaseGenericRes<MyMeetingResponse> getMyMeeting() {
        return slotEntryService.findMyActiveMeeting();
    }

    @PostMapping
    @Operation(summary = "모임 가입", description = "현재 로그인한 사용자가 모임에 가입합니다.")
    public BaseGenericRes<SlotEntry> join(@Valid @RequestBody SlotEntryRequest request) {
        return slotEntryService.joinSlot(request);
    }

    @DeleteMapping
    @Operation(summary = "모임 참여 취소", description = "현재 참여 중인 모임을 취소합니다. 모임 시작 전(OPEN)에만 가능합니다.")
    public BaseGenericRes<Boolean> cancel(@Valid @RequestBody SlotEntryCancelRequest request) {
        return slotEntryService.cancelMyEntry(request);
    }

    @PostMapping("/late")
    @Operation(summary = "늦어요 알림", description = "현재 참여 중인 모임에 늦어요 상태를 알립니다.")
    public BaseGenericRes<Boolean> setLate() {
        return slotEntryService.setMyLate();
    }
}

