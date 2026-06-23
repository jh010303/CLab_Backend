package com.clab.participant.service;

import java.util.List;

import com.clab.common.dto.SortRequestDto;
import com.clab.participant.dto.ParticipantDto;
import com.clab.participant.dto.ParticipantMeetingDto;
import com.clab.participant.dto.ParticipantPersonaDto;

public interface ParticipantService {

	List<ParticipantDto> findAll();

	ParticipantDto findById(int id);

	List<ParticipantPersonaDto> findPersonaById(int chatId, SortRequestDto sortRequest);
	
	List<ParticipantMeetingDto> findMeetingById(int chatId, SortRequestDto sortRequest);
	
	List<ParticipantDto> findByChatId(int chatId);

	int insert(ParticipantDto dto);

	void delete(int id);

	void deleteByChatId(int chatId);
}
