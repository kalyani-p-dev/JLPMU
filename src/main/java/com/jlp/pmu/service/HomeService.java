package com.jlp.pmu.service;

import java.util.List;

import com.jlp.pmu.dto.FilterPrinterRequest;
import com.jlp.pmu.dto.HomePagePrintersResponse;
import com.jlp.pmu.dto.HomeTestPrintRequest;
import com.jlp.pmu.dto.JobDetailsListRequest;
import com.jlp.pmu.dto.JobDetailsResponse;
import com.jlp.pmu.dto.PrinterRequest;
import com.jlp.pmu.dto.TransactionResponse;

public interface HomeService {

	List<JobDetailsResponse> getListOfJobDetailsOfPrinter(JobDetailsListRequest request);

	List<HomePagePrintersResponse> getHomePagePrinters(Long branchCode);

	List<HomePagePrintersResponse> filterPrinters(FilterPrinterRequest request);
	
	void printtestpageDetails(HomeTestPrintRequest requestDto);

	List<TransactionResponse> searchJobDetails(Long branchCode, String serachKey);

}
