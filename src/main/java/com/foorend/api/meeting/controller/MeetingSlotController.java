package com.foorend.api.meeting.controller;

import com.foorend.api.common.domain.BaseGenericListRes;
import com.foorend.api.meeting.domain.MeetingSlotListResponse;
import com.foorend.api.meeting.service.MeetingSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 모임 일정 API 컨트롤러
 */
@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
@Tag(name = "Meeting", description = "모임 일정 API")
public class MeetingSlotController {

    private final MeetingSlotService meetingSlotService;

    /**
     * 예정된 모임 조회 (현재 시간 이후만)
     */
    @GetMapping
    @Operation(summary = "예정된 모임 조회", description = "현재 시간 이후, 정원 미달인 모임 목록을 조회합니다.")
    public BaseGenericListRes<MeetingSlotListResponse> findUpcoming() {
        return meetingSlotService.findUpcoming();
    }
}
