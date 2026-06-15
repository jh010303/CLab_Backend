package com.clab.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.clab.chat.dao.ChatMapper;
import com.clab.chat.dto.ChatDto;
import com.clab.chatFile.service.ChatFileService;
import com.clab.common.exception.CustomException;
import com.clab.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
	
	private final ChatMapper chatMapper;
	private final ChatFileService chatFileService;
	
	@Override
	public List<ChatDto> findAll() {
		return chatMapper.findAll();
	}

	@Override
	public List<ChatDto> findAllByUserId(int userId) {
		return chatMapper.findAllByUserId(userId);
	}

	@Override
	public ChatDto findById(int userId, int id) {
		ChatDto result = chatMapper.findById(id);
		if(result == null) {
			throw new CustomException(ErrorCode.CHAT_NOT_FOUND);
		}
		if(userId != result.getUserId()) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}
		else {
			return result;
		}
	}

	@Override
	@Transactional
	public int insert(ChatDto dto, MultipartFile file, int userId) {
		int chatFileId = chatFileService.storeFile(file);
		ChatDto insertDto = new ChatDto(null,userId,chatFileId,null,null,
				dto.getTitle(),dto.getContent());
		int chatResult = chatMapper.insert(insertDto);
		
		/*
		 * 채팅 참여자, 채팅 참여자 별 대화 내용 파싱 및 저장
		 * ai에게 위 데이터 보내서 채팅 참여자, 대화 내용 별 카테고리 추출 및 저장
		 * ai가 위 데이터 토대로 대화분석, 참여자 별 인물상 추출 및 저장
		 * 추후 issue에서 구현 예정
		 * */
		
		if(chatResult == 0) {
			throw new CustomException(ErrorCode.CHAT_BAD_REQUEST);
		} else {
			int id = insertDto.getId();
			return id;
		}
	}

	@Override
	@Transactional
	public void update(int userId, int id, ChatDto dto) {
		ChatDto chat = chatMapper.findById(id);	
		if(chat == null) {
			throw new CustomException(ErrorCode.CHAT_NOT_FOUND);
		}
		if(userId != chat.getUserId()) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		int result = chatMapper.update(id, dto);
		if(result == 0) {
			throw new CustomException(ErrorCode.CHAT_BAD_REQUEST);
		}
	}

	@Override
	@Transactional
	public void delete(int userId, int id) {
		ChatDto chat = chatMapper.findById(id);	
		if(chat == null) {
			throw new CustomException(ErrorCode.CHAT_NOT_FOUND);
		}
		if(userId != chat.getUserId()) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}
		
		int result = chatFileService.delete(chat.getFileId());
		if(result == 0) {
			throw new CustomException(ErrorCode.CHAT_BAD_REQUEST);
		}
		
		int chatResult = chatMapper.delete(id);
		if(chatResult == 0) {
			throw new CustomException(ErrorCode.CHAT_BAD_REQUEST);
		}
	}
}
