package com.clab.content.controller;

import java.util.List;
import java.util.Map;

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
import com.clab.content.dto.ContentCategoryJoinDto;
import com.clab.content.dto.ContentDto;
import com.clab.content.service.ContentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/content")
@Tag(name = "ContentController", description = "대화 내용 관리 API")
public class ContentController {

	private final ContentService contentService;

	@GetMapping
	public ResponseEntity<ApiResponse> findAll() {
		List<ContentDto> result = contentService.findAll();
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse> findById(@PathVariable("id") int id) {
		ContentDto result = contentService.findById(id);
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@GetMapping("/participant/{participantId}")
	public ResponseEntity<ApiResponse> findByParticipantId(@PathVariable("participantId") int participantId) {
		List<ContentCategoryJoinDto> result = contentService.findByParticipantId(participantId);
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity.status(response.getStatus()).body(response);
	}
	
	@PostMapping
	public ResponseEntity<ApiResponse> insert(@RequestBody ContentDto dto) {
		int id = contentService.insert(dto);
		ApiResponse response = new ApiResponse(SuccessCode.INSERT_SUCCESS, Map.of("id", id));
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse> delete(@PathVariable("id") int id) {
		contentService.delete(id);
		ApiResponse response = new ApiResponse(SuccessCode.DELETE_SUCCESS, null);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@DeleteMapping("/participant/{participantId}")
	public ResponseEntity<ApiResponse> deleteByParticipantId(@PathVariable("participantId") int participantId) {
		contentService.deleteByParticipantId(participantId);
		ApiResponse response = new ApiResponse(SuccessCode.DELETE_SUCCESS, null);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

}
