package com.clab.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {
	// Common Error --- BEGIN
	INVALID_REQUEST(400, "ERR-REQ-400", "잘못된 요청입니다."),
    INVALID_JSON(400, "ERR-JSON-400", "요청 본문 형식이 올바르지 않습니다."),
    UNAUTHORIZED(401, "ERR-AUTH-401", "인증이 필요합니다."),
    FORBIDDEN(403, "ERR-FORBIDDEN-403", "접근 권한이 없습니다."),
    NOT_FOUND(404, "ERR-NOTFOUND-404", "대상을 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(500, "ERR-SERVER-500", "서버 내부 오류가 발생했습니다."),
	// Common Error --- END

	
	// Chat Error --- BEGIN
	CHAT_NOT_FOUND(404, "CHAT-001", "대화가 존재하지 않습니다."),
	CHAT_BAD_REQUEST(400, "CHAT-002", "잘못된 접근입니다. DB에 수정이 일어나지 않았습니다."),
	// Chat Error --- END
	
	// ChatFile Error --- BEGIN
	CHATFILE_DIRECTORY_ERROR(500,"CHATFILE-001", "디렉토리를 생성할 수 없습니다."),
	CHATFILE_SAVED_ERROR(500,"CHATFILE-002", "파일을 저장할 수 없습니다."),
	CHATFILE_BAD_REQUEST(400,"CHATFILE-003", "잘못된 접근입니다. DB에 수정이 일어나지 않았습니다."),
	CHATFILE_DB_NOT_FOUND(404,"CHATFILE-004", "DB의 대화파일이 존재하지 없습니다."),
	CHATFILE_NOT_FOUND(404,"CHATFILE-005", "대화파일이 존재하지 없습니다."),
	CHATFILE_DELETE_FAILED(400,"CHATFILE-006", "대화파일 삭제에 실패하였습니다."),
	
	
	// ChatFile Error --- END
		
	
	// Category Error --- BEGIN
	CATEGORY_NOT_FOUND(404, "CATE-001", "카테고리를 조회할 수 없습니다."),
	CATEGORY_BAD_REQUEST(400, "CATE-002", "잘못된 접근입니다. 카테고리 DB에 적용이 되지 않았습니다."),
	CATEGORY_INSERT_FAILED(400, "CATE-003", "카테고리 생성에 실패하였습니다."),
	CATEGORY_UPDATE_FAILED(400, "CATE-004", "카테고리 수정에 실패하였습니다."),
	CATEGORY_DELETE_FAILED(400, "CATE-005", "카테고리 삭제에 실패하였습니다."),
	// Category Error --- END
	
	// Common Error --- BEGIN
	
	// Common Error --- END
	
	// Content Error --- BEGIN
	CONTENT_NOT_FOUND(404, "CONT-001", "대화 내용을 조회할 수 없습니다."),
	CONTENT_BAD_REQUEST(400, "CONT-002", "잘못된 접근입니다. 대화 내용 DB에 적용이 되지 않았습니다."),
	CONTENT_INSERT_FAILED(400, "CONT-003", "대화 내용 생성에 실패하였습니다."),
	CONTENT_UPDATE_FAILED(400, "CONT-004", "대화 내용 수정에 실패하였습니다."),
	CONTENT_DELETE_FAILED(400, "CONT-005", "대화 내용 삭제에 실패하였습니다."),
	// Content Error --- END
	
	// Content-Category Error --- BEGIN
	CONTENT_CATEGORY_NOT_FOUND(404, "COCA-001", "내용-카테고리를 조회할 수 없습니다."),
	CONTENT_CATEGORY_BAD_REQUEST(400, "COCA-002", "잘못된 접근입니다. 내용-카테고리 DB에 적용이 되지 않았습니다."),
	CONTENT_CATEGORY_INSERT_FAILED(400, "COCA-003", "내용-카테고리 생성에 실패하였습니다."),
	CONTENT_CATEGORY_UPDATE_FAILED(400, "COCA-004", "내용-카테고리 수정에 실패하였습니다."),
	CONTENT_CATEGORY_DELETE_FAILED(400, "COCA-005", "내용-카테고리 삭제에 실패하였습니다."),
	// Content-Category Error --- END
	
	// Member Error --- BEGIN
	MEMBER_NOT_FOUND(404,"MEM-001", "사용자를 조회할 수 없습니다."),
	MEMBER_DUPLICATED(409, "MEM-002", "이미 존재하는 사용자입니다."),
	MEMBER_BAD_REQUEST(400,"MEM-003", "잘못된 접근입니다. DB에 수정이 발생하지 않았습니다."),
	// Member Error --- END
	
	// Auth Error --- BEGIN
	INVALID_CREDENTIALS(401, "ERR-AUTH-001", "아이디 또는 비밀번호를 잘못 입력했습니다."),
	TOKEN_EXPIRED(401, "ERR-AUTH-002", "토큰이 만료되었습니다."),
	TOKEN_INVALID(401, "ERR-AUTH-003", "유효하지 않은 토큰입니다."),
	
	// Auth Error --- END
	
	// Participant Error --- BEGIN
	PARTICIPANT_NOT_FOUND(404, "PART-001", "참여자를 조회할 수 없습니다."),
	PARTICIPANT_BAD_REQUEST(400, "PART-002", "잘못된 접근입니다. 참여자 DB에 적용이 되지 않았습니다."),
	PARTICIPANT_INSERT_FAILED(400, "PART-003", "참여자 생성에 실패하였습니다."),
	PARTICIPANT_UPDATE_FAILED(400, "PART-004", "참여자 수정에 실패하였습니다."),
	PARTICIPANT_DELETE_FAILED(400, "PART-005", "참여자 삭제에 실패하였습니다."),
	// Participant Error --- END
	
	// Participant-Category Error --- BEGIN
	PARTICIPANT_CATEGORY_NOT_FOUND(404, "PACA-001", "참여자-카테고리를 조회할 수 없습니다."),
	PARTICIPANT_CATEGORY_BAD_REQUEST(400, "PACA-002", "잘못된 접근입니다. 참여자-카테고리 DB에 적용이 되지 않았습니다."),
	PARTICIPANT_CATEGORY_INSERT_FAILED(400, "PACA-003", "참여자-카테고리 생성에 실패하였습니다."),
	PARTICIPANT_CATEGORY_UPDATE_FAILED(400, "PACA-004", "참여자-카테고리 수정에 실패하였습니다."),
	PARTICIPANT_CATEGORY_DELETE_FAILED(400, "PACA-005", "참여자-카테고리 삭제에 실패하였습니다."),
	// Participant-Category Error --- END
	
	// Persona Error --- BEGIN
	PERSONA_NOT_FOUND(404, "PERS-001", "인물상을 조회할 수 없습니다."),
	PERSONA_BAD_REQUEST(400, "PERS-002", "잘못된 접근입니다. 인물상 DB에 적용되지 않았습니다."),
	PERSONA_INSERT_FAILED(400, "PERS-003", "인물상 생성에 실패하였습니다."),
	PERSONA_UPDATE_FAILED(400, "PERS-004", "인물상 수정에 실패하였습니다."),
	PERSONA_DELETE_FAILED(400, "PERS-005", "인물상 삭제에 실패하였습니다."),
	// Persona Error --- END

	// PersonaAnalysis Error --- BEGIN
	PERSONA_ANALYSIS_NOT_FOUND(404, "PA-001", "인물 분석을 조회할 수 없습니다."),
	PERSONA_ANALYSIS_INSERT_FAILED(400, "PA-002", "인물 분석 생성에 실패하였습니다."),
	PERSONA_ANALYSIS_UPDATE_FAILED(400, "PA-003", "인물 분석 수정에 실패하였습니다."),
	PERSONA_ANALYSIS_DELETE_FAILED(400, "PA-004", "인물 분석 삭제에 실패하였습니다."),
	// PersonaAnalysis Error --- END
	
	// MeetingAnalysis Error --- BEGIN
	MEETING_ANALYSIS_NOT_FOUND(404, "MA-001", "회의 분석을 조회할 수 없습니다."),
	MEETING_ANALYSIS_INSERT_FAILED(400, "MA-002", "회의 분석 생성에 실패하였습니다."),
	MEETING_ANALYSIS_UPDATE_FAILED(400, "MA-003", "회의 분석 수정에 실패하였습니다."),
	MEETING_ANALYSIS_DELETE_FAILED(400, "MA-004", "회의 분석 삭제에 실패하였습니다."),
	// MeetingAnalysis Error --- END
	
	// MeetingParticipation Error --- BEGIN
	MEETING_PARTICIPATION_NOT_FOUND(404, "MP-001", "회의 참여을 조회할 수 없습니다."),
	MEETING_PARTICIPATION_INSERT_FAILED(400, "MP-002", "회의 참여 생성에 실패하였습니다."),
	MEETING_PARTICIPATION_UPDATE_FAILED(400, "MP-003", "회의 참야 수정에 실패하였습니다."),
	MEETING_PARTICIPATIONDELETE_FAILED(400, "MP-004", "회의 참여 삭제에 실패하였습니다."),
	// MeetingParticipation Error --- END
	
	; // End
	

	private final int status;
	private final String code;
	private final String message;
}

