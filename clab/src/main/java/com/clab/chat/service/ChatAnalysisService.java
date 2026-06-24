package com.clab.chat.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.clab.chat.dao.ChatMapper;
import com.clab.chatFile.dto.ParsedMessage;
import com.clab.common.ai.AiUtil;
import com.clab.common.ai.AiUtil.ParticipantAnalysis;
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
import com.clab.chatFile.service.util.ChatParserUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatAnalysisService {

	private final ChatMapper chatMapper;
	private final ChatParserUtil chatParserUtil;
	private final AiUtil aiUtil;
	private final ParticipantService participantService;
	private final ContentService contentService;
	private final ContentCategoryService contentCategoryService;
	private final ParticipantCategoryService participantCategoryService;
	private final PersonaAnalysisService personaAnalysisService;
	private final MeetingAnalysisService meetingAnalysisService;
	private final MeetingParticipationService meetingParticipationService;

	@Async("analysisExecutor")
	public void analyze(int chatId, List<ParsedMessage> messages, String category) {
		try {
			if ("EMOTION".equals(category)) {
				processEmotionAnalysis(messages, chatId);
			} else if ("MEETING".equals(category)) {
				processMeetingAnalysis(messages, chatId);
			}
			chatMapper.updateStatus(chatId, "DONE");
		} catch (Exception e) {
			log.error("채팅 분석 실패 - chatId: {}", chatId, e);
			chatMapper.updateStatus(chatId, "FAILED");
		}
	}

	private void processEmotionAnalysis(List<ParsedMessage> messages, int chatId) {
		List<ParticipantDto> participantDtos = chatParserUtil.buildParticipantDtos(messages, chatId);

		for (ParticipantDto participantDto : participantDtos) {
			participantService.insert(participantDto);
			int participantId = participantDto.getId();

			List<ContentDto> contentDtos = chatParserUtil.buildContentDtos(messages, participantDto.getName(), participantId);
			List<List<Integer>> categories = aiUtil.assignCategories(contentDtos);

			Map<Integer, Integer> categoryCountMap = new HashMap<>();

			for (int i = 0; i < contentDtos.size(); i++) {
				int contentId = contentService.insert(contentDtos.get(i));
				List<Integer> categoryIds = categories.get(i);

				for (Integer categoryId : categoryIds) {
					contentCategoryService.insert(new ContentCategoryDto(null, contentId, categoryId));
					if (categoryId != null) {
						categoryCountMap.merge(categoryId, 1, Integer::sum);
					}
				}
			}

			for (Map.Entry<Integer, Integer> entry : categoryCountMap.entrySet()) {
				participantCategoryService.insert(
						new ParticipantCategoryDto(null, participantId, entry.getKey(), entry.getValue())
				);
			}

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
			participantService.insert(participantDto);
			int participantId = participantDto.getId();

			List<ContentDto> contentDtos = chatParserUtil.buildContentDtos(messages, participantDto.getName(), participantId);

			for (ContentDto contentDto : contentDtos) {
				contentService.insert(contentDto);
			}

			MeetingParticipationDto participationDto = aiUtil.analyzeMeetingParticipation(
					contentDtos,
					participantDto.getName(),
					participantId
			);

			pendingParticipations.add(participationDto);
		}

		MeetingAnalysisDto analysisDto = aiUtil.analyzeMeetingOverview(messages, pendingParticipations, chatId);
		meetingAnalysisService.insert(analysisDto);
		int meetingAnalysisId = analysisDto.getId();

		for (MeetingParticipationDto participationDto : pendingParticipations) {
			participationDto.setMeetingAnalysisId(meetingAnalysisId);
			meetingParticipationService.insert(participationDto);
		}
	}

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
			case 1001 -> isHigh ? 1000 : 1001;
			case 1002 -> isHigh ? 1002 : 1003;
			case 1003 -> isHigh ? 1004 : 1005;
			case 1004 -> isHigh ? 1006 : 1007;
			case 1005 -> isHigh ? 1008 : 1009;
			case 1006 -> isHigh ? 1010 : 1011;
			default   -> 1003;
		};
	}
}
