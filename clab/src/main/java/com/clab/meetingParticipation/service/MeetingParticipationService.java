package com.clab.meetingParticipation.service;

import java.util.List;

import com.clab.meetingParticipation.dto.MeetingParticipationDto;

public interface MeetingParticipationService {

	List<MeetingParticipationDto> findByMeetingAnalysisId(int meetingAnalysisId);

	int insert(MeetingParticipationDto dto);

}
