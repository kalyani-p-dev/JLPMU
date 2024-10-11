package com.jlp.pmu.dto;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class PrinterNamesListRequset {
	private String userId;
	private Long branchCode;
	private String printerName;
	
	}
