package com.jlp.pmu.dto;

import java.util.List;

import lombok.Data;

@Data
public class PrintJobDetailsRequest {
	private String alternaterPrinterName;
	TransactionResponse printerTransaction;
	private Boolean printToAlternatePrinter;
	}
