package com.jlp.pmu.dto;

import java.util.List;

import lombok.Data;

@Data
public class JobDetailsListRequest {
	private String userId;
	private Long branchCode;
	private List<String> printerNames;
	private List<String> reportNames;
}
