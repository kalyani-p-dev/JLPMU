package com.jlp.pmu.exception;

import lombok.Data;
@Data
public class TemplateNotFoundException extends RuntimeException{

	private String errorCode;
	public TemplateNotFoundException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

}


