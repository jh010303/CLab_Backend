package com.clab.chat.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
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

import com.clab.chat.dto.ChatDto;
import com.clab.chat.service.ChatService;
import com.clab.chat_file.dto.ParsedMessage;
import com.clab.chat_file.service.ChatParserService;
import com.clab.common.exception.ApiResponse;
import com.clab.common.exception.SuccessCode;
import com.clab.content.dto.ContentDto;
import com.clab.content.service.ContentService;
import com.clab.participant.dto.ParticipantDto;
import com.clab.participant.service.ParticipantService;

import io.jsonwebtoken.io.IOException;
import com.clab.common.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
@Tag(name = "ChatController", description = "대화 관리 API")
public class ChatController {
	
	private final ChatService chatService;
	private final ChatParserService charParserService;
	private final ParticipantService participantService;
	private final ContentService contentService;
	
	@GetMapping
	@Operation(summary = "전체 채팅 목록 조회", description = "admin이 추가 되면 사용함")
	public ResponseEntity<ApiResponse> findAll(){
		List<ChatDto> result = chatService.findAll();
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
	
	@GetMapping("/me")
	@Operation(summary = "내 채팅 목록 조회")
	public ResponseEntity<ApiResponse> findAllByUserId(@AuthenticationPrincipal CustomUserDetails userDetails){
		int userId = userDetails.getMember().getId();
		List<ChatDto> result = chatService.findAllByUserId(userId);
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
	
	@GetMapping("/{id}")
	@Operation(summary = "채팅 조회")
	public ResponseEntity<ApiResponse> findById(@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable("id") int id) {
		int userId = userDetails.getMember().getId();
		ChatDto result = chatService.findById(userId, id);
		ApiResponse response = new ApiResponse(SuccessCode.SELECT_SUCCESS, result);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}

	@PostMapping(consumes = "multipart/form-data")
	@Operation(summary = "채팅 생성", description = "분석할 파일 + 제목 + 내용 업로드하면 채팅 생성됨")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "multipart/form-data",
		encoding = {
			@Encoding(name = "dto", contentType = "application/json"),
			@Encoding(name = "file", contentType = "application/octet-stream")
		}))
	public ResponseEntity<ApiResponse> insert(@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestPart("dto") ChatDto dto,
			@Parameter(description = "업로드할 파일", schema = @Schema(type = "string", format = "binary"))
			@RequestPart("file") MultipartFile file) {
		int userId = userDetails.getMember().getId();
		int id = chatService.insert(dto,file,userId);
		
		List<ParsedMessage> messages = charParserService.parseChatLog(file);
		List<String> participantNames = charParserService.extractParticipants(messages);
		
		for (String name : participantNames) {
	      // 해당 참여자 메시지 필터링
	      List<ParsedMessage> myMessages = messages.stream()
	          .filter(m -> m.getSender().equals(name))
	          .collect(Collectors.toList());
	      
	      ParticipantDto participantDto = new ParticipantDto();
	      participantDto.setChatId(id);
	      participantDto.setName(name);
	      participantDto.setCount(myMessages.size());
	      participantDto.setScore(100);
	      participantDto.setAverageReplyTime(90);
	      participantDto.setChatLength(
	          (long) myMessages.stream()
	              .mapToInt(m -> m.getContent().length())
	              .sum()
	      );
	      participantDto.setComment("---");
	      participantDto.setAlias(name);
	      participantDto.setPersonaId(1);
	      participantService.insert(participantDto);
	      
	      int participantId = participantDto.getId();
	      
	      for(ParsedMessage m : myMessages) {
	      	ContentDto c = new ContentDto(null, participantId, m.getContent(), m.getTime());
	      	contentService.insert(c);
	      }
		}
		
		ApiResponse response = new ApiResponse(SuccessCode.INSERT_SUCCESS, Map.of("id", id));
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
	
	@PatchMapping("/{id}")
	@Operation(summary = "채팅 수정", description = "제목, 내용만 수정")
	public ResponseEntity<ApiResponse> update(@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable("id") int id, @RequestBody ChatDto dto) {
		int userId = userDetails.getMember().getId();
		chatService.update(userId, id, dto);
		ApiResponse response = new ApiResponse(SuccessCode.UPDATE_SUCCESS, null);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}
	
	@DeleteMapping("/{id}")
	@Operation(summary = "채팅 삭제")
	public ResponseEntity<ApiResponse> delete(@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable("id") int id) {
		int userId = userDetails.getMember().getId();
		chatService.delete(userId, id);
		ApiResponse response = new ApiResponse(SuccessCode.DELETE_SUCCESS, null);
		return ResponseEntity
				.status(response.getStatus())
				.body(response);
	}

}
