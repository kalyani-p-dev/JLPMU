package com.jlp.pmu.exception;
import lombok.Data;

@Data
public class BranchNotFoundException extends RuntimeException{
	
	private String errorCode;
	
	public BranchNotFoundException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

}
