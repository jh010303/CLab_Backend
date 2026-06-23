package com.clab.chat.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.clab.chat.dto.ChatDto;

@Mapper
public interface ChatMapper {

	List<ChatDto> findAll();
	
	List<ChatDto> findAllByUserId(Map<String, Object> params);

	int countByUserId(int userId);

	ChatDto findById(int id);

	int insert(ChatDto dto);

	int update(@Param("id") int id, @Param("dto") ChatDto dto);

	int delete(int id);
}
