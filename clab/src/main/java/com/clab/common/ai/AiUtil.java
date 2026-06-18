package com.clab.common.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import com.clab.content.dto.ContentDto;
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
				result.add(i < parsed.size() ? parsed.get(i) : Collections.emptyList());
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
}
