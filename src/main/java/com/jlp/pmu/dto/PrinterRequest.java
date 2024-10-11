package com.jlp.pmu.dto;

import java.util.List;

import com.jlp.pmu.enums.PrinterType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class PrinterRequest {
	private String userId;
	private Long branchCode;
	private Long printerID;
	private String printerName;
	private String pmuPrinterName;
	private String printerPATH;
	private PrinterType printerType;
	private String comments;
	private String pmuServer;
	private Boolean localAttachedPrinter;
	

	}
