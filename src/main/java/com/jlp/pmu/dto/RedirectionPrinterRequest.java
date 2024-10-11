package com.jlp.pmu.dto;

import lombok.Data;

@Data
public class RedirectionPrinterRequest {
	
	public class RedirectPrinterRequest {
		private String userId;
		private Long branchCode;
		private String printerName;
		private String redirectPrintName;

	}

}
