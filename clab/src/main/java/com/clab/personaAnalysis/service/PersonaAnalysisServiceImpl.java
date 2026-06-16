package com.clab.personaAnalysis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clab.common.exception.CustomException;
import com.clab.common.exception.ErrorCode;
import com.clab.personaAnalysis.dao.PersonaAnalysisMapper;
import com.clab.personaAnalysis.dto.PersonaAnalysisDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonaAnalysisServiceImpl implements PersonaAnalysisService {

	private final PersonaAnalysisMapper mapper;

	@Override
	public List<PersonaAnalysisDto> findAll() {
		return mapper.findAll();
	}

	@Override
	public PersonaAnalysisDto findById(int id) {
		PersonaAnalysisDto dto = mapper.findById(id);
		if (dto == null) {
			throw new CustomException(ErrorCode.PERSONA_ANALYSIS_NOT_FOUND);
		}
		return dto;
	}

	@Override
	public List<PersonaAnalysisDto> findByChatId(int chatId) {
		return mapper.findByChatId(chatId);
	}

	@Override
	public List<PersonaAnalysisDto> findByParticipantId(int participantId) {
		return mapper.findByParticipantId(participantId);
	}

	@Override
	public int insert(PersonaAnalysisDto dto) {
		int result = mapper.insert(dto);
		if (result == 0) {
			throw new CustomException(ErrorCode.PERSONA_ANALYSIS_INSERT_FAILED);
		}
		return dto.getId();
	}

	@Override
	public void update(int id, PersonaAnalysisDto dto) {
		if (mapper.findById(id) == null) {
			throw new CustomException(ErrorCode.PERSONA_ANALYSIS_NOT_FOUND);
		}
		int result = mapper.update(id, dto);
		if (result == 0) {
			throw new CustomException(ErrorCode.PERSONA_ANALYSIS_UPDATE_FAILED);
		}
	}

	@Override
	public void delete(int id) {
		int result = mapper.delete(id);
		if (result == 0) {
			throw new CustomException(ErrorCode.PERSONA_ANALYSIS_DELETE_FAILED);
		}
	}

}
