package com.clab.content.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.clab.content.dto.ContentCategoryJoinDto;
import com.clab.content.dto.ContentDto;

@Mapper
public interface ContentMapper {

	List<ContentDto> findAll();

	ContentDto findById(int id);

	List<ContentCategoryJoinDto> findByParticipantId(int participantId);

	int insert(ContentDto dto);

	int delete(int id);

	int deleteByParticipantId(int participantId);

}
