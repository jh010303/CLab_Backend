package com.clab.participant_category.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clab.common.exception.CustomException;
import com.clab.common.exception.ErrorCode;
import com.clab.participant_category.dao.ParticipantCategoryMapper;
import com.clab.participant_category.dto.ParticipantCategoryDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticipantCategoryServiceImpl implements ParticipantCategoryService {
	
	private final ParticipantCategoryMapper mapper;

	public List<ParticipantCategoryDto> findByParticipantId(int participantId) {
		List<ParticipantCategoryDto> dtos = mapper.findByParticipantId(participantId);
		if(dtos == null) {
			throw new CustomException(ErrorCode.PARTICIPANT_CATEGORY_NOT_FOUND);
		}
		return dtos;
	}

	@Override
	public int insert(ParticipantCategoryDto dto) {
		int result = mapper.insert(dto);
		if(result == 0) {
			throw new CustomException(ErrorCode.PARTICIPANT_CATEGORY_INSERT_FAILED);			
		}
		return dto.getId();
	}

	@Override
	public void delete(int id) {
		int result = mapper.delete(id);
		if(result == 0) {
			throw new CustomException(ErrorCode.PARTICIPANT_CATEGORY_DELETE_FAILED);						
		}
	}

	@Override
	public void deleteByParticipantId(int participantId) {
		int result = mapper.deleteByParticipantId(participantId);
		if(result == 0) {
			throw new CustomException(ErrorCode.PARTICIPANT_CATEGORY_DELETE_FAILED);						
		}		
	}

}
