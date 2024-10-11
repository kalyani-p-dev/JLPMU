package com.jlp.pmu.dto;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class RedirectPrinterRequest {
	private String userId;
	private Long branchCode;
	private String printerName;
	private String redirectPrintName;

}
