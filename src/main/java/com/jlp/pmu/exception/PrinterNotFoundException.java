package com.jlp.pmu.exception;

import lombok.Data;

@Data
public class PrinterNotFoundException extends RuntimeException {
private String errorCode;
	
	public PrinterNotFoundException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
	

}
