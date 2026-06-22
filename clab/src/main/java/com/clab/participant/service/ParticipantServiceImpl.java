package com.clab.participant.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clab.common.exception.CustomException;
import com.clab.common.exception.ErrorCode;
import com.clab.participant.dao.ParticipantMapper;
import com.clab.participant.dto.ParticipantDto;
import com.clab.participant.dto.ParticipantPersonaDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

	private final ParticipantMapper mapper;
	@Override
	public List<ParticipantDto> findAll() {
		return mapper.findAll();
	}

	@Override
	public ParticipantDto findById(int id) {
		ParticipantDto dto = mapper.findById(id);
		if(dto == null) {
			throw new CustomException(ErrorCode.PARTICIPANT_NOT_FOUND);
		}
		return dto;
	}


	@Override
	public List<ParticipantPersonaDto> findPersonaById(int chatId) {
		List<ParticipantPersonaDto> dto = mapper.findPersonaById(chatId);
		if(dto.isEmpty()) {
			throw new CustomException(ErrorCode.PARTICIPANT_NOT_FOUND);
		}
		return dto;
	}
	
	@Override
	public List<ParticipantDto> findByChatId(int chatId) {
		return mapper.findByChatId(chatId);
	}

	@Override
	public int insert(ParticipantDto dto) {
		int result = mapper.insert(dto);
		if(result == 0) {
			throw new CustomException(ErrorCode.PARTICIPANT_INSERT_FAILED);
		}
		return dto.getId();
	}

	@Override
	public void delete(int id) {
		int result = mapper.delete(id);
		if(result == 0) {
			throw new CustomException(ErrorCode.PARTICIPANT_DELETE_FAILED);
		}
	}
	
	@Override
	public void deleteByChatId(int chatId) {
		int result = mapper.deleteByChatId(chatId);
		if(result == 0) {
			throw new CustomException(ErrorCode.PARTICIPANT_DELETE_FAILED);
		}
	}
}
