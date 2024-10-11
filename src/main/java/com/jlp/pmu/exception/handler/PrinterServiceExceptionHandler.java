package com.jlp.pmu.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.jlp.pmu.dto.ErrorResponse;
import com.jlp.pmu.exception.PrinterAlreadyExistException;
import com.jlp.pmu.exception.PrinterNotFoundException;


@ControllerAdvice
public class PrinterServiceExceptionHandler extends ResponseEntityExceptionHandler{
	@ExceptionHandler(PrinterNotFoundException.class)
	public ResponseEntity<ErrorResponse> printerNotFoundExceptionHandler(PrinterNotFoundException exception){
		
		ErrorResponse response = ErrorResponse.builder()
											  .message(exception.getMessage())
											  .errorCode(exception.getErrorCode())
											  .build();
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.NOT_FOUND);
		
	}
	
	@ExceptionHandler(PrinterAlreadyExistException.class)
	public ResponseEntity<ErrorResponse> printerAlreadyExistExceptionHandler(PrinterAlreadyExistException exception){
		
		ErrorResponse response = ErrorResponse.builder()
											  .message(exception.getMessage())
											  .errorCode(exception.getErrorCode())
											  .build();
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.NOT_FOUND);
		
	}

}
