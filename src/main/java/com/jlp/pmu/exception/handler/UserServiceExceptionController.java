package com.jlp.pmu.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.jlp.pmu.dto.ErrorResponse;
import com.jlp.pmu.exception.UserAlreadyExistException;
import com.jlp.pmu.exception.UserNotFoundException;

@ControllerAdvice
public class UserServiceExceptionController extends ResponseEntityExceptionHandler {

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> userNotFoundExceptionHandler(UserNotFoundException exception){
		
		ErrorResponse response = ErrorResponse.builder()
											  .message(exception.getMessage())
											  .errorCode(exception.getErrorCode())
											  .build();
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.NOT_FOUND);
		
	}
	
	@ExceptionHandler(UserAlreadyExistException.class)
	public ResponseEntity<ErrorResponse> userAlreadyExistExceptionHandler(UserAlreadyExistException exception){
		
		ErrorResponse response = ErrorResponse.builder()
											  .message(exception.getMessage())
											  .errorCode(exception.getErrorCode())
											  .build();
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.ALREADY_REPORTED);
		
	}
	
}
