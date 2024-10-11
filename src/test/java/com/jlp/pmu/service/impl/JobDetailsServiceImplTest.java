package com.jlp.pmu.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.jlp.pmu.dto.FilterJobDetailsRequest;
import com.jlp.pmu.dto.JobDetailsResponse;
import com.jlp.pmu.dto.PrintJobDetailsRequest;
import com.jlp.pmu.dto.TransactionResponse;
import com.jlp.pmu.enums.PrinterJobType;
import com.jlp.pmu.enums.StatusType;
import com.jlp.pmu.models.PrintTransaction;
import com.jlp.pmu.models.PrinterJob;
import com.jlp.pmu.repository.PrintTransactionRepository;
import com.jlp.pmu.repository.PrinterJobRepository;
import com.jlp.pmu.repository.UserRepository;
import com.jlp.pmu.service.JobDetailsService;

@ExtendWith(MockitoExtension.class)
class JobDetailsServiceImplTest {

	@Mock
	UserRepository userRepository;
	
	@Mock
	PrintTransactionRepository transactionRepository;
	
	@Mock
	PrinterJobRepository jobRepository;
	
	@Mock
	ModelMapper modelMapper;
	
	@InjectMocks
	JobDetailsService jobDetailsService = new JobDetailsServiceImpl();
	
	PrinterJob printerJob = new PrinterJob();
	PrinterJob printerJob2 = new PrinterJob();
	
	PrintTransaction printTransaction = new PrintTransaction();
	PrintTransaction printTransaction2 = new PrintTransaction();
	
	FilterJobDetailsRequest filterJobDetailsRequest = new FilterJobDetailsRequest();
	ModelMapper modelMapper2 = new ModelMapper();
	
	@BeforeEach
	public void setUp() {
		
		// mocking printer job objects
		printerJob.setPrintJobId(1L);
		printerJob.setPrinterId(4L);
		printerJob.setHold(true);
		printerJob.setNoOfCopies(3);
		
		printerJob2.setPrintJobId(2L);
		printerJob2.setPrinterId(3L);
		printerJob2.setHold(true);
		printerJob2.setNoOfCopies(4);
		
		//mocking print transactions object
		
		printTransaction.setTransactionId(1L);
		printTransaction.setPrinterJob(1L);
		printTransaction.setBranchCode(4L);
		printTransaction.setCustomerName("Harry");
		printTransaction.setPrinterName("printer1");
		printTransaction.setReportName("report1");
		printTransaction.setDeptCode("dept1");
		printTransaction.setType(PrinterJobType.PRINT);
		printTransaction.setStatus(StatusType.DONE);
		printTransaction.setDefaultTitle("title1");
		printTransaction.setGen("gen1");
		printTransaction.setRef("ref1");
		printTransaction.setPdfPath("path\\abc.pdf");
		printTransaction.setTime(LocalDateTime.now());
		
		printTransaction2.setTransactionId(2L);
		printTransaction2.setPrinterJob(2L);
		printTransaction2.setBranchCode(4L);
		printTransaction2.setCustomerName("Kane");
		printTransaction2.setPrinterName("printer2");
		printTransaction2.setReportName("report2");
		printTransaction2.setDeptCode("dept2");
		printTransaction2.setType(PrinterJobType.PRINT);
		printTransaction2.setStatus(StatusType.DONE);
		printTransaction2.setDefaultTitle("title2");
		printTransaction2.setGen("gen2");
		printTransaction2.setRef("ref2");
		printTransaction2.setPdfPath("path\\xyz.pdf");
		printTransaction2.setTime(LocalDateTime.now());
		
	}
	
	@Test
	public void fetch_transactions_in_getAll_or_filter_jobDetails() {
		
		filterJobDetailsRequest.setBranchCode(4L);
		filterJobDetailsRequest.setIsDataToBeFiltered(false);
		filterJobDetailsRequest.setPrinterNames(List.of("printer", "printer2"));
		
		when(transactionRepository.findByBranchCodeAndPrinterNameIn(anyLong(), anyList())).thenReturn(List.of(printTransaction, printTransaction2));
		when(jobRepository.findByPrintJobIdIn(anySet())).thenReturn(List.of(printerJob, printerJob2));
		when(modelMapper.map(any(), any())).thenAnswer( invocation -> modelMapper2.map(invocation.getArgument(0), invocation.getArgument(1)));
		
		JobDetailsResponse jobDetails = jobDetailsService.getAll_or_filter_jobDetails(filterJobDetailsRequest);
		
		assertNotNull(jobDetails);
		//assertThat(jobDetails.size()).isEqualTo(2);
	}
	
	@DisplayName("Passing empty printer names in the request resulting empty job details response List")
	@Test
	public void fetch_transactions_in_getAll_or_filter_jobDetails_2() {
		
		filterJobDetailsRequest.setBranchCode(4L);
		filterJobDetailsRequest.setIsDataToBeFiltered(false);
		
		
		//List<JobDetailsResponse> jobDetails = jobDetailsService.getAll_or_filter_jobDetails(filterJobDetailsRequest);
		JobDetailsResponse jobDetails = jobDetailsService.getAll_or_filter_jobDetails(filterJobDetailsRequest);
		
		assertNotNull(jobDetails);
		//assertTrue(jobDetails.isEmpty());
	}
	
	@Test
	public void filter_transactions_in_getAll_or_filter_jobDetails() {
		
		filterJobDetailsRequest.setBranchCode(4L);
		filterJobDetailsRequest.setIsDataToBeFiltered(false);
		filterJobDetailsRequest.setPrinterNames(List.of("printer", "printer2"));
		
		when(transactionRepository.findByBranchCodeAndPrinterNameIn(anyLong(), anyList())).thenReturn(List.of(printTransaction, printTransaction2));
		when(jobRepository.findByPrintJobIdIn(anySet())).thenReturn(List.of(printerJob, printerJob2));
		when(modelMapper.map(any(), any())).thenAnswer( invocation -> modelMapper2.map(invocation.getArgument(0), invocation.getArgument(1)));
		
		//List<JobDetailsResponse> jobDetails = jobDetailsService.getAll_or_filter_jobDetails(filterJobDetailsRequest);
		JobDetailsResponse jobDetails = jobDetailsService.getAll_or_filter_jobDetails(filterJobDetailsRequest);
		
		assertNotNull(jobDetails);
		//assertThat(jobDetails.size()).isEqualTo(2);
	}
	
	@Test
    public void testPrintJobDetails_PrinterNameUsed() {
        // Creating the request object
		 List <TransactionResponse> printerTransactions = new ArrayList<TransactionResponse>();
        TransactionResponse printerTransaction = new TransactionResponse();
        printerTransaction.setPrinterName("PrinterName");
        printerTransactions.add(printerTransaction);
        
        JobDetailsResponse printerJobDetails = new JobDetailsResponse();
        printerJobDetails.setPrinterTransaction(printerTransactions);

        PrintJobDetailsRequest requestDto = new PrintJobDetailsRequest();
        requestDto.setPrinterTransaction(printerTransaction);
        requestDto.setPrintToAlternatePrinter(false);

        // Redirecting System.out to capture the prints
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // Calling the method
        jobDetailsService.printJobDetails(requestDto);

        // Restoring the original System.out
        System.setOut(originalOut);

        // Verifying the output
        String expectedOutput = " Printer Name: PrinterName\n";
        assertEquals(expectedOutput.trim(), outContent.toString().trim());
    }

    @Test
    public void testPrintJobDetails_AlternatePrinterNameUsed() {
        // Creating the request object
        TransactionResponse printerTransaction = new TransactionResponse();


        printerTransaction.setPrinterName("OriginalPrinterName");

        JobDetailsResponse printerJobDetails = new JobDetailsResponse();
      //  printerJobDetails.setPrinterTransaction(printerTransaction);

        PrintJobDetailsRequest requestDto = new PrintJobDetailsRequest();
       // requestDto.setPrinterJobDetails(printerJobDetails);
        requestDto.setPrintToAlternatePrinter(true);
        requestDto.setAlternaterPrinterName("AlternatePrinterName");

        // Redirecting System.out to capture the prints
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // Calling the method
        jobDetailsService.printJobDetails(requestDto);

        // Restoring the original System.out
        System.setOut(originalOut);

        // Verifying the output
        String expectedOutput = " Printer Name: AlternatePrinterName\n";
        assertEquals(expectedOutput.trim(), outContent.toString().trim());
    }
}

