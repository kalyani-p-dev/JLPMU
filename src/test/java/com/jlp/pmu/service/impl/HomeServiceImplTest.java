package com.jlp.pmu.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.jlp.pmu.dto.FilterPrinterRequest;
import com.jlp.pmu.dto.HomePagePrintersResponse;
import com.jlp.pmu.dto.HomeTestPrintRequest;
import com.jlp.pmu.enums.JobTypeInFilter;
import com.jlp.pmu.enums.PrinterJobType;
import com.jlp.pmu.enums.PrinterType;
import com.jlp.pmu.enums.StatusColor;
import com.jlp.pmu.enums.StatusType;
import com.jlp.pmu.models.PrintTransaction;
import com.jlp.pmu.models.Printer;
import com.jlp.pmu.repository.ActivityRepository;
import com.jlp.pmu.repository.HomeRepository;
import com.jlp.pmu.repository.PrintTransactionRepository;
import com.jlp.pmu.repository.PrinterJobRepository;
import com.jlp.pmu.repository.PrinterRepository;
import com.jlp.pmu.repository.UserRepository;
import com.jlp.pmu.service.HomeService;

@ExtendWith(MockitoExtension.class)
class HomeServiceImplTest {

	@Mock
	HomeRepository homeRepository;
	
	@Mock
	PrinterRepository printerRepository;
	
	@Mock
	UserRepository userRepository;
	
	@Mock
	ActivityRepository activityRepository;
	
	@Mock
	PrintTransactionRepository transactionRepository;
	
	@Mock
	PrinterJobRepository jobRepository;
	
	@Mock
	ModelMapper modelMapper;
	
	@InjectMocks
	HomeService homeService = new HomeServiceImpl();
	
	FilterPrinterRequest filterPrinterRequest = new FilterPrinterRequest();
	Printer printer = new Printer();
	Printer printer2 = new Printer();
	PrintTransaction transaction = new PrintTransaction();
	PrintTransaction transaction2 = new PrintTransaction();
	
	@BeforeEach
	public void setup() {
		
		//mocking filterPrinterRequest
		
		filterPrinterRequest.setBranchCode(4L);
		filterPrinterRequest.setIsDataToBeFiltered(false);
		
		// mocking printer objects
		
		printer.setBranchCode(4L);
		printer.setLocalAttachedPrinter(true);
		printer.setPmuPrinterName("pmu1");
		printer.setPmuServer("server1");
		printer.setPrinterID(1L);
		printer.setPrinterName("printer1");
		printer.setPrinterPATH("path1");
		printer.setPrinterType(PrinterType.LINE);
		printer.setStationary("stn1");
		printer.setStatus(true);
		
		printer2.setBranchCode(4L);
		printer2.setLocalAttachedPrinter(true);
		printer2.setPmuPrinterName("pmu2");
		printer2.setPmuServer("server2");
		printer2.setPrinterID(2L);
		printer2.setPrinterName("printer2");
		printer2.setPrinterPATH("path2");
		printer2.setPrinterType(PrinterType.ZEBRA);
		printer2.setStationary("stn2");
		printer2.setStatus(true);
		
		transaction.setTransactionId(1L);
		transaction.setBranchCode(4L);
		transaction.setPrinterName("printer2");
		transaction.setStatus(StatusType.DONE);
		transaction.setType(PrinterJobType.PRINT);
		transaction.setStationary("stn3");
		
		transaction2.setTransactionId(2L);
		transaction2.setBranchCode(4L);
		transaction2.setPrinterName("printer2");
		transaction2.setStatus(StatusType.DONE);
		transaction2.setType(PrinterJobType.PRINT);
		transaction2.setStationary("stn2");
		
	}
	
	@Test
	public void printer_grid_in_home_filter_printer_api_success_test_status_green() {
		
		when(printerRepository.findByStatusAndBranchCodeOrderbydate(anyBoolean(), anyLong())).thenReturn(List.of(printer, printer2));
		when(transactionRepository.findByBranchCodeAndPrinterNameIn(anyLong(), anyList())).thenReturn(List.of(transaction, transaction2));
		
		List<HomePagePrintersResponse> response = homeService.filterPrinters(filterPrinterRequest);
		
		assertThat(response.size()).isEqualTo(2);
	}
	
	@Test
	public void printer_grid_in_home_filter_printer_api_success_test_status_yellow() {
		
		transaction2.setType(PrinterJobType.HOLD);
		transaction2.setStatus(StatusType.TODO);
		
		when(printerRepository.findByStatusAndBranchCodeOrderbydate(anyBoolean(), anyLong())).thenReturn(List.of(printer, printer2));
		when(transactionRepository.findByBranchCodeAndPrinterNameIn(anyLong(), anyList())).thenReturn(List.of(transaction, transaction2));
		
		List<HomePagePrintersResponse> response = homeService.filterPrinters(filterPrinterRequest);
		
		assertThat(response.size()).isEqualTo(2);
		assertThat(response.get(0).getStatus()).isEqualTo(StatusColor.YELLOW);
	}
	
	@Test
	public void printer_grid_in_home_filter_printer_api_success_test_status_orange() {
		
		transaction2.setType(PrinterJobType.PRINT);
		transaction2.setStatus(StatusType.TODO);
		
		when(printerRepository.findByStatusAndBranchCodeOrderbydate(anyBoolean(), anyLong())).thenReturn(List.of(printer, printer2));
		when(transactionRepository.findByBranchCodeAndPrinterNameIn(anyLong(), anyList())).thenReturn(List.of(transaction, transaction2));
		
		List<HomePagePrintersResponse> response = homeService.filterPrinters(filterPrinterRequest);
		
		assertThat(response.size()).isEqualTo(2);
		assertThat(response.get(0).getStatus()).isEqualTo(StatusColor.ORANGE);
	}
	
	@Test
	public void printer_grid_in_home_filter_printer_api_success_test_status_red() {
		
		transaction.setType(PrinterJobType.HOLD);
		transaction.setStatus(StatusType.TODO);
		
		transaction2.setType(PrinterJobType.PRINT);
		transaction2.setStatus(StatusType.TODO);
		
		when(printerRepository.findByStatusAndBranchCodeOrderbydate(anyBoolean(), anyLong())).thenReturn(List.of(printer, printer2));
		when(transactionRepository.findByBranchCodeAndPrinterNameIn(anyLong(), anyList())).thenReturn(List.of(transaction, transaction2));
		
		List<HomePagePrintersResponse> response = homeService.filterPrinters(filterPrinterRequest);
		
		assertThat(response.size()).isEqualTo(2);
		assertThat(response.get(0).getStatus()).isEqualTo(StatusColor.RED);
	}
	
	@Test
	public void filterPrinters_api_success_test() {
		
		Printer printer3 = new Printer();
		Printer printer4 = new Printer();
		Printer printer5 = new Printer();
		PrintTransaction transaction3 = new PrintTransaction();
		
		printer3.setBranchCode(4L);
		printer3.setLocalAttachedPrinter(true);
		printer3.setPmuPrinterName("pmu3");
		printer3.setPmuServer("server3");
		printer3.setPrinterID(3L);
		printer3.setPrinterName("printer3");
		printer3.setPrinterPATH("path3");
		printer3.setPrinterType(PrinterType.LINE);
		printer3.setStationary("stn3");
		printer3.setStatus(true);
		
		printer4.setBranchCode(4L);
		printer4.setLocalAttachedPrinter(true);
		printer4.setPmuPrinterName("pmu4");
		printer4.setPmuServer("server4");
		printer4.setPrinterID(4L);
		printer4.setPrinterName("printer4");
		printer4.setPrinterPATH("path4");
		printer4.setPrinterType(PrinterType.LINE);
		printer4.setStationary("stn4");
		printer4.setStatus(true);
		
		printer5.setBranchCode(4L);
		printer5.setLocalAttachedPrinter(true);
		printer5.setPmuPrinterName("pmu5");
		printer5.setPmuServer("server5");
		printer5.setPrinterID(5L);
		printer5.setPrinterName("printer5");
		printer5.setPrinterPATH("path5");
		printer5.setPrinterType(PrinterType.LINE);
		printer5.setStationary("stn5");
		printer5.setStatus(true);
		
		transaction.setPrinterName("printer3");
		transaction2.setPrinterName("printer4");
		
		transaction3.setTransactionId(3L);
		transaction3.setBranchCode(4L);
		transaction3.setPrinterName("printer5");
		transaction3.setStatus(StatusType.DONE);
		transaction3.setType(PrinterJobType.PRINT);
		transaction3.setStationary("stn2");
		
		
		filterPrinterRequest.setIsDataToBeFiltered(true);
		filterPrinterRequest.setPrinterTypes(List.of(PrinterType.LINE));
		filterPrinterRequest.setPrinterJobTypes(List.of(JobTypeInFilter.HOLD, JobTypeInFilter.UNPRINTED, JobTypeInFilter.PRINTED));
		filterPrinterRequest.setStationeries(List.of("stn3", "stn4", "stn5"));
		filterPrinterRequest.setGenReportNames(List.of("report1"));
		filterPrinterRequest.setRepReportNames(List.of("report2"));
		
		when(printerRepository.findByStatusAndBranchCodeOrderbydate(anyBoolean(), anyLong())).thenReturn(List.of(printer, printer2, printer3, printer4, printer5));
		when(printerRepository.filterByReportNamesAndReportType(anyList(), any(Boolean.class), anyLong())).thenReturn(List.of(printer3,printer4)).thenReturn(List.of(printer5));
		when(printerRepository.fetchHoldTransactionsPrinters(anyLong(), anyBoolean(), anyString())).thenReturn(List.of(printer3));
		when(printerRepository.fetchPrintedOrUnPrintedTransactionsPrinters(anyLong(), anyBoolean(), anyString(), anyString())).thenReturn(List.of(printer4)).thenReturn(List.of(printer5));
		when(transactionRepository.findByBranchCodeAndPrinterNameIn(anyLong(), anyList())).thenReturn(List.of(transaction, transaction2, transaction3));
		
		List<HomePagePrintersResponse> response =  homeService.filterPrinters(filterPrinterRequest);
		
		assertThat(response.size()).isEqualTo(3);
		
	}
	
	@DisplayName(value = "mismatch printer types in filter and getting empty data")
	@Test
	public void filterPrinters_api_failure_test_1() {
		
		when(printerRepository.findByStatusAndBranchCodeOrderbydate(anyBoolean(), anyLong())).thenReturn(List.of(printer, printer2));
		
		filterPrinterRequest.setIsDataToBeFiltered(true);
		filterPrinterRequest.setPrinterTypes(List.of(PrinterType.LASER));
		
		List<HomePagePrintersResponse> response =  homeService.filterPrinters(filterPrinterRequest);
		
		assertThat(response.isEmpty()).isTrue();
	}
	
	@DisplayName(value = "mismatch stationaries in filter and getting empty data")
	@Test
	public void filterPrinters_api_failure_test_2() {
		
		when(printerRepository.findByStatusAndBranchCodeOrderbydate(anyBoolean(), anyLong())).thenReturn(List.of(printer, printer2));
		
		filterPrinterRequest.setIsDataToBeFiltered(true);
		filterPrinterRequest.setPrinterTypes(Collections.emptyList());
		filterPrinterRequest.setStationeries(List.of("stn3", "stn4", "stn5"));
		
		List<HomePagePrintersResponse> response =  homeService.filterPrinters(filterPrinterRequest);
		
		assertThat(response.isEmpty()).isTrue();
	}
	
	@DisplayName(value = "mismatch report names in filter and getting empty data")
	@Test
	public void filterPrinters_api_failure_test_3() {
		
		when(printerRepository.findByStatusAndBranchCodeOrderbydate(anyBoolean(), anyLong())).thenReturn(List.of(printer, printer2));
		when(printerRepository.filterByReportNamesAndReportType(anyList(), any(Boolean.class), anyLong())).thenReturn(Collections.emptyList());
		
		filterPrinterRequest.setIsDataToBeFiltered(true);
		filterPrinterRequest.setPrinterTypes(Collections.emptyList());
		filterPrinterRequest.setStationeries(Collections.emptyList());
		filterPrinterRequest.setGenReportNames(List.of("report1"));
		
		
		List<HomePagePrintersResponse> response =  homeService.filterPrinters(filterPrinterRequest);
		
		assertThat(response.isEmpty()).isTrue();
	}
	
	@DisplayName(value = "mismatch printer job types in filter and getting empty data")
	@Test
	public void filterPrinters_api_failure_test_4() {
		
		when(printerRepository.findByStatusAndBranchCodeOrderbydate(anyBoolean(), anyLong())).thenReturn(List.of(printer, printer2));
		when(printerRepository.fetchHoldTransactionsPrinters(anyLong(), anyBoolean(), anyString())).thenReturn(Collections.emptyList());
		
		filterPrinterRequest.setIsDataToBeFiltered(true);
		filterPrinterRequest.setPrinterTypes(Collections.emptyList());
		filterPrinterRequest.setStationeries(Collections.emptyList());
		filterPrinterRequest.setPrinterJobTypes(List.of(JobTypeInFilter.HOLD));
		
		
		
		List<HomePagePrintersResponse> response =  homeService.filterPrinters(filterPrinterRequest);
		
		assertThat(response.isEmpty()).isTrue();
	}
	@Test
    public void testPrintTestPageDetails_Success() {
        // Create a test object for HomeTestPrintRequest
        HomePagePrintersResponse hometestPrint = new HomePagePrintersResponse();
        hometestPrint.setPrinterName("PrinterName");
        //hometestPrint.setStationary("Stationary");

        HomeTestPrintRequest requestDto = new HomeTestPrintRequest();
        requestDto.setHometestPrint(hometestPrint);

        // Call the method
        homeService.printtestpageDetails(requestDto);

        
    }
}
