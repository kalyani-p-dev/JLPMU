package com.jlp.pmu.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.jlp.pmu.dto.BranchRequest;
import com.jlp.pmu.dto.PrinterResponse;
import com.jlp.pmu.dto.ReportRequest;
import com.jlp.pmu.dto.ReportResponse;
import com.jlp.pmu.enums.PrinterType;
import com.jlp.pmu.exception.ReportAlreadyExistsException;
import com.jlp.pmu.exception.ReportNotFoundException;
import com.jlp.pmu.exception.UserNotFoundException;
import com.jlp.pmu.models.Activity;
import com.jlp.pmu.models.Printer;
import com.jlp.pmu.models.Report;
import com.jlp.pmu.models.User;
import com.jlp.pmu.repository.ActivityRepository;
import com.jlp.pmu.repository.PrinterRepository;
import com.jlp.pmu.repository.ReportRepository;
import com.jlp.pmu.repository.UserRepository;
import com.jlp.pmu.service.ReportService;

@ExtendWith(MockitoExtension.class)
class ReportSeviceImplTest {

	@Mock
	UserRepository userRepository;

	@Mock
	ActivityRepository activityRepository;

	@Mock
	ReportRepository reportRepository;

	@Mock
	PrinterRepository printerRepository;

	@Mock
	ModelMapper modelMapper;
	
	@InjectMocks
	ReportService reportService = new ReportSeviceImpl();
	
	private ReportRequest reportRequest = new ReportRequest();
	private ReportResponse reportResponse = new ReportResponse();
	private Printer printer = new Printer();
	private PrinterResponse printerResponse = new PrinterResponse();
	private Report report = new Report();
	private User user = new User();
	
	@BeforeEach
	void setup() {
		
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
		
		//mocking report object
		
		report.setBranchCode(6L);
		report.setComments("added by John");
		report.setCopies(2);
		report.setDept("dept1");
		report.setPrinterId(3L);
		report.setPrintjoboptions(true);
		report.setReportId(5L);
		report.setReportName("CDSPRINTMAN");
		report.setSubDept("sub1");
		
		
		//mocking user object
		
		user.setFirstName("David");
		user.setLastName("Warner");
		user.setUserId("J12345");
		user.setEmailAddress("david@gmail.com");
		
		//mocking printer object
		
		printer.setBranchCode(4L);
		printer.setLocalAttachedPrinter(true);
		printer.setPmuPrinterName("pmu1");
		printer.setPmuServer("server1");
		printer.setPrinterID(3L);
		printer.setPrinterName("printer1");
		printer.setPrinterPATH("path1");
		printer.setPrinterType(PrinterType.LASER);
		printer.setStationary("stn1");
		printer.setStatus(true);
		
		//mocking printer response object
		
		printerResponse.setLocalAttachedPrinter(true);
		printerResponse.setPmuPrinterName("pmu1");
		printerResponse.setPmuServer("server1");
		printerResponse.setPrinterID(3L);
		printerResponse.setPrinterName("printer1");
		printerResponse.setPrinterPATH("path1");
		printerResponse.setPrinterType(PrinterType.LASER);
		printerResponse.setComments("added by John");
	}
	
	@Test
	public void addReport_success_test() {
		
		when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
		when(reportRepository.findReportByCombinations(anyString(), anyLong(), anyString(), anyString())).thenReturn(Optional.empty());
		when(reportRepository.save(any(Report.class))).thenReturn(report);
		when(modelMapper.map(report, ReportResponse.class)).thenReturn(reportResponse);
		
		ReportResponse response = reportService.addReport(reportRequest);
		
		assertNotNull(response);
		
		verify(userRepository, times(1)).findById(anyString());
		verify(reportRepository, times(1)).findReportByCombinations(anyString(), anyLong(), anyString(), anyString());
		verify(reportRepository, times(1)).save(any(Report.class));
		verify(activityRepository, times(1)).save(any(Activity.class));
		verify(modelMapper, times(1)).map(report, ReportResponse.class);
		
	}

	@Test
	public void addReport_UserNotFoundException_test() {
		
		when(userRepository.findById(anyString())).thenReturn(Optional.empty());
		
		UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> reportService.addReport(reportRequest));
		
		assertEquals("USER_NOT_FOUND", exception.getErrorCode());
		assertEquals("The User was not found with given ID: "+ reportRequest.getUserId(), exception.getMessage());
		
		verify(userRepository, times(1)).findById(anyString());
		verify(reportRepository, never()).findReportByCombinations(anyString(), anyLong(), anyString(), anyString());
		
	}
	
	@Test
	public void addReport_ReportAlreadyExistsException_test() {
		
		when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
		when(reportRepository.findReportByCombinations(anyString(), anyLong(), anyString(), anyString())).thenReturn(Optional.of(report));
		
		ReportAlreadyExistsException exception = assertThrows(ReportAlreadyExistsException.class, () -> reportService.addReport(reportRequest));
		
		assertEquals("REPORT_ALREADY_EXISTS", exception.getErrorCode());
		assertEquals("The Report with given combinations is alreday exists", exception.getMessage());
		
		verify(userRepository, times(1)).findById(anyString());
		verify(reportRepository, times(1)).findReportByCombinations(anyString(), anyLong(), anyString(), anyString());
		verify(reportRepository, never()).save(any(Report.class));
		
	}
	
	@Test
	public void updateReport_success_test() {
		
		when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
		when(reportRepository.findById(anyLong())).thenReturn(Optional.of(report));
		when(reportRepository.save(any(Report.class))).thenReturn(report);
		when(modelMapper.map(report, ReportResponse.class)).thenReturn(reportResponse);
		
		ReportResponse response = reportService.updateReport(reportRequest);
		
		assertNotNull(response);
		
		verify(userRepository, times(1)).findById(anyString());
		verify(reportRepository, times(1)).findById(anyLong());
		verify(reportRepository, times(1)).save(any(Report.class));
		verify(activityRepository, times(1)).save(any(Activity.class));
		verify(modelMapper, times(1)).map(report, ReportResponse.class);
		
	}
	
	@Test
	public void updateReport_UserNotFoundException_test() {
		
		when(userRepository.findById(anyString())).thenReturn(Optional.empty());
		
		UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> reportService.updateReport(reportRequest));
		
		assertEquals("USER_NOT_FOUND", exception.getErrorCode());
		assertEquals("The User was not found with given ID: "+ reportRequest.getUserId(), exception.getMessage());
		
		verify(userRepository, times(1)).findById(anyString());
		verify(reportRepository, never()).findById(anyLong());
		
	}
	
	@Test
	public void updateReport_ReportNotFoundException_test() {
		
		when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
		when(reportRepository.findById(anyLong())).thenReturn(Optional.empty());
		
		ReportNotFoundException exception = assertThrows(ReportNotFoundException.class, () -> reportService.updateReport(reportRequest));
		
		assertEquals("REPORT_NOT_FOUND", exception.getErrorCode());
		assertEquals("The Report was not found with given ID: " + reportRequest.getReportId(), exception.getMessage());
		
		verify(userRepository, times(1)).findById(anyString());
		verify(reportRepository, times(1)).findById(anyLong());
		verify(reportRepository, never()).save(any(Report.class));
		
	}
	
	@Test
	public void inactive_report_success_test() {
		
		when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
		when(reportRepository.findById(anyLong())).thenReturn(Optional.of(report));
		when(reportRepository.save(any(Report.class))).thenReturn(report);
		when(modelMapper.map(report, ReportResponse.class)).thenReturn(reportResponse);
		
		ReportResponse response = reportService.inactiveReport(reportRequest);
		
		assertNotNull(response);
		
		verify(userRepository, times(1)).findById(anyString());
		verify(reportRepository, times(1)).findById(anyLong());
		verify(reportRepository, times(1)).save(any(Report.class));
		verify(activityRepository, times(1)).save(any(Activity.class));
		verify(modelMapper, times(1)).map(report, ReportResponse.class);
		
	}
	
	@Test
	public void inactive_report_UserNotFoundException_test() {
		
		when(userRepository.findById(anyString())).thenReturn(Optional.empty());
		
		UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> reportService.inactiveReport(reportRequest));
		
		assertEquals("USER_NOT_FOUND", exception.getErrorCode());
		assertEquals("The User was not found with given ID: "+ reportRequest.getUserId(), exception.getMessage());
		
		verify(userRepository, times(1)).findById(anyString());
		verify(reportRepository, never()).findById(anyLong());
		
	}
	
	
	@Test
	public void inactive_report_ReportNotFoundException_test() {
		
		when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
		when(reportRepository.findById(anyLong())).thenReturn(Optional.empty());
		
		ReportNotFoundException exception = assertThrows(ReportNotFoundException.class, () -> reportService.inactiveReport(reportRequest));
		
		assertEquals("REPORT_NOT_FOUND", exception.getErrorCode());
		assertEquals("The Report was not found with given ID: " + reportRequest.getReportId(), exception.getMessage());
		
		verify(userRepository, times(1)).findById(anyString());
		verify(reportRepository, times(1)).findById(anyLong());
		verify(reportRepository, never()).save(any(Report.class));
		
	}
	
	@Test
	public void getAllReportByBranchCodeAndReportType_test_1() {
		
		when(printerRepository.findByPrinterIDIn(anySet())).thenReturn(List.of(printer));
		when(modelMapper.map(report, ReportResponse.class)).thenReturn(reportResponse);
		when(modelMapper.map(anyString(), any())).thenReturn(printer.getPmuPrinterName());
		when(modelMapper.map(printer, PrinterResponse.class)).thenReturn(printerResponse);
		
		List<ReportResponse> response = reportService.getAllReports(reportRequest.getBranchCode());
		
		assertNotNull(response);
		assertThat(response.size()).isEqualTo(1);
		
		verify(printerRepository, times(1)).findByPrinterIDIn(anySet());
		verify(modelMapper, times(1)).map(report, ReportResponse.class);
		verify(modelMapper, times(1)).map(printer, PrinterResponse.class);
		
	}
	

	
	@Test
	public void getAllReportByBranchCode_test_1() {
		
		when(reportRepository.findByStatusAndBranchCodeOrderbyDate(anyBoolean(), anyLong())).thenReturn(List.of(report));
		when(printerRepository.findByPrinterIDIn(anySet())).thenReturn(List.of(printer));
		when(modelMapper.map(printer, PrinterResponse.class)).thenReturn(printerResponse);
		when(modelMapper.map(anyString(), any())).thenReturn(printer.getPmuPrinterName());
		when(modelMapper.map(report, ReportResponse.class)).thenReturn(reportResponse);
		
		List<ReportResponse> response = reportService.getAllReports(reportRequest.getBranchCode());
		
		assertNotNull(response);
		assertThat(response.size()).isEqualTo(1);
		
		verify(reportRepository, times(1)).findByStatusAndBranchCodeOrderbyDate(anyBoolean(), anyLong());
		verify(printerRepository, times(1)).findByPrinterIDIn(anySet());
		verify(modelMapper, times(1)).map(report, ReportResponse.class);
		verify(modelMapper, times(1)).map(printer, PrinterResponse.class);
		
	}
	
	@Test
	public void getAllReportByBranchCode_test_2() {
		
		when(reportRepository.findByStatusAndBranchCodeOrderbyDate(anyBoolean(), anyLong())).thenReturn(List.of(report));
		when(printerRepository.findByPrinterIDIn(anySet())).thenReturn(Collections.emptyList());
		when(modelMapper.map(report, ReportResponse.class)).thenReturn(reportResponse);
		
		List<ReportResponse> response = reportService.getAllReports(reportRequest.getBranchCode());
		
		assertNotNull(response);
		assertThat(response.size()).isEqualTo(1);
		
		verify(reportRepository, times(1)).findByStatusAndBranchCodeOrderbyDate(anyBoolean(), anyLong());;
		verify(printerRepository, times(1)).findByPrinterIDIn(anySet());
		verify(modelMapper, times(1)).map(report, ReportResponse.class);
		verify(modelMapper, never()).map(printer, PrinterResponse.class);
		
	}
	

	@Test
	public void getAllReportByBranchCode_test_3() {
		
		when(reportRepository.findByStatusAndBranchCodeOrderbyDate(anyBoolean(), anyLong())).thenReturn(Collections.emptyList());
		
		List<ReportResponse> response = reportService.getAllReports(reportRequest.getBranchCode());
		
		assertNotNull(response);
		assertTrue(response.isEmpty());
		
		verify(reportRepository, times(1)).findByStatusAndBranchCodeOrderbyDate(anyBoolean(), anyLong());
		verify(modelMapper, never()).map(report, ReportResponse.class);
		
	}
	
}

