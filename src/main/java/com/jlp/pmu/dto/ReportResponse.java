package com.jlp.pmu.dto;

import com.jlp.pmu.enums.PrinterType;

import lombok.Data;

@Data
public class ReportResponse {
	private Long reportId;
	private String reportName;
	private String printerName;
	private String dept;
	private String subDept;
	private PrinterResponse printer;
	private Boolean printjoboptions;
	private String comments;
	private Long branchCode;
	private int copies;
	private PrinterType printerType;
}
