package com.jlp.pmu.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PrinterJobResponse {
	private Long printJobId;
	private LocalDateTime jobStartTime;
	private LocalDateTime jobEndTime;
	private Long printerId;
	private Boolean hold;
	private int noOfCopies;
}
