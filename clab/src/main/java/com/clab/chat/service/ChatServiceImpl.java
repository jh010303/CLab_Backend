package com.clab.chat.service;

import java.util.ArrayList;
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
import com.clab.common.ai.AiUtil;
import com.clab.common.ai.AiUtil.ParticipantAnalysis;
import com.clab.common.dto.PageRequestDto;
import com.clab.common.exception.CustomException;
import com.clab.common.exception.ErrorCode;
import com.clab.content.dto.ContentDto;
import com.clab.content.service.ContentService;
import com.clab.contentCategory.dto.ContentCategoryDto;
import com.clab.contentCategory.service.ContentCategoryService;
import com.clab.meetingAnalysis.dto.MeetingAnalysisDto;
import com.clab.meetingAnalysis.service.MeetingAnalysisService;
import com.clab.meetingParticipation.dto.MeetingParticipationDto;
import com.clab.meetingParticipation.service.MeetingParticipationService;
import com.clab.participant.dto.ParticipantDto;
import com.clab.participant.service.ParticipantService;
import com.clab.participantCategory.dto.ParticipantCategoryDto;
import com.clab.participantCategory.service.ParticipantCategoryService;
import com.clab.personaAnalysis.dto.PersonaAnalysisDto;
import com.clab.personaAnalysis.service.PersonaAnalysisService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

	private final ChatMapper chatMapper;
	private final ChatFileService chatFileService;
	private final ParticipantService participantService;
	private final ContentService contentService;
	private final ChatParserUtil chatParserUtil;
	private final AiUtil aiUtil;
	private final ContentCategoryService contentCategoryService;
	private final ParticipantCategoryService participantCategoryService;
	private final PersonaAnalysisService personaAnalysisService;
	private final MeetingAnalysisService meetingAnalysisService;
	private final MeetingParticipationService meetingParticipationService;

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

	    List<ChatDto> chatList = chatMapper.findAllByUserId(params);
	    int totalCount = chatMapper.countByUserId(userId);

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
		int chatFileId = chatFileService.storeFile(file);
		ChatDto insertDto = new ChatDto(null, userId, chatFileId, null, null, dto.getTitle(), dto.getContent(), dto.getCategory());
		int chatResult = chatMapper.insert(insertDto);

		if (chatResult == 0) {
			throw new CustomException(ErrorCode.CHAT_BAD_REQUEST);
		}
		int chatId = insertDto.getId();

		List<ParsedMessage> messages = chatParserUtil.parseChatLog(file);
		
		String analysisType = dto.getCategory();
		
		if ("EMOTION".equals(analysisType)) {
	        processEmotionAnalysis(messages, chatId);
	    } else if ("MEETING".equals(analysisType)) {
	        processMeetingAnalysis(messages, chatId);
	    } else {
	        throw new CustomException(ErrorCode.CHAT_BAD_REQUEST); // 예외 처리 추가 권장
	    }
		return chatId;
	}
	
	private void processEmotionAnalysis(List<ParsedMessage> messages, int chatId) {
		List<ParticipantDto> participantDtos = chatParserUtil.buildParticipantDtos(messages, chatId);

		for (ParticipantDto participantDto : participantDtos) {
			participantService.insert(participantDto);
			int participantId = participantDto.getId();

			List<ContentDto> contentDtos = chatParserUtil.buildContentDtos(messages, participantDto.getName(), participantId);

			// content 전체를 한 번에 AI에 보내 카테고리 배치 분류
			List<List<Integer>> categories = aiUtil.assignCategories(contentDtos);

			Map<Integer, Integer> categoryCountMap = new HashMap<>();

			for (int i = 0; i < contentDtos.size(); i++) {
				int contentId = contentService.insert(contentDtos.get(i));
				List<Integer> categoryIds = categories.get(i);

				for (Integer categoryId : categoryIds) {
					contentCategoryService.insert(new ContentCategoryDto(null, contentId, categoryId));
					categoryCountMap.merge(categoryId, 1, Integer::sum);
				}
			}

			// 집계된 카테고리 개수로 participant_category 생성
			for (Map.Entry<Integer, Integer> entry : categoryCountMap.entrySet()) {
				participantCategoryService.insert(
						new ParticipantCategoryDto(null, participantId, entry.getKey(), entry.getValue())
				);
			}

			// 참여자 말투 AI 분석 → persona_analysis 생성
			ParticipantAnalysis analysis = aiUtil.analyzeParticipant(contentDtos, categoryCountMap);
			int personaId = resolvePersonaId(categoryCountMap);
			personaAnalysisService.insert(new PersonaAnalysisDto(
					null, chatId, participantId, personaId,
					analysis.analysisSummary(), analysis.speechStyle(), analysis.tetoScore()
			));
		}
	}
	
	private void processMeetingAnalysis(List<ParsedMessage> messages, int chatId) {
		List<ParticipantDto> participantDtos = chatParserUtil.buildParticipantDtos(messages, chatId);
		
		List<MeetingParticipationDto> pendingParticipations = new ArrayList<>();
		
		for (ParticipantDto participantDto : participantDtos) {
			// 참여자 테이블 insert (공통)
			participantService.insert(participantDto);
			int participantId = participantDto.getId();
			
			List<ContentDto> contentDtos = chatParserUtil.buildContentDtos(messages, participantDto.getName(), participantId);
			
			for (ContentDto contentDto : contentDtos) {
				contentService.insert(contentDto); // 각 발화 내용 DB 저장
			}
			
			MeetingParticipationDto participationDto = aiUtil.analyzeMeetingParticipation(
	                contentDtos, 
	                participantDto.getName(), 
	                participantId
	        );
			
			pendingParticipations.add(participationDto);
			
		}
			
	    // 1. 전체 회의 단위 분석 (주제, 요약 등)
	    MeetingAnalysisDto analysisDto = aiUtil.analyzeMeetingOverview(messages, pendingParticipations, chatId);
	    meetingAnalysisService.insert(analysisDto);
	    int meetingAnalysisId = analysisDto.getId(); // insert 후 발급된 ID 가져오기
	    
	    for (MeetingParticipationDto participationDto : pendingParticipations) {
	        participationDto.setMeetingAnalysisId(meetingAnalysisId); // Setter 필요
	        meetingParticipationService.insert(participationDto);
	    }
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

	// 6개 카테고리 평균 계산 → 평균에서 가장 크게 벗어난 카테고리를 대표 특성으로 선택
	// 평균보다 높으면 high 페르소나, 낮으면 low 페르소나 배정
	private int resolvePersonaId(Map<Integer, Integer> categoryCountMap) {
		List<Integer> categoryIds = List.of(1001, 1002, 1003, 1004, 1005, 1006);

		double average = categoryIds.stream()
				.mapToInt(id -> categoryCountMap.getOrDefault(id, 0))
				.average()
				.orElse(0);

		int dominantCategory = 0;
		double maxDeviation = 0;

		for (int categoryId : categoryIds) {
			double deviation = categoryCountMap.getOrDefault(categoryId, 0) - average;
			if (Math.abs(deviation) > Math.abs(maxDeviation)) {
				maxDeviation = deviation;
				dominantCategory = categoryId;
			}
		}

		boolean isHigh = maxDeviation > 0;

		return switch (dominantCategory) {
			case 1001 -> isHigh ? 1000 : 1001; // 감정표현도: 리액션 화산 / 포커페이스
			case 1002 -> isHigh ? 1002 : 1003; // 확신성: 결론부터 말해봄 / 일단 열어두는 편
			case 1003 -> isHigh ? 1004 : 1005; // 지시성: 총대 메는 대장 / 흐름에 맡기는 물결
			case 1004 -> isHigh ? 1006 : 1007; // 직설성: 돌직구 장인 / 쿠션어 마스터
			case 1005 -> isHigh ? 1008 : 1009; // 공감성: 감정 공명러 / 팩트만 챙기는 사람
			case 1006 -> isHigh ? 1010 : 1011; // 배려도: 눈치력 만렙 / 내 페이스대로 간다
			default   -> 1003;                  // 모든 카테고리 count 동일 → 일단 열어두는 편
		};
	}
}
