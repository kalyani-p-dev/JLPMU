package com.jlp.pmu.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.jlp.pmu.dto.ErrorResponse;
import com.jlp.pmu.exception.BranchAlreadyExistException;
import com.jlp.pmu.exception.BranchNotFoundException;
import com.jlp.pmu.exception.MnemonicAlreadyExistException;

@ControllerAdvice
public class BranchServiceExceptionHandler extends ResponseEntityExceptionHandler{
	
	@ExceptionHandler(BranchNotFoundException.class)
	public ResponseEntity<ErrorResponse> branchNotFoundExceptionHandler(BranchNotFoundException exception){
		
		ErrorResponse response = ErrorResponse.builder()
											  .message(exception.getMessage())
											  .errorCode(exception.getErrorCode())
											  .build();
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.NOT_FOUND);
		
	}
	
	@ExceptionHandler(BranchAlreadyExistException.class)
	public ResponseEntity<ErrorResponse> branchAlreadyExistExceptionHandler(BranchAlreadyExistException exception){
		
		ErrorResponse response = ErrorResponse.builder()
											  .message(exception.getMessage())
											  .errorCode(exception.getErrorCode())
											  .build();
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.ALREADY_REPORTED);
		
	}
	
	@ExceptionHandler(MnemonicAlreadyExistException.class)
	public ResponseEntity<ErrorResponse> mnemonicAlreadyExistExceptionHandler(MnemonicAlreadyExistException exception){
		
		ErrorResponse response = ErrorResponse.builder()
											  .message(exception.getMessage())
											  .errorCode(exception.getErrorCode())
											  .build();
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.ALREADY_REPORTED);
		
	}

}
