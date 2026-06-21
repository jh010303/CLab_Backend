package com.clab.meetingParticipation.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.clab.meetingParticipation.dto.MeetingParticipationDto;

@Mapper
public interface MeetingParticipationMapper {

	List<MeetingParticipationDto> findByMeetingAnalysisId(int meetingAnalysisId);

	int insert(MeetingParticipationDto dto);

}
