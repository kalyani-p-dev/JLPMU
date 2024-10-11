package com.jlp.pmu.exception;

import lombok.Data;

@Data
public class BranchAlreadyExistException extends RuntimeException{
	
	private String errorCode;
	
	public BranchAlreadyExistException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
}
