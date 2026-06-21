package com.clab.meetingAnalysis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clab.common.exception.ApiResponse;
import com.clab.common.exception.SuccessCode;
import com.clab.meetingAnalysis.dto.MeetingAnalysisDto;
import com.clab.meetingAnalysis.service.MeetingAnalysisService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meeting-analysis")
@Tag(name = "MeetingAnalysisController", description = "회의 분석 관리 API")
public class MeetingAnalysisController {
	
	private final MeetingAnalysisService service;
	
	@GetMapping("/chat/{chatId}")
	public ResponseEntity<ApiResponse> findByChatId(@PathVariable("chatId") int chatId){
		MeetingAnalysisDto dto = service.findByChatId(chatId);
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, dto);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
	
	@PostMapping
	public ResponseEntity<ApiResponse> insert(@RequestBody MeetingAnalysisDto dto){
		int id = service.insert(dto);
		ApiResponse response = new ApiResponse(SuccessCode.INSERT_SUCCESS, id);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
}
