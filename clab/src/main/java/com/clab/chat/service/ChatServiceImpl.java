package com.clab.chat.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.clab.chat.dao.ChatMapper;
import com.clab.chat.dto.ChatDto;
import com.clab.chatFile.dto.ParsedMessage;
import com.clab.chatFile.service.ChatFileService;
import com.clab.chatFile.service.util.ChatParserUtil;
import com.clab.common.dto.PageRequestDto;
import com.clab.common.exception.CustomException;
import com.clab.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

	private final ChatMapper chatMapper;
	private final ChatFileService chatFileService;
	private final ChatParserUtil chatParserUtil;
	private final ChatAnalysisService chatAnalysisService;

	@Override
	public List<ChatDto> findAll() {
		return chatMapper.findAll();
	}

	@Override
	public Map<String, Object> findAllByUserId(int userId, PageRequestDto pageRequest) {
		Map<String, Object> params = new HashMap<>();
	    params.put("userId", userId);
	    params.put("limit", pageRequest.getSize());
	    params.put("offset", pageRequest.getOffset());
	    params.put("sortBy", pageRequest.getSortBy());
	    params.put("sortOrder", pageRequest.getSortOrder());
	    params.put("category", pageRequest.getCategory());

	    List<ChatDto> chatList = chatMapper.findAllByUserId(params);
	    int totalCount = chatMapper.countByUserIdAndCategoryId(userId, pageRequest.getCategory());

	    Map<String, Object> result = new HashMap<>();
	    result.put("chats", chatList);
	    result.put("totalCount", totalCount);
	    
	    return result;
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
		if (!"EMOTION".equals(dto.getCategory()) && !"MEETING".equals(dto.getCategory())) {
			throw new CustomException(ErrorCode.CHAT_BAD_REQUEST);
		}

		int chatFileId = chatFileService.storeFile(file);

		// MultipartFile은 요청 종료 후 사라지므로 비동기 전달 전에 미리 파싱
		List<ParsedMessage> messages = chatParserUtil.parseChatLog(file);

		ChatDto insertDto = new ChatDto(null, userId, chatFileId, null, null, dto.getTitle(), dto.getContent(), dto.getCategory(), "PENDING");
		int chatResult = chatMapper.insert(insertDto);

		if (chatResult == 0) {
			throw new CustomException(ErrorCode.CHAT_BAD_REQUEST);
		}
		int chatId = insertDto.getId();

		// 분석은 백그라운드에서 실행 → 즉시 chatId 반환
		chatAnalysisService.analyze(chatId, messages, dto.getCategory());

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
