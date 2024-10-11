package com.jlp.pmu.dto;

import lombok.Data;

@Data
public class ReportRequest {
	private String userId;
	private Long reportId;
	private String reportName;
	private String dept;
	private String subDept;
	private Long printerId;
	private Boolean printjoboptions;
	private String comments;
	private Long branchCode;
	private int copies;
}
