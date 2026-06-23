package com.clab.participant.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.clab.common.dto.SortRequestDto;
import com.clab.participant.dto.ParticipantDto;
import com.clab.participant.dto.ParticipantMeetingDto;
import com.clab.participant.dto.ParticipantPersonaDto;

@Mapper
public interface ParticipantMapper {

	List<ParticipantDto> findAll();

	ParticipantDto findById(int id);

	List<ParticipantPersonaDto> findPersonaById(@Param("chatId") int chatId, 
			@Param("sort")SortRequestDto sortRequest);
	
	List<ParticipantMeetingDto> findMeetingById(@Param("chatId")int chatId, 
			@Param("sort") SortRequestDto sortRequest);
	
	List<ParticipantDto> findByChatId(int chatId);

	int insert(ParticipantDto dto);

	int update(int id, ParticipantDto dto);

	int delete(int id);

	int deleteByChatId(int chatId);
}
