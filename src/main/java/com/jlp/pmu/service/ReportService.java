package com.jlp.pmu.service;

import java.util.List;

import com.jlp.pmu.dto.ReportRequest;
import com.jlp.pmu.dto.ReportResponse;
public interface ReportService {

	ReportResponse addReport(ReportRequest request);

	ReportResponse updateReport(ReportRequest request);

	ReportResponse inactiveReport(ReportRequest request);

	List<ReportResponse> getAllReports(Long branchCode);

}
