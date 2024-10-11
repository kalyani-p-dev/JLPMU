package com.jlp.pmu.dto;

import lombok.Data;

@Data
public class SearchJobDetailsRequest {
	private Long branchCode;
	private String searchKey;
}
