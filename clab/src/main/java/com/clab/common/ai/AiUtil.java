package com.clab.common.ai;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import com.clab.chatFile.dto.ParsedMessage;
import com.clab.content.dto.ContentDto;
import com.clab.meetingAnalysis.dto.MeetingAnalysisDto;
import com.clab.meetingParticipation.dto.MeetingParticipationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiUtil {

	private final OpenAiChatModel model;

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private static final Set<Integer> VALID_CATEGORY_IDS = Set.of(1001, 1002, 1003, 1004, 1005, 1006);

	private static final Map<Integer, String> CATEGORY_NAMES = Map.of(
			1001, "감정표현도",
			1002, "확신성",
			1003, "지시성",
			1004, "직설성",
			1005, "공감성",
			1006, "배려도"
	);

	private static final String CATEGORY_PROMPT_HEADER = """
			다음 카카오톡 대화 메시지들을 분석하여 각 메시지에 해당하는 카테고리 ID를 2차원 배열로만 반환하세요.
			메시지 순서에 맞게 결과를 반환하고, 설명이나 코드블록 없이 2차원 배열만 출력하세요.
			해당하는 카테고리가 없으면 빈 배열 []을 사용하세요.

			카테고리 목록:
			1001: 감정표현도 - 자신의 감정을 풍부하게 표현하는 정도
			1002: 확신성 - 단정적이거나 확신에 찬 어휘 사용 빈도
			1003: 지시성 - 상대방에게 행동을 요구하거나 지시하는 정도
			1004: 직설성 - 돌려 말하지 않고 직접적으로 의사를 전달하는 정도
			1005: 공감성 - 상대방의 감정이나 상황에 동조하는 정도
			1006: 배려도 - 상대방의 입장과 편의를 고려하는 정도

			메시지 목록:
			""";

	public record ParticipantAnalysis(String analysisSummary, String speechStyle, int tetoScore) {}

	// 반환: contents[i]에 해당하는 카테고리 ID 목록이 result[i]에 담김
	public List<List<Integer>> assignCategories(List<ContentDto> contents) {
		if (contents.isEmpty()) {
			return Collections.emptyList();
		}

		StringBuilder prompt = new StringBuilder(CATEGORY_PROMPT_HEADER);
		for (int i = 0; i < contents.size(); i++) {
			prompt.append(i).append(": \"").append(contents.get(i).getContent()).append("\"\n");
		}
		prompt.append("\n반환 형식 예시 (메시지 3개일 때): [[1001, 1005], [], [1003]]");

		try {
			String response = model.call(prompt.toString());
			log.info("ai 답변: {}", response);
			String cleaned = response.trim();
			List<List<Integer>> parsed = objectMapper.readValue(cleaned, new TypeReference<List<List<Integer>>>() {});

			List<List<Integer>> result = new ArrayList<>();
			for (int i = 0; i < contents.size(); i++) {
				List<Integer> raw = i < parsed.size() ? parsed.get(i) : Collections.emptyList();
				List<Integer> validated = raw.stream()
						.map(id -> VALID_CATEGORY_IDS.contains(id) ? id : null)
						.collect(java.util.stream.Collectors.toList());
				result.add(validated);
			}
			return result;
		} catch (Exception e) {
			log.error("AI 카테고리 배치 분류 실패 - content 수: {}", contents.size(), e);
			return contents.stream().map(c -> Collections.<Integer>emptyList()).toList();
		}
	}

	// 참여자 말투 분석: analysis_summary, speech_style, teto_score 반환
	public ParticipantAnalysis analyzeParticipant(List<ContentDto> contents, Map<Integer, Integer> categoryCountMap) {
		StringBuilder prompt = new StringBuilder();
		prompt.append("다음은 카카오톡 대화 참여자의 메시지와 말투 카테고리 분석 결과입니다.\n");
		prompt.append("이를 바탕으로 이 사람의 말투를 분석해주세요.\n\n");

		prompt.append("카테고리별 발화 횟수:\n");
		CATEGORY_NAMES.forEach((id, name) ->
				prompt.append("- ").append(name).append(": ").append(categoryCountMap.getOrDefault(id, 0)).append("회\n")
		);

		prompt.append("\n대화 샘플 (최대 100개):\n");
		List<ContentDto> sampled = contents.size() <= 100
				? contents
				: new java.util.ArrayList<>(contents);
		if (contents.size() > 100) {
			java.util.Collections.shuffle(sampled);
			sampled = sampled.subList(0, 100);
		}
		sampled.forEach(c -> prompt.append("\"").append(c.getContent()).append("\"\n"));

		prompt.append("""

				코드블록 없이 아래 JSON 형식으로만 반환하세요:
				{
				  "analysis_summary": "말투 상세 분석 (200자 이내)",
				  "speech_style": "말투 특징을 자세히 설명하고, 위에 제공된 대화 샘플 중에서 그 근거가 되는 메시지를 2~3개 그대로 인용하세요. 예시는 '예: \\\"실제 메시지\\\"' 형식으로 작성하세요.",
				  "teto_score": 0에서 100 사이 정수
				}

				teto_score 기준:
				0 = 수용적/공감적 (상대 감정에 맞추고 배려하는 성향)
				100 = 주도적/단호함 (자기 의견을 명확히 밀고 이끄는 성향)
				""");

		try {
			String response = model.call(prompt.toString());
			log.info("참여자 분석 ai 답변: {}", response);
			JsonNode node = objectMapper.readTree(response.trim());
			return new ParticipantAnalysis(
					node.get("analysis_summary").asText(),
					node.get("speech_style").asText(),
					node.get("teto_score").asInt()
			);
		} catch (Exception e) {
			log.error("AI 참여자 분석 실패", e);
			return new ParticipantAnalysis("분석 실패", "알 수 없음", 50);
		}
	}
	
	/*
	 * =================== 회의 분석 로직 ======================================
	 * */
	
	private static final String MEETING_OVERVIEW_PROMPT_HEADER = """
            다음은 실제 회의에서 나눈 대화 메시지들입니다. 아래 대화를 분석하여 회의 전반에 대한 정보를 JSON 형식으로만 반환하세요.
            
            [반환 형식]
            반드시 아래 JSON 형식만 반환하고, 다른 설명이나 텍스트는 절대 포함하지 마세요.
            {
              "topic": "회의 주제 (간결하게 한 줄 요약)",
              "started_at": "회의 시작 시간 (yyyy-MM-dd HH:mm:ss 형식, 첫 메시지 기준)",
              "ended_at": "회의 종료 시간 (yyyy-MM-dd HH:mm:ss 형식, 마지막 메시지 기준)",
              "keywords": "키워드1,키워드2,키워드3,키워드4,키워드5",
              "atmosphere": "회의 분위기 (예: 긍정적, 갈등적, 중립적, 협력적, 긴장된)",
              "meeting_summary": "회의 전체 내용 요약 (3~5문장)",
              "action_items": "액션아이템1|액션아이템2|액션아이템3"
            }
            
            [분석 기준]
            - topic: 대화에서 주로 논의된 핵심 주제를 파악하세요.
            - started_at / ended_at: 메시지의 타임스탬프를 기반으로 추출하세요.
            - keywords: 회의에서 반복적으로 언급되거나 중요한 단어 최대 5개를 쉼표(,)로 구분하여 반환하세요.
            - atmosphere: 대화 톤, 감정, 갈등 여부 등을 종합하여 분위기를 판단하세요.
            - meeting_summary: 회의의 흐름과 결론을 중심으로 요약하세요.
            - action_items: 회의에서 결정된 할 일, 담당자 지정, 후속 조치 등을 파이프(|)로 구분하여 반환하세요.
            
            [대화 메시지]
            """;

    private static final String MEETING_PARTICIPATION_PROMPT_HEADER = """
            다음은 실제 회의에서 나눈 대화 메시지들입니다. 특정 참여자의 발화를 중심으로 기여도를 분석하여 JSON 형식으로만 반환하세요.
            
            [반환 형식]
            반드시 아래 JSON 형식만 반환하고, 다른 설명이나 텍스트는 절대 포함하지 마세요.
            {
              "meaningful_utterance_count": 0,
              "key_opinion": "해당 참여자의 핵심 의견 또는 주요 발언 요약 (2~3문장)",
              "participation_score": 0,
              "topic_initiation_count": 0,
              "reaction_received_score": 0,
              "assigned_task": "해당 참여자에게 배정된 작업 또는 역할 (없으면 '없음')"
            }
            
            [분석 기준]
            - meaningful_utterance_count: 단순 반응("ㅇㅇ", "ㄴㄴ", "ㅋㅋ" 등)을 제외하고, 실질적인 내용이 담긴 발화 수를 정수로 카운트하세요.
            - key_opinion: 해당 참여자가 회의에서 주장하거나 제안한 핵심 의견을 요약하세요.
            - participation_score: 전체 회의 기여도를 0 ~ 10 사이의 정수로 평가하세요.
              (발화량, 발화 품질, 주제 주도, 의사결정 기여 등을 종합 판단)
            - topic_initiation_count: 해당 참여자가 새로운 주제나 안건을 먼저 꺼낸 횟수를 정수로 카운트하세요.
            - reaction_received_score: 해당 참여자의 발언에 다른 참여자들이 반응(동의, 반론, 질문, 인용 등)한 정도를 0 ~ 10 사이의 정수로 평가하세요.
            - assigned_task: 회의 중 해당 참여자에게 명시적으로 부여된 역할이나 할 일을 추출하세요.
            
            [분석 대상 참여자]
            """;

    private static final String MEETING_PARTICIPATION_PROMPT_FOOTER = """
            
            [전체 대화 메시지]
            """;

    // ==================== 회의 전체 분석 ====================

    public MeetingAnalysisDto analyzeMeetingOverview(
            List<ParsedMessage> messages,
            List<MeetingParticipationDto> pendingParticipations,
            int chatId) {

        // 1. 프롬프트 구성
        String prompt = buildMeetingOverviewPrompt(messages);

        // 2. OpenAiChatModel 호출
        String jsonResponse = model.call(prompt);

        // 3. JSON 파싱 후 DTO 반환
        return parseMeetingAnalysisResponse(jsonResponse, chatId);
    }

    private String buildMeetingOverviewPrompt(List<ParsedMessage> messages) {
        StringBuilder sb = new StringBuilder();
        sb.append(MEETING_OVERVIEW_PROMPT_HEADER);

        for (ParsedMessage message : messages) {
            sb.append(String.format("[%s] %s: %s%n",
                    message.getTime(),
                    message.getSender(),
                    message.getContent()
            ));
        }

        return sb.toString();
    }

    private MeetingAnalysisDto parseMeetingAnalysisResponse(String jsonResponse, int chatId) {
        try {
            String cleanedJson = extractJson(jsonResponse);
            JsonNode root = objectMapper.readTree(cleanedJson);

            String topic = root.path("topic").asText();
            String startedAtStr = root.path("started_at").asText();
            String endedAtStr = root.path("ended_at").asText();
            String keywords = root.path("keywords").asText();
            String atmosphere = root.path("atmosphere").asText();
            String meetingSummary = root.path("meeting_summary").asText();
            String actionItems = root.path("action_items").asText();

            LocalDateTime startedAt = parseDateTime(startedAtStr);
            LocalDateTime endedAt = parseDateTime(endedAtStr);

            return new MeetingAnalysisDto(
                    null,
                    chatId,
                    topic,
                    startedAt,
                    endedAt,
                    keywords,
                    atmosphere,
                    meetingSummary,
                    actionItems
            );

        } catch (JsonProcessingException e) {
            throw new RuntimeException("회의 분석 결과 JSON 파싱 실패: " + e.getMessage(), e);
        }
    }

    // ==================== 참여자 개별 분석 ====================

    public MeetingParticipationDto analyzeMeetingParticipation(
            List<ContentDto> contentDtos,
            String participantName,
            int participantId) {

        // 1. 프롬프트 구성
        String prompt = buildParticipationPrompt(contentDtos, participantName);

        // 2. OpenAiChatModel 호출
        String jsonResponse = model.call(prompt);

        // 3. JSON 파싱 후 DTO 반환
        return parseMeetingParticipationResponse(jsonResponse, participantId);
    }

    private String buildParticipationPrompt(List<ContentDto> contentDtos, String participantName) {
        StringBuilder sb = new StringBuilder();

        sb.append(MEETING_PARTICIPATION_PROMPT_HEADER);
        sb.append(participantName).append("\n");
        sb.append(MEETING_PARTICIPATION_PROMPT_FOOTER);

        for (ContentDto content : contentDtos) {
            sb.append(String.format("[%s] (participantId: %d): %s%n",
                    content.getTime(),
                    content.getParticipantId(),
                    content.getContent()
            ));
        }

        return sb.toString();
    }

    private MeetingParticipationDto parseMeetingParticipationResponse(
            String jsonResponse,
            int participantId) {
        try {
            String cleanedJson = extractJson(jsonResponse);
            JsonNode root = objectMapper.readTree(cleanedJson);

            int meaningfulUtteranceCount = root.path("meaningful_utterance_count").asInt();
            String keyOpinion = root.path("key_opinion").asText();
            int participationScore = root.path("participation_score").asInt();
            int topicInitiationCount = root.path("topic_initiation_count").asInt();
            int reactionReceivedScore = root.path("reaction_received_score").asInt();
            String assignedTask = root.path("assigned_task").asText();

            return new MeetingParticipationDto(
                    null,
                    null,
                    participantId,
                    meaningfulUtteranceCount,
                    keyOpinion,
                    participationScore,
                    topicInitiationCount,
                    reactionReceivedScore,
                    assignedTask
            );

        } catch (JsonProcessingException e) {
            throw new RuntimeException("참여자 분석 결과 JSON 파싱 실패: " + e.getMessage(), e);
        }
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * LLM 응답에서 순수 JSON만 추출
     * (```json ... ``` 마크다운 블록이 포함된 경우 대비)
     */
    private String extractJson(String response) {
        if (response == null || response.isBlank()) {
            throw new RuntimeException("LLM 응답이 비어있습니다.");
        }

        String cleaned = response.trim();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceAll("(?s)```(?:json)?\\s*", "").trim();
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.lastIndexOf("```")).trim();
            }
        }
        return cleaned;
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (Exception e) {
            return null;
        }
    }
}
