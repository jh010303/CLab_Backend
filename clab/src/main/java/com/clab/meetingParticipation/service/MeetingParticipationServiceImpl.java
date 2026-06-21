package com.clab.meetingParticipation.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clab.common.exception.CustomException;
import com.clab.common.exception.ErrorCode;
import com.clab.meetingParticipation.dao.MeetingParticipationMapper;
import com.clab.meetingParticipation.dto.MeetingParticipationDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetingParticipationServiceImpl implements MeetingParticipationService{

	private final MeetingParticipationMapper mapper;

	@Override
	public List<MeetingParticipationDto> findByMeetingAnalysisId(int meetingAnalysisId) {
		List<MeetingParticipationDto> list = mapper.findByMeetingAnalysisId(meetingAnalysisId);
		return list;
	}

	@Override
	public int insert(MeetingParticipationDto dto) {
		int result = mapper.insert(dto);
		if(result == 0) {
			throw new CustomException(ErrorCode.MEETING_PARTICIPATION_INSERT_FAILED);
		}
		return dto.getId();
	}
}
