package com.clab.personaAnalysis.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.clab.personaAnalysis.dto.PersonaAnalysisDto;

@Mapper
public interface PersonaAnalysisMapper {

	List<PersonaAnalysisDto> findAll();

	PersonaAnalysisDto findById(int id);

	List<PersonaAnalysisDto> findByChattingId(int chattingId);

	List<PersonaAnalysisDto> findByParticipantId(int participantId);

	int insert(PersonaAnalysisDto dto);

	int update(int id, PersonaAnalysisDto dto);

	int delete(int id);

}
