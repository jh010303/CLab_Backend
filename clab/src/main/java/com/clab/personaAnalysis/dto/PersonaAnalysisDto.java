package com.clab.personaAnalysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaAnalysisDto {
	private Integer id;
	private Integer chattingId;
	private Integer participantId;
	private Integer personaId;
	private String analysisSummary;
	private String speechStyle;
	private Integer tetoScore;
}
