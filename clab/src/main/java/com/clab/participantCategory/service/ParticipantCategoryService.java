package com.clab.participantCategory.service;

import java.util.List;

import com.clab.participantCategory.dto.ParticipantCategoryDto;

public interface ParticipantCategoryService {

	List<ParticipantCategoryDto> findByParticipantId(int participantId);

	int insert(ParticipantCategoryDto dto);

	void delete(int id);

	void deleteByParticipantId(int participantId);

}
