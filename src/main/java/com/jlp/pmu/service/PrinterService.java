package com.jlp.pmu.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.jlp.pmu.dto.FilterPrinterRequest;
import com.jlp.pmu.dto.PrinterNamesListRequset;
import com.jlp.pmu.dto.PrinterRequest;
import com.jlp.pmu.dto.PrinterResponse;
import com.jlp.pmu.dto.RedirectPrinterRequest;
import com.jlp.pmu.enums.PrinterType;

public interface PrinterService {
	
	PrinterResponse addPrinterDetails(PrinterRequest requestDto);
	PrinterResponse updatePrinterDetails(PrinterRequest request);
	PrinterResponse doInactivePrinterByprinterID(PrinterRequest requestDto);
	List<String> getPrinterNamesList(String userId, Long branchCode, String printerName);
	List<PrinterType> getAllPrinterTypes(Long branchCode);
	PrinterResponse updateRedirectPrinter(RedirectPrinterRequest requestDto);
	PrinterResponse removeRedirectionPrinter(RedirectPrinterRequest requestDto);
	List<PrinterResponse> getListOfPrinterManagement(Long branchCode);
	List<PrinterResponse> filterPrinters(FilterPrinterRequest request);
}
