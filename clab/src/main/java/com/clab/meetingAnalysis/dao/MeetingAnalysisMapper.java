package com.clab.meetingAnalysis.dao;

import org.apache.ibatis.annotations.Mapper;

import com.clab.meetingAnalysis.dto.MeetingAnalysisDto;

@Mapper
public interface MeetingAnalysisMapper {

	MeetingAnalysisDto findByChatId(int chatId);

	int insert(MeetingAnalysisDto dto);

}
