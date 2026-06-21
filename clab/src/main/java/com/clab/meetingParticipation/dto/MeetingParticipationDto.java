package com.clab.meetingParticipation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MeetingParticipationDto {

	private Integer id;
	private Integer meetingAnalysisId;
	private Integer participantId;
	private Integer meaningfulUtteranceCount;
	private String keyOpinion;
	private Integer participationScore;
	private Integer topicInitiationCount;
	private Integer reactionReceivedScore;
	private String assignedTask;
}
