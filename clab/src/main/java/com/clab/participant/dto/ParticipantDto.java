package com.clab.participant.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipantDto {
	private Integer id;
	private Integer chatId;
	private String name;
	private Integer count;
	private Integer averageReplyTime;
	private Integer chatLength;
}
