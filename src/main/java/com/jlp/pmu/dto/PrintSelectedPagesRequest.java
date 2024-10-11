package com.jlp.pmu.dto;

import lombok.Data;

@Data
public class PrintSelectedPagesRequest {
	
	TransactionResponse printerTransaction;
	private	Boolean printsAllPages;
	private int startRange;
	private int endRange;
	
}
