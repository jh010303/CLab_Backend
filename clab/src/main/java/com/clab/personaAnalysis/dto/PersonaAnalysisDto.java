package com.clab.personaAnalysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class PersonaAnalysisDto {
	private Integer id;
	private Integer chatId;
	private Integer participantId;
	private Integer personaId;
	private String analysisSummary;
	private String speechStyle;
	private Integer tetoScore;
}
