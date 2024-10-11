package com.jlp.pmu.exception;

import lombok.Data;

@Data
public class MnemonicAlreadyExistException extends RuntimeException{
	
private String errorCode;
	
	public MnemonicAlreadyExistException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
}
