package com.clab.member.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.clab.common.exception.ApiResponse;
import com.clab.common.exception.SuccessCode;
import com.clab.common.security.CustomUserDetails;
import com.clab.member.dto.MemberDto;
import com.clab.member.dto.MemberUpdatePasswordDto;
import com.clab.member.dto.MemberUpdateDto;
import com.clab.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "MemberController", description = "회원 관리 API")
public class MemberController {

	private final MemberService memberService;

	@GetMapping
	@Operation(summary = "전체 사용자 조회")
	public ResponseEntity<ApiResponse> findAll() {
		List<MemberDto> result = memberService.findAll();
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@GetMapping("/{id}")
	@Operation(summary = "단일 사용자 조회")
	public ResponseEntity<ApiResponse> findById(@PathVariable int id) {
		MemberDto result = memberService.findById(id);
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@GetMapping("/me")
	@Operation(summary = "내 정보 조회")
	public ResponseEntity<ApiResponse> findByMyId(@AuthenticationPrincipal CustomUserDetails userDetails) {
		int id = userDetails.getMember().getId();
		MemberDto result = memberService.findById(id);
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@PostMapping("/join")
	@Operation(summary = "회원가입")
	public ResponseEntity<ApiResponse> join(@RequestBody MemberDto dto) {
		memberService.insert(dto);
		ApiResponse response = new ApiResponse(SuccessCode.INSERT_SUCCESS, "회원가입에 성공하였습니다.");
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	@PatchMapping(value = "/me/image", consumes = "multipart/form-data")
	@Operation(summary = "사용자 이미지 수정")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "multipart/form-data",
		encoding = {
			@Encoding(name = "image", contentType = "image/*")
		}))
	public ResponseEntity<ApiResponse> updateImage(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@Parameter(description = "프로필 이미지", schema = @Schema(type = "string", format = "binary"))
			@RequestPart(value = "image", required = false) MultipartFile image) {
		int id = userDetails.getMember().getId();
		memberService.updateImage(id, image);
		ApiResponse response = new ApiResponse(SuccessCode.UPDATE_SUCCESS, "사용자 정보가 수정 되었습니다.");
		return ResponseEntity.status(response.getStatus()).body(response);
	}
	
	@PatchMapping("/me/password")
	@Operation(summary = "사용자 비밀번호 수정")
	public ResponseEntity<ApiResponse> updatePassword(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestBody MemberUpdatePasswordDto dto
			){
		int id = userDetails.getMember().getId();
		memberService.updatePassword(id, dto);
		ApiResponse response = new ApiResponse(SuccessCode.UPDATE_SUCCESS, "사용자 정보가 수정 되었습니다.");
		return ResponseEntity.status(response.getStatus()).body(response);
	}
	
	@PatchMapping("/me")
	@Operation(summary = "사용자 정보 수정")
	public ResponseEntity<ApiResponse> update(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestBody MemberUpdateDto dto){
		int id = userDetails.getMember().getId();
		memberService.update(id, dto);
		ApiResponse response = new ApiResponse(SuccessCode.UPDATE_SUCCESS, "사용자 정보가 수정 되었습니다.");
		return ResponseEntity.status(response.getStatus()).body(response);
		
	}

	@DeleteMapping("/me")
	@Operation(summary = "사용자 정보 삭제")
	public ResponseEntity<ApiResponse> delete(@AuthenticationPrincipal CustomUserDetails userDetails) {
		int id = userDetails.getMember().getId();
		memberService.delete(id);
		ApiResponse response = new ApiResponse(SuccessCode.UPDATE_SUCCESS, "사용자 정보가 삭제 되었습니다.");
		return ResponseEntity.status(response.getStatus()).body(response);
	}
}
