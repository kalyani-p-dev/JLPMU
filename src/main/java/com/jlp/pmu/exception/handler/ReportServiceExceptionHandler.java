package com.jlp.pmu.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.jlp.pmu.dto.ErrorResponse;
import com.jlp.pmu.exception.ReportAlreadyExistsException;
import com.jlp.pmu.exception.ReportNotFoundException;

@ControllerAdvice
public class ReportServiceExceptionHandler extends ResponseEntityExceptionHandler{
	
	@ExceptionHandler(ReportNotFoundException.class)
	public ResponseEntity<ErrorResponse> reportNotFoundExceptionHandler(ReportNotFoundException exception){
		ErrorResponse response = ErrorResponse.builder()
				  .message(exception.getMessage())
				  .errorCode(exception.getErrorCode())
				  .build();
		
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(ReportAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> reportAlreadyExistsExceptionHandler(ReportAlreadyExistsException exception){
		ErrorResponse response = ErrorResponse.builder()
				  .message(exception.getMessage())
				  .errorCode(exception.getErrorCode())
				  .build();
		
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.ALREADY_REPORTED);
	}
}
