package com.clab.content.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContentCategoryJoinDto {
	// 대화 내용
	private Integer id;
	private Integer participantId;
	private String content;
	private LocalDateTime time;
	
	// 카테고리
	private Integer categoryId;
	private String categoryName;
}
