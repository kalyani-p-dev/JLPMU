package com.jlp.pmu.exception;

import lombok.Data;

@Data 
public class ReportNotFoundException extends RuntimeException{
	
	private String errorCode;
	
	public ReportNotFoundException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
}
