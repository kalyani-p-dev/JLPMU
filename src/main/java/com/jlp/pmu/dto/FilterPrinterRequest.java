package com.jlp.pmu.dto;

import java.util.List;

import com.jlp.pmu.enums.JobTypeInFilter;
import com.jlp.pmu.enums.PrinterType;

import lombok.Data;

@Data
public class FilterPrinterRequest {
	private Boolean isDataToBeFiltered;
	private Long branchCode;
	private List<PrinterType> printerTypes;
	private List<JobTypeInFilter> printerJobTypes;
	private List<String> stationeries;
	private List<String> genReportNames;
	private List<String> repReportNames;
}
