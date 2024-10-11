package com.jlp.pmu.service;

import java.util.List;

import com.jlp.pmu.dto.FilterJobDetailsRequest;
import com.jlp.pmu.dto.JobDetailsListRequest;
import com.jlp.pmu.dto.JobDetailsResponse;
import com.jlp.pmu.dto.PrintJobDetailsRequest;
import com.jlp.pmu.dto.PrintSelectedPagesRequest;
import com.jlp.pmu.dto.TransactionResponse;
import com.jlp.pmu.pojo.ReportMetaData;

public interface JobDetailsService {

	public List<JobDetailsResponse> getListOfJobDetails(JobDetailsListRequest request);

	public String printSelectedPages(PrintSelectedPagesRequest request);

	public String reprocessJob(List<TransactionResponse> jobDetails);
	
	void printJobDetails (PrintJobDetailsRequest requestDto);
	
	String storeTransactionVariables(ReportMetaData metaData);
	
	JobDetailsResponse getAll_or_filter_jobDetails(FilterJobDetailsRequest request);
}
