package com.clab.participant.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipantPersonaDto {
	private Integer id;
	// 대화 참여자
	private Integer chatId;
	private String name;
	private Integer count;
	private Integer averageReplyTime;
	private Integer chatLength;
	
	// 페르소나 분석
	private Integer personaAnalysisId;
	private String analysisSummary;
	private String speechStyle;
	private Integer tetoScore;
	
	// 인물상
	private Integer personaId;
	private String personaName;
	private String description;
	private String image;
}
