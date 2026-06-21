package com.clab.participantCategory.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.clab.participantCategory.dto.ParticipantCategoryDto;

@Mapper
public interface ParticipantCategoryMapper {

	List<ParticipantCategoryDto> findByParticipantId(int participantId);

	int insert(ParticipantCategoryDto dto);

	int delete(int id);

	int deleteByParticipantId(int participantId);

}
