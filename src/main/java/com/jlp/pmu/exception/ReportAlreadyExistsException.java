package com.jlp.pmu.exception;

import lombok.Data;


@Data
public class ReportAlreadyExistsException extends RuntimeException {
	private String errorCode;

	public ReportAlreadyExistsException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
}
