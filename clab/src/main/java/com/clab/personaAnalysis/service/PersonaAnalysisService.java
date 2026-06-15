package com.clab.personaAnalysis.service;

import java.util.List;

import com.clab.personaAnalysis.dto.PersonaAnalysisDto;

public interface PersonaAnalysisService {

	List<PersonaAnalysisDto> findAll();

	PersonaAnalysisDto findById(int id);

	List<PersonaAnalysisDto> findByChattingId(int chattingId);

	List<PersonaAnalysisDto> findByParticipantId(int participantId);

	int insert(PersonaAnalysisDto dto);

	void update(int id, PersonaAnalysisDto dto);

	void delete(int id);

}
