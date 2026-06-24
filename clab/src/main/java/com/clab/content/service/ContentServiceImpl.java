package com.clab.content.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clab.common.exception.CustomException;
import com.clab.common.exception.ErrorCode;
import com.clab.content.dao.ContentMapper;
import com.clab.content.dto.ContentCategoryJoinDto;
import com.clab.content.dto.ContentDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {
	
	private final ContentMapper mapper;

	@Override
	public List<ContentDto> findAll() {
		return mapper.findAll();
	}

	@Override
	public ContentDto findById(int id) {
		ContentDto dto = mapper.findById(id);
		if(dto == null) {
			throw new CustomException(ErrorCode.CONTENT_NOT_FOUND);
		}
		return dto;
	}

	@Override
	public List<ContentCategoryJoinDto> findByParticipantId(int participantId) {
		return mapper.findByParticipantId(participantId);
	}

	@Override
	public int insert(ContentDto dto) {
		int result = mapper.insert(dto);
		if(result == 0) {
			throw new CustomException(ErrorCode.CONTENT_INSERT_FAILED);
		}
		return dto.getId();
	}

	@Override
	public void delete(int id) {
		int result = mapper.delete(id);
		if(result == 0) {
			throw new CustomException(ErrorCode.CONTENT_DELETE_FAILED);
		}
	}

	@Override
	public void deleteByParticipantId(int participantId) {
		int result = mapper.deleteByParticipantId(participantId);
		if(result == 0) {
			throw new CustomException(ErrorCode.CONTENT_DELETE_FAILED);
		}
	}
}
