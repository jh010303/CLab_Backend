package com.clab.participant.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clab.common.exception.ApiResponse;
import com.clab.common.exception.SuccessCode;
import com.clab.participant.dto.ParticipantDto;
import com.clab.participant.dto.ParticipantMeetingDto;
import com.clab.participant.dto.ParticipantPersonaDto;
import com.clab.participant.service.ParticipantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/participant")
@Tag(name = "ParticipantController", description = "참여자 관리 API")
public class ParticipantController {
	
	private final ParticipantService participantService;
	
	@GetMapping
	@Operation(summary = "전체 조회")
	public ResponseEntity<ApiResponse> findAll() {
		List<ParticipantDto> result = participantService.findAll();
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
	
	@GetMapping("/{id}")
	@Operation(summary = "단건 조회")
	public ResponseEntity<ApiResponse> findById(@PathVariable("id") int id) {
		ParticipantDto result = participantService.findById(id);
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
	
	@GetMapping("/persona/{chatId}")
	@Operation(summary = "참여자+페르소나 분석 조회")
	public ResponseEntity<ApiResponse> findPersonaById(@PathVariable("chatId") int chatId) {
		List<ParticipantPersonaDto> result = participantService.findPersonaById(chatId);
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
	
	@GetMapping("/meeting/{chatId}")
	@Operation(summary = "참여자+회의 분석 조회")
	public ResponseEntity<ApiResponse> findMeetingById(@PathVariable("chatId") int chatId) {
		List<ParticipantMeetingDto> result = participantService.findMeetingById(chatId);
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
	
	@GetMapping("/chat/{chatId}")
	@Operation(summary = "대화id로 조회")
	public ResponseEntity<ApiResponse> findByChatId(@PathVariable("chatId") int chatId) {
		List<ParticipantDto> result = participantService.findByChatId(chatId);
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
	
	@PostMapping
	@Operation(summary = "등록")
	public ResponseEntity<ApiResponse> insert(@RequestBody ParticipantDto dto) {
		int id = participantService.insert(dto);
		ApiResponse response = new ApiResponse(SuccessCode.INSERT_SUCCESS, id);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "id로 삭제")
	public ResponseEntity<ApiResponse> delete(@PathVariable("id") int id) {
		participantService.delete(id);
		ApiResponse response = new ApiResponse(SuccessCode.DELETE_SUCCESS, null);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
	
	@DeleteMapping("/chat/{chatId}")
	@Operation(summary = "chatId로 삭제")
	public ResponseEntity<ApiResponse> deleteByChatId(@PathVariable("chatId") int chatId) {
		participantService.deleteByChatId(chatId);
		ApiResponse response = new ApiResponse(SuccessCode.DELETE_SUCCESS, null);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
	
	

}
