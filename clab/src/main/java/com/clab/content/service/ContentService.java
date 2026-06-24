package com.clab.content.service;

import java.util.List;

import com.clab.content.dto.ContentCategoryJoinDto;
import com.clab.content.dto.ContentDto;

public interface ContentService {

	List<ContentDto> findAll();

	ContentDto findById(int id);

	List<ContentCategoryJoinDto> findByParticipantId(int participantId);

	int insert(ContentDto dto);

	void delete(int id);

	void deleteByParticipantId(int participantId);

}
