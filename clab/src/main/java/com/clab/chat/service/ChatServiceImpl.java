package com.clab.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.clab.chat.dao.ChatMapper;
import com.clab.chat.dto.ChatDto;
import com.clab.chatFile.dto.ParsedMessage;
import com.clab.chatFile.service.ChatFileService;
import com.clab.chatFile.service.util.ChatParserUtil;
import com.clab.common.exception.CustomException;
import com.clab.common.exception.ErrorCode;
import com.clab.content.dto.ContentDto;
import com.clab.content.service.ContentService;
import com.clab.participant.dto.ParticipantDto;
import com.clab.participant.service.ParticipantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

	private final ChatMapper chatMapper;
	private final ChatFileService chatFileService;
	private final ParticipantService participantService;
	private final ContentService contentService;
	private final ChatParserUtil chatParserUtil;

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
		if (result == null) {
			throw new CustomException(ErrorCode.CHAT_NOT_FOUND);
		}
		if (userId != result.getUserId()) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		} else {
			return result;
		}
	}

	@Override
	@Transactional
	public int insert(ChatDto dto, MultipartFile file, int userId) {
		int chatFileId = chatFileService.storeFile(file);
		ChatDto insertDto = new ChatDto(null, userId, chatFileId, null, null, dto.getTitle(), dto.getContent());
		int chatResult = chatMapper.insert(insertDto);

		if (chatResult == 0) {
			throw new CustomException(ErrorCode.CHAT_BAD_REQUEST);
		}
		int chatId = insertDto.getId();

		List<ParsedMessage> messages = chatParserUtil.parseChatLog(file);
		List<ParticipantDto> participantDtos = chatParserUtil.buildParticipantDtos(messages, chatId);

		for (ParticipantDto participantDto : participantDtos) {
			participantService.insert(participantDto);
			List<ContentDto> contentDtos = chatParserUtil.buildContentDtos(messages, participantDto.getName(), participantDto.getId());
			contentDtos.forEach(contentService::insert);
		}

		return chatId;
	}

	@Override
	@Transactional
	public void update(int userId, int id, ChatDto dto) {
		ChatDto chat = chatMapper.findById(id);
		if (chat == null) {
			throw new CustomException(ErrorCode.CHAT_NOT_FOUND);
		}
		if (userId != chat.getUserId()) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		int result = chatMapper.update(id, dto);
		if (result == 0) {
			throw new CustomException(ErrorCode.CHAT_BAD_REQUEST);
		}
	}

	@Override
	@Transactional
	public void delete(int userId, int id) {
		ChatDto chat = chatMapper.findById(id);
		if (chat == null) {
			throw new CustomException(ErrorCode.CHAT_NOT_FOUND);
		}
		
		if (userId != chat.getUserId()) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		int result = chatFileService.delete(chat.getFileId());
		if (result == 0) {
			throw new CustomException(ErrorCode.CHAT_BAD_REQUEST);
		}
	}
}
