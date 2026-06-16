package com.clab.content_category.controller;

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
import com.clab.content_category.dto.ContentCategoryDto;
import com.clab.content_category.service.ContentCategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/content-category")
public class ContentCategoryController {
	
	private final ContentCategoryService contentCategoryService;
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse> findById(@PathVariable("id") int id){
		ContentCategoryDto result = contentCategoryService.findById(id);
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
	@GetMapping("/content/{contentId}")
	public ResponseEntity<ApiResponse> findByContentId(@PathVariable("contentId") int contentId){
		List<ContentCategoryDto> result = contentCategoryService.findByContentId(contentId);
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
	@PostMapping
	public ResponseEntity<ApiResponse> insert(@RequestBody ContentCategoryDto dto){
		int id = contentCategoryService.insert(dto);
		ApiResponse response = new ApiResponse(SuccessCode.INSERT_SUCCESS, id);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse> delete(@PathVariable("id") int id){
		contentCategoryService.delete(id);
		ApiResponse response = new ApiResponse(SuccessCode.DELETE_SUCCESS, null);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
}
