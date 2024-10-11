package com.jlp.pmu.exception;

import lombok.Data;

@Data
public class UserNotFoundException extends RuntimeException{
	private String errorCode;
	
	public UserNotFoundException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
}
