package com.clab.chat.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.clab.chat.dto.ChatDto;
import com.clab.common.dto.PageRequestDto;
import com.clab.common.exception.ApiResponse;

public interface ChatService {
	
	List<ChatDto> findAll();
	
	Map<String, Object> findAllByUserId(int userId, PageRequestDto pageRequest);
	
	ChatDto findById(int userId, int id);

	int insert(ChatDto dto, MultipartFile file, int userId);

	void update(int userId, int id, ChatDto dto);

	void delete(int userId, int id);
}
