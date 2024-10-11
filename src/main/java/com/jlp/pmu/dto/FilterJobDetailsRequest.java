package com.jlp.pmu.dto;

import java.util.List;

import com.jlp.pmu.enums.JobTypeInFilter;
import com.jlp.pmu.enums.PrinterType;

import lombok.Data;

@Data
public class FilterJobDetailsRequest {
	private Boolean isDataToBeFiltered;
	private Long branchCode;
	private List<String> printerNames;
	private List<PrinterType> printerTypes;
}
