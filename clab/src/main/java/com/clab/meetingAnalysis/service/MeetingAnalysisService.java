package com.clab.meetingAnalysis.service;

import com.clab.meetingAnalysis.dto.MeetingAnalysisDto;

public interface MeetingAnalysisService {

	MeetingAnalysisDto findByChatId(int chatId);

	int insert(MeetingAnalysisDto dto);

}
