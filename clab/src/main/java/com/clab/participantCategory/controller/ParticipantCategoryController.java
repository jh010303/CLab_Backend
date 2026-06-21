package com.clab.participantCategory.controller;

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
import com.clab.participantCategory.dto.ParticipantCategoryDto;
import com.clab.participantCategory.service.ParticipantCategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/participant-category")
public class ParticipantCategoryController {
	
	private final ParticipantCategoryService service;
	
	@GetMapping("/participant/{participantId}")
	public ResponseEntity<ApiResponse> findByParticipantId(@PathVariable("participantId") int participantId){
		List<ParticipantCategoryDto> result = service.findByParticipantId(participantId);
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}

	@PostMapping
	public ResponseEntity<ApiResponse> insert(@RequestBody ParticipantCategoryDto dto){
		int id = service.insert(dto);
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, id);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse> delete(@PathVariable("id") int id) {
		service.delete(id);
		ApiResponse response = new ApiResponse(SuccessCode.DELETE_SUCCESS, null);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
	
	@DeleteMapping("/participant/{participantId}")
	public ResponseEntity<ApiResponse> deleteByParticipantId(@PathVariable("participantId") int participantId) {
		service.deleteByParticipantId(participantId);
		ApiResponse response = new ApiResponse(SuccessCode.DELETE_SUCCESS, null);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}	
}
