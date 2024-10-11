package com.jlp.pmu.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.jlp.pmu.dto.ErrorResponse;
import com.jlp.pmu.exception.TemplateAlreadyExistException;
import com.jlp.pmu.exception.TemplateNotFoundException;


@ControllerAdvice
public class TemplateServiceExceptionHandler extends ResponseEntityExceptionHandler{
	@ExceptionHandler(TemplateNotFoundException.class)
	public ResponseEntity<ErrorResponse> templateNotFoundExceptionHandler(TemplateNotFoundException exception){
		
		ErrorResponse response = ErrorResponse.builder()
											  .message(exception.getMessage())
											  .errorCode(exception.getErrorCode())
											  .build();
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.NOT_FOUND);
		
	}
	
	@ExceptionHandler(TemplateAlreadyExistException.class)
	public ResponseEntity<ErrorResponse> templaterAlreadyExistExceptionHandler(TemplateAlreadyExistException exception){
		
		ErrorResponse response = ErrorResponse.builder()
											  .message(exception.getMessage())
											  .errorCode(exception.getErrorCode())
											  .build();
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.NOT_FOUND);
		
	}

}
