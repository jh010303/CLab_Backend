package com.clab.common.exception;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	@ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse> handleCustomException(
            CustomException exception) {

        return ResponseEntity
                .status(exception.getErrorCode().getStatus())
                .body(new ApiResponse(exception.getErrorCode(), null));
    }
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();

        for (FieldError error :
                exception.getBindingResult().getFieldErrors()) {

            errors.put(
                    error.getField(),
                    error.getDefaultMessage()
            );
        }

        return ResponseEntity
                .badRequest()
                .body(new ApiResponse(
                        ErrorCode.INVALID_REQUEST,
                        errors
                ));
    }
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleJsonException(
            HttpMessageNotReadableException exception) {

        log.error("JSON Parsing Error", exception);

        return ResponseEntity
                .badRequest()
                .body(new ApiResponse(
                        ErrorCode.INVALID_JSON,
                        null
                ));
    }
	
	@ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(
            AccessDeniedException exception) {

        log.error("Access Denied", exception);

        return ResponseEntity
                .status(ErrorCode.FORBIDDEN.getStatus())
                .body(new ApiResponse(
                        ErrorCode.FORBIDDEN,
                        null
                ));
    }
	
//	@ExceptionHandler(AuthenticationException.class)
//    public ResponseEntity<ApiResponse> handleAuthenticationException(
//            AuthenticationException exception) {
//
//        log.error("Authentication Error", exception);
//
//        return ResponseEntity
//                .status(ErrorCode.UNAUTHORIZED.getStatus())
//                .body(new ApiResponse(
//                        ErrorCode.UNAUTHORIZED,
//                        null
//                ));
//    }

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse> handleException(
	        Exception exception) {

	    exception.printStackTrace();

	    log.error("Unhandled Exception", exception);

	    return ResponseEntity
	            .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
	            .body(new ApiResponse(
	                    ErrorCode.INTERNAL_SERVER_ERROR,
	                    null
	            ));
	}

}
