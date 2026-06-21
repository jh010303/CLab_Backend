package com.clab.meetingAnalysis.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeetingAnalysisDto {
	
	private Integer id;
	private Integer chatId;
	private String topic;
	private LocalDateTime startedAt;
	private LocalDateTime endedAt;
	private String keywords;
	private String atmosphere;
	private String meetingSummary;
	private String actionItems;

}
