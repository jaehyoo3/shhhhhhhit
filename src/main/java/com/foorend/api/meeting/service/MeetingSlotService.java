package com.foorend.api.meeting.service;

import com.foorend.api.common.constants.ErrorCode;
import com.foorend.api.common.domain.BaseGenericListRes;
import com.foorend.api.common.exception.GlobalException;
import com.foorend.api.common.repository.GenericDAO;
import com.foorend.api.common.util.SecurityUtil;
import com.foorend.api.meeting.domain.MeetingSlot;
import com.foorend.api.meeting.domain.MeetingSlotListResponse;
import com.foorend.api.user.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 모임 일정 Service
 */
@Service
public class MeetingSlotService {

    private final GenericDAO genericDAO;
    private final UserService userService;

    public MeetingSlotService(@Qualifier("mainDB") GenericDAO genericDAO, UserService userService) {
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
     * 예정된 모임 조회 (현재 시간 이후, 정원 미달만)
     */
    @SuppressWarnings("unchecked")
    public BaseGenericListRes<MeetingSlotListResponse> findUpcoming() {
        checkBasicInfoRequired();
        BaseGenericListRes<MeetingSlotListResponse> response = new BaseGenericListRes<>();

        List<MeetingSlot> slots = (List<MeetingSlot>) genericDAO.selectList("meetingSlot.findUpcoming");

        List<MeetingSlotListResponse> result = slots.stream()
                .map(MeetingSlotListResponse::from)
                .toList();

        response.setSuccessResultData(result);
        return response;
    }
}
