package com.clab.participant.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipantMeetingDto {
	private Integer id;
	
	// 대화 참여자
	private Integer chatId;
	private String name;
	private Integer count;
	private Integer averageReplyTime;
	private Integer chatLength;
	
	// 회의 참여
	private Integer meetingParticipationId;
	private Integer meaningfulUtteranceCount;
	private String keyOpinion;
	private Integer participationScore;
	private Integer topicInitiationCount;
	private Integer reactionReceivedScore;
	private String assignedTask;
}
