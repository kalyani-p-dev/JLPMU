package com.jlp.pmu.exception;

import lombok.Data;

@Data
public class PrinterAlreadyExistException extends RuntimeException {
	private String errorCode;
	
	public PrinterAlreadyExistException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
}
