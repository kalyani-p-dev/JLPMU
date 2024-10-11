package com.jlp.pmu.dto;

import java.time.LocalDateTime;

import com.jlp.pmu.enums.PrinterJobType;
import com.jlp.pmu.enums.StatusColor;
import com.jlp.pmu.enums.StatusType;

import lombok.Data;

@Data
public class TransactionResponse {
	private Long transactionId;
	private String printerName;
	private String reportName;
	private PrinterJobType type;
	private StatusType status;
	private String pdfPath;
	private String fixture;
	private String item;
	private String deptCode;
	private LocalDateTime arrivalTime;
	private String customerName;
	private String gen;
	private String defaultTitle;
	private LocalDateTime time;
	private int pages;
	private String ref;
	private Long branchCode;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String reportDescription;
	private Long formattedTime;
	private String pdfName;
	private StatusColor statusColor;
	
}
