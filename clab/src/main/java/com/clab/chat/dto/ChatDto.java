package com.clab.chat.dto;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

// @Data
//@Builder
/*
 * build는 field에 null이 들어갈 수 있다.
 * 유지보수를 하면서 field를 추가할 때 compile 에러가 발생하지 않는다.
 * 생성자로 생성하게 하면 필드가 추가되었을때 컴파일 에러가 발생하면서 유지보수에 유리해진다.
 * 
 * */
@Getter
@AllArgsConstructor
// 얼 알규스를 쓰면 모든 필드를 받아서 생성할 때 private을 쓰면 외부에서 사용하지 못한다.
public class ChatDto {
	private Integer id;
	private Integer userId;
	private Integer fileId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String title;
	private String content;
	private String category;
	private String status;
}
