package com.clab.meetingAnalysis.service;

import org.springframework.stereotype.Service;

import com.clab.common.exception.CustomException;
import com.clab.common.exception.ErrorCode;
import com.clab.meetingAnalysis.dao.MeetingAnalysisMapper;
import com.clab.meetingAnalysis.dto.MeetingAnalysisDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetingAnalysisServiceImpl implements MeetingAnalysisService {

	private final MeetingAnalysisMapper mapper;
	
	@Override
	public MeetingAnalysisDto findByChatId(int chatId) {
		MeetingAnalysisDto dto = mapper.findByChatId(chatId);
		if(dto == null) {
			throw new CustomException(ErrorCode.MEETING_ANALYSIS_NOT_FOUND);
		}
		return dto;
	}

	@Override
	public int insert(MeetingAnalysisDto dto) {
		int result = mapper.insert(dto);
		if(result == 0) {
			throw new CustomException(ErrorCode.MEETING_ANALYSIS_INSERT_FAILED);
		}
		return dto.getId();
	}

}
