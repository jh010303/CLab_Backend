package com.clab.participant_category.service;

import java.util.List;

import com.clab.participant_category.dto.ParticipantCategoryDto;

public interface ParticipantCategoryService {

	List<ParticipantCategoryDto> findByParticipantId(int participantId);

	int insert(ParticipantCategoryDto dto);

	void delete(int id);

	void deleteByParticipantId(int participantId);

}
