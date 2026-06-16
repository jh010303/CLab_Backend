package com.clab.participant_category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipantCategoryDto {
	private Integer id;
	private Integer participantId;
	private Integer categoryId;
	private Integer count;

}
