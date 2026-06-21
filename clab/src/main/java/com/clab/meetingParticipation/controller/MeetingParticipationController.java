package com.clab.meetingParticipation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clab.common.exception.ApiResponse;
import com.clab.common.exception.SuccessCode;
import com.clab.meetingParticipation.dto.MeetingParticipationDto;
import com.clab.meetingParticipation.service.MeetingParticipationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meeting-participation")
@Tag(name = "MeetingParticipationController", description = "회의 참여 관리 API")
public class MeetingParticipationController {
	
	private final MeetingParticipationService service;
	
	@GetMapping("/meeting-analysis/{meetingAnalysisId}")
	public ResponseEntity<ApiResponse> findByMeetingAnalysisId(@PathVariable("meetingAnalysisId") int meetingAnalysisId){
		List<MeetingParticipationDto> result = service.findByMeetingAnalysisId(meetingAnalysisId);
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
	
	@PostMapping
	public ResponseEntity<ApiResponse> insert(@RequestBody MeetingParticipationDto dto){
		int id = service.insert(dto);
		ApiResponse response = new ApiResponse(SuccessCode.INSERT_SUCCESS, id);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}

}
