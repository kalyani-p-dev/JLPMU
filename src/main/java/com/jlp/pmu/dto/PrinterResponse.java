package com.jlp.pmu.dto;

import com.jlp.pmu.enums.PrinterType;

import lombok.Builder;
import lombok.Data;

@Data
public class PrinterResponse {
	private Long printerID;
	private String printerName;
	private String pmuPrinterName;
	private String printerPATH;
	private PrinterType printerType;
	private String comments;
	private String pmuServer;
	private Boolean localAttachedPrinter;
	private String redirectPrintName;
}
