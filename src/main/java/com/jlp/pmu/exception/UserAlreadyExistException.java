package com.jlp.pmu.exception;

import lombok.Data;
@Data
public class UserAlreadyExistException extends RuntimeException{
private String errorCode;
	

	public UserAlreadyExistException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

}



