package com.clab.participant.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.clab.participant.dto.ParticipantDto;
import com.clab.participant.dto.ParticipantMeetingDto;
import com.clab.participant.dto.ParticipantPersonaDto;

@Mapper
public interface ParticipantMapper {

	List<ParticipantDto> findAll();

	ParticipantDto findById(int id);

	List<ParticipantPersonaDto> findPersonaById(int chatId);
	
	List<ParticipantMeetingDto> findMeetingById(int chatId);
	
	List<ParticipantDto> findByChatId(int chatId);

	int insert(ParticipantDto dto);

	int update(int id, ParticipantDto dto);

	int delete(int id);

	int deleteByChatId(int chatId);

}
