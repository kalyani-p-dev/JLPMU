package com.jlp.pmu.exception;

import lombok.Data;

@Data
public class BaseException extends RuntimeException {
private String errorCode;
	
	public BaseException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
	

}
