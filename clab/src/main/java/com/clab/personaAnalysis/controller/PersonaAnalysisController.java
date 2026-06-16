package com.clab.personaAnalysis.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clab.common.exception.ApiResponse;
import com.clab.common.exception.SuccessCode;
import com.clab.personaAnalysis.dto.PersonaAnalysisDto;
import com.clab.personaAnalysis.service.PersonaAnalysisService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/persona-analysis")
@Tag(name = "PersonaAnalysisController", description = "인물 분석 관리 API")
public class PersonaAnalysisController {

	private final PersonaAnalysisService personaAnalysisService;

	@GetMapping
	@Operation(summary = "전체 조회")
	public ResponseEntity<ApiResponse> findAll() {
		List<PersonaAnalysisDto> result = personaAnalysisService.findAll();
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@GetMapping("/{id}")
	@Operation(summary = "단건 조회")
	public ResponseEntity<ApiResponse> findById(@PathVariable("id") int id) {
		PersonaAnalysisDto result = personaAnalysisService.findById(id);
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@GetMapping("/chat/{chatId}")
	@Operation(summary = "채팅 id로 조회")
	public ResponseEntity<ApiResponse> findByChattingId(@PathVariable("chatId") int chatId) {
		List<PersonaAnalysisDto> result = personaAnalysisService.findByChatId(chatId);
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@GetMapping("/participant/{participantId}")
	@Operation(summary = "참여자 id로 조회")
	public ResponseEntity<ApiResponse> findByParticipantId(@PathVariable("participantId") int participantId) {
		List<PersonaAnalysisDto> result = personaAnalysisService.findByParticipantId(participantId);
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@PostMapping
	@Operation(summary = "등록")
	public ResponseEntity<ApiResponse> insert(@RequestBody PersonaAnalysisDto dto) {
		int id = personaAnalysisService.insert(dto);
		ApiResponse response = new ApiResponse(SuccessCode.INSERT_SUCCESS, id);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@PatchMapping("/{id}")
	@Operation(summary = "수정")
	public ResponseEntity<ApiResponse> update(@PathVariable("id") int id, @RequestBody PersonaAnalysisDto dto) {
		personaAnalysisService.update(id, dto);
		ApiResponse response = new ApiResponse(SuccessCode.UPDATE_SUCCESS, null);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "삭제")
	public ResponseEntity<ApiResponse> delete(@PathVariable("id") int id) {
		personaAnalysisService.delete(id);
		ApiResponse response = new ApiResponse(SuccessCode.DELETE_SUCCESS, null);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

}
