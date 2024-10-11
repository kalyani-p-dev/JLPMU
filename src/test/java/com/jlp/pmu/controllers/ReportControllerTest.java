package com.jlp.pmu.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.Charset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlp.pmu.dto.PrinterResponse;
import com.jlp.pmu.dto.ReportRequest;
import com.jlp.pmu.dto.ReportResponse;
import com.jlp.pmu.exception.ReportAlreadyExistsException;
import com.jlp.pmu.exception.ReportNotFoundException;
import com.jlp.pmu.exception.handler.ReportServiceExceptionHandler;
import com.jlp.pmu.exception.handler.UserServiceExceptionController;
import com.jlp.pmu.service.ReportService;

@WebMvcTest
@ContextConfiguration(classes = {ReportController.class, ReportServiceExceptionHandler.class, UserServiceExceptionController.class})
class ReportControllerTest {

	@MockBean
	private ReportService reportService;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;
	private ReportRequest reportRequest = new ReportRequest();
	private ReportResponse reportResponse = new ReportResponse();
	
	@BeforeEach
	void setUp() {
		
		//Init MockMvc Object and build
		 mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		 
		//mocking report request Object
		
				reportRequest.setBranchCode(6L);
				reportRequest.setComments("added by John");
				reportRequest.setCopies(2);
				reportRequest.setDept("dept1");
				reportRequest.setPrinterId(3L);
				reportRequest.setPrintjoboptions(true);
				reportRequest.setReportId(5L);
				reportRequest.setReportName("CDSPRINTMAN");
				reportRequest.setSubDept("sub1");
				reportRequest.setUserId("J12345");
				
				//mocking report response object
				
				reportResponse.setBranchCode(6L);
				reportResponse.setComments("added by John");
				reportResponse.setCopies(2);
				reportResponse.setDept("dept1");
				reportResponse.setPrintjoboptions(true);
				reportResponse.setReportId(5L);
				reportResponse.setReportName("CDSPRINTMAN");
				reportResponse.setSubDept("sub1");
				reportResponse.setPrinter(new PrinterResponse());
	}

	@Test
	public void addReport_success_test() throws Exception {
		
		when(reportService.addReport(any(ReportRequest.class))).thenReturn(reportResponse);

		ResultActions response = mockMvc.perform(post("/pmu/v1/api/report/add-report")
				.characterEncoding(Charset.defaultCharset()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reportRequest)));

		response.andExpect(status().isCreated()).andDo(print())
				.andExpect(jsonPath("$.reportName", is(reportResponse.getReportName())))
				.andExpect(jsonPath("$.dept", is(reportResponse.getDept())));
	}
	
	@Test
	public void addReport_ReportAlreadyExistsException_test() throws Exception {
		
		when(reportService.addReport(any(ReportRequest.class))).thenThrow(new ReportAlreadyExistsException("The Report with given combinations is alreday exists", "REPORT_ALREADY_EXISTS"));

		ResultActions response = mockMvc.perform(post("/pmu/v1/api/report/add-report")
				.characterEncoding(Charset.defaultCharset()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reportRequest)));

		response.andExpect(status().isAlreadyReported()).andDo(print())
				.andExpect(jsonPath("$.message", is("The Report with given combinations is alreday exists")))
				.andExpect(jsonPath("$.errorCode", is("REPORT_ALREADY_EXISTS")));
	}
	
	@Test
	public void updateReport_success_test() throws Exception{
		
		when(reportService.updateReport(any(ReportRequest.class))).thenReturn(reportResponse);

		ResultActions response = mockMvc.perform(put("/pmu/v1/api/report/update-report")
				.characterEncoding(Charset.defaultCharset()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reportRequest)));

		response.andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$.reportName", is(reportResponse.getReportName())))
				.andExpect(jsonPath("$.dept", is(reportResponse.getDept())));
	}
	
	@Test
	public void updateReport_ReportNotFoundException_test() throws Exception {
		
		when(reportService.updateReport(any(ReportRequest.class))).thenThrow(new ReportNotFoundException(
				"The Report was not found with given ID: " + reportRequest.getReportId(), "REPORT_NOT_FOUND"));

		ResultActions response = mockMvc.perform(put("/pmu/v1/api/report/update-report")
				.characterEncoding(Charset.defaultCharset()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reportRequest)));

		response.andExpect(status().isNotFound()).andDo(print())
				.andExpect(jsonPath("$.message", is("The Report was not found with given ID: " + reportRequest.getReportId())))
				.andExpect(jsonPath("$.errorCode", is("REPORT_NOT_FOUND")));
	}
	
	@Test
	public void inactive_report_success_test() throws Exception {
		
		when(reportService.inactiveReport(any(ReportRequest.class))).thenReturn(reportResponse);

		ResultActions response = mockMvc.perform(put("/pmu/v1/api/report/inactive-report")
				.characterEncoding(Charset.defaultCharset()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reportRequest)));

		response.andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$.reportName", is(reportResponse.getReportName())))
				.andExpect(jsonPath("$.dept", is(reportResponse.getDept())));
	}
	
	
	@Test
	public void getAllReportsByBranchCode_test() throws Exception {
		
		when(reportService.getAllReports(anyLong())).thenReturn(List.of(reportResponse));

		ResultActions response = mockMvc.perform(
													get("/pmu/v1/api/report/get-all-reports/branch/{branchCode}", reportRequest.getBranchCode())
												);

		response.andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$.size()", is(1)))
				.andExpect(jsonPath("$[0].reportName", is(reportResponse.getReportName()) ));
				
	}
	
}
