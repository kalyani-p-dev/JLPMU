package com.jlp.pmu.exception;

import lombok.Data;

@Data
public class TemplateAlreadyExistException extends RuntimeException{
	
private String errorCode;
	public TemplateAlreadyExistException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}	
	

}




