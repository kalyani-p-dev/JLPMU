package com.jlp.pmu.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jlp.pmu.dto.FilterPrinterRequest;
import com.jlp.pmu.dto.HomePagePrintersResponse;
import com.jlp.pmu.dto.HomeTestPrintRequest;
import com.jlp.pmu.dto.JobDetailsListRequest;
import com.jlp.pmu.dto.JobDetailsResponse;
import com.jlp.pmu.dto.PrinterJobResponse;
import com.jlp.pmu.dto.TransactionResponse;
import com.jlp.pmu.enums.JobTypeInFilter;
import com.jlp.pmu.enums.PrinterJobType;
import com.jlp.pmu.enums.PrinterType;
import com.jlp.pmu.enums.StatusColor;
import com.jlp.pmu.enums.StatusType;
import com.jlp.pmu.exception.UserNotFoundException;
import com.jlp.pmu.models.PrintTransaction;
import com.jlp.pmu.models.Printer;
import com.jlp.pmu.models.PrinterJob;
import com.jlp.pmu.repository.ActivityRepository;
import com.jlp.pmu.repository.HomeRepository;
import com.jlp.pmu.repository.PrintTransactionRepository;
import com.jlp.pmu.repository.PrinterJobRepository;
import com.jlp.pmu.repository.PrinterRepository;
import com.jlp.pmu.repository.UserRepository;
import com.jlp.pmu.service.HomeService;
import com.jlp.pmu.utility.PMUUtility;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class HomeServiceImpl implements HomeService {
	
	@Autowired
	HomeRepository homeRepository;
	
	@Autowired
	PrinterRepository printerRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ActivityRepository activityRepository;
	
	@Autowired
	PrintTransactionRepository transactionRepository;
	
	@Autowired
	PrinterJobRepository jobRepository;
	
	@Autowired
	ModelMapper modelMapper;
	
	
	@Override
	public void printtestpageDetails(HomeTestPrintRequest requestDto) {
		String printerName =requestDto.getHometestPrint().getPrinterName();
		
		System.out.println("Printer Name: " + printerName);
		
		
	}


	@Override
	public List<JobDetailsResponse> getListOfJobDetailsOfPrinter(JobDetailsListRequest request) {

		userRepository.findById(request.getUserId()).orElseThrow(() -> new UserNotFoundException(
				"The User was not found with given ID: " + request.getUserId(), "USER_NOT_FOUND"));
		
		List<JobDetailsResponse> response = new ArrayList<JobDetailsResponse>();
		
		List<PrintTransaction> transactions = transactionRepository.findByBranchCodeAndReportNameIn(request.getBranchCode(), request.getPrinterNames());
		
		if( !transactions.isEmpty() ) {
			
			Set<Long> printerJobIds = transactions.stream().map(trans -> trans.getPrinterJob()).collect(Collectors.toSet());
			List<PrinterJob> printJobs = jobRepository.findByPrintJobIdIn(printerJobIds);
			Map<Long,PrinterJob> printerJobsMap = new HashMap<Long, PrinterJob>();
			
			if( !printJobs.isEmpty() ) {
				
				printJobs.stream().forEach(job -> printerJobsMap.put(job.getPrintJobId(), job));
				transactions.stream().forEach(transaction -> {
					
					JobDetailsResponse detailsResponse = new JobDetailsResponse();
					//detailsResponse.setPrinterTransaction(modelMapper.map(transaction, TransactionResponse.class));
					
					PrinterJob printJob = printerJobsMap.get(transaction.getPrinterJob());
					//detailsResponse.setPrinterJob(modelMapper.map(printJob, PrinterJobResponse.class));
					
					response.add(detailsResponse);
				});
			}
		}
		
		return response;
	}
	
	


	@Override
	public List<HomePagePrintersResponse> getHomePagePrinters(Long branchCode) {
		List<Printer> printers = printerRepository.findByStatusAndBranchCodeOrderbydate(true, branchCode);
		List<HomePagePrintersResponse> printersResponse = this.doPrinterTransactionsCaluculations(printers, branchCode);
		return printersResponse;
	}
	
	@Override
	public List<HomePagePrintersResponse> filterPrinters(FilterPrinterRequest request) {
		
		List<HomePagePrintersResponse> printerResponses = new ArrayList<>();
		
		List<Printer> printersFilteredByRepReportNames = new ArrayList<>();
		List<Printer> printersFilteredByGenReportNames = new ArrayList<>();
		
		List<Printer> printers = printerRepository.findByStatusAndBranchCodeOrderbydate(true, request.getBranchCode());

		if( !request.getIsDataToBeFiltered()) {
			
			if(printers != null && !printers.isEmpty()) {
				printerResponses = this.doPrinterTransactionsCaluculations(printers, request.getBranchCode());
			}
			
			return printerResponses;
		}
		
	// filter by Printer Types
		if(request.getPrinterTypes() != null && !request.getPrinterTypes().isEmpty()) {
			
			List<PrinterType> printerTypes = request.getPrinterTypes();
			printers = printers.stream().filter(printer -> printerTypes.contains(printer.getPrinterType())).toList();
		
			if(printers == null || printers.isEmpty()) {
				return new ArrayList<>();
			}
		}
		
	// filter by stationaries	
		
		if (request.getStationeries() != null && !request.getStationeries().isEmpty()) {

			List<String> stationaries = request.getStationeries();

			printers = printers.stream()
					.filter(printer -> stationaries.contains(printer.getStationary())).toList();

			if (printers == null || printers.isEmpty()) {
				return new ArrayList<>();
			}

		}
		

	// filter by report names
		
		if( request.getRepReportNames() != null && !request.getRepReportNames().isEmpty()) {
			printersFilteredByRepReportNames =  printerRepository.filterByReportNamesAndReportType(request.getRepReportNames(), true, request.getBranchCode());
			Set<Long> printerIds = printersFilteredByRepReportNames.stream().map(printer -> printer.getPrinterID()).collect(Collectors.toSet());
			printersFilteredByRepReportNames = printers.stream().filter(printer -> printerIds.contains(printer.getPrinterID())).toList();
		}
		
		if( request.getGenReportNames() != null && !request.getGenReportNames().isEmpty()) {
			printersFilteredByGenReportNames =  printerRepository.filterByReportNamesAndReportType(request.getGenReportNames(), true, request.getBranchCode());
			Set<Long> printerIds = printersFilteredByGenReportNames.stream().map(printer -> printer.getPrinterID()).collect(Collectors.toSet());
			printersFilteredByGenReportNames = printers.stream().filter(printer -> printerIds.contains(printer.getPrinterID())).toList();
		}
		
		if ((request.getRepReportNames() != null && !request.getRepReportNames().isEmpty())
				|| (request.getGenReportNames() != null && !request.getGenReportNames().isEmpty())) {

			printers = Stream.of(printersFilteredByRepReportNames, printersFilteredByGenReportNames)
					.flatMap(Collection::stream).collect(Collectors.toList());

			if (printers == null || printers.isEmpty()) {
				return new ArrayList<>();
			}
		}

		// filter by job types

		if (request.getPrinterJobTypes() != null && !request.getPrinterJobTypes().isEmpty()) {

			List<Printer> unprintedTransactionsPrinters = new ArrayList<>();
			List<Printer> printedTransactionsPrinters = new ArrayList<>();
			List<Printer> holdTransactionsPrinters = new ArrayList<>();

			if (request.getPrinterJobTypes().contains(JobTypeInFilter.HOLD)) {
				holdTransactionsPrinters = printerRepository.fetchHoldTransactionsPrinters(request.getBranchCode(),	true, PrinterJobType.HOLD.toString());
				Set<Long> printerIds = holdTransactionsPrinters.stream().map(printer -> printer.getPrinterID()).collect(Collectors.toSet());
				holdTransactionsPrinters = printers.stream().filter(printer -> printerIds.contains(printer.getPrinterID())).toList();
			}

			if (request.getPrinterJobTypes().contains(JobTypeInFilter.UNPRINTED)) {
				unprintedTransactionsPrinters = printerRepository.fetchPrintedOrUnPrintedTransactionsPrinters(request.getBranchCode(), true, PrinterJobType.PRINT.toString(), StatusType.TODO.toString());
				Set<Long> printerIds = unprintedTransactionsPrinters.stream().map(printer -> printer.getPrinterID()).collect(Collectors.toSet());
				unprintedTransactionsPrinters = printers.stream().filter(printer -> printerIds.contains(printer.getPrinterID())).toList();
			}

			if (request.getPrinterJobTypes().contains(JobTypeInFilter.PRINTED)) {
				printedTransactionsPrinters = printerRepository.fetchPrintedOrUnPrintedTransactionsPrinters(request.getBranchCode(), true, PrinterJobType.PRINT.toString(), StatusType.DONE.toString());
				Set<Long> printerIds = printedTransactionsPrinters.stream().map(printer -> printer.getPrinterID()).collect(Collectors.toSet());
				printedTransactionsPrinters = printers.stream().filter(printer -> printerIds.contains(printer.getPrinterID())).toList();
			}

			Set<Printer> filteredPrinters = Stream
					.of(holdTransactionsPrinters, unprintedTransactionsPrinters, printedTransactionsPrinters)
					.flatMap(Collection::stream).collect(Collectors.toSet());

			printers = filteredPrinters.stream().collect(Collectors.toList());

			if (printers == null || printers.isEmpty()) {
				return new ArrayList<>();
			}
		}
		
		if(printers != null && !printers.isEmpty()) {
			printerResponses = this.doPrinterTransactionsCaluculations(printers, request.getBranchCode());
		}
		
		return printerResponses;
	}
	
	@Override
	public List<TransactionResponse> searchJobDetails(Long branchCode, String serachKey) {
		
		List<TransactionResponse> response = new ArrayList<>();
		
		if(branchCode != 0 && serachKey != null) {
			
			String serachKeyValue = serachKey.replace(" ", "");
			List<PrintTransaction> transactions = transactionRepository.searchJobDetails(branchCode, serachKeyValue);
			
			if(transactions != null && !transactions.isEmpty()) {
				
				transactions.stream().forEach(transaction -> {
					TransactionResponse  transactionResponse = modelMapper.map(transaction, TransactionResponse.class);
					
					if(transaction.getTime() != null) {
						transactionResponse.setFormattedTime(PMUUtility.convertToTimeStamp(transaction.getTime()));
					}
					
					PrinterJobType type=transaction.getType();
					StatusType status=transaction.getStatus();
					if("PRINT".equals(type.toString()) && "DONE".equals(status.toString())) {
						transactionResponse.setStatusColor(StatusColor.GREEN);
					}else if("PRINT".equals(type.toString()) && "TODO".equals(status.toString())) {
						transactionResponse.setStatusColor(StatusColor.ORANGE);
					}else if("HOLD".equals(type.toString()) && "TODO".equals(status.toString())) {
						transactionResponse.setStatusColor(StatusColor.YELLOW);
					}
					
					response.add(transactionResponse);
				});
				
				
//				Set<Long> printerJobIds = transactions.stream().map(trans -> trans.getPrinterJob())
//						  .collect(Collectors.toSet());
//
//				List<PrinterJob> printJobs = jobRepository.findByPrintJobIdIn(printerJobIds);	
//				Map<Long, PrinterJob> printerJobsMap = new HashMap<Long, PrinterJob>();
//
//				if (!printJobs.isEmpty()) {
//
//					printJobs.stream().forEach(job -> printerJobsMap.put(job.getPrintJobId(), job));
//
//					transactions.stream().forEach(transaction -> {
//
//						JobDetailsResponse detailsResponse = new JobDetailsResponse();
//						detailsResponse.setPrinterTransaction(modelMapper.map(transaction, TransactionResponse.class));
//
//						PrinterJob printJob = printerJobsMap.get(transaction.getPrinterJob());
//						detailsResponse.setPrinterJob(modelMapper.map(printJob, PrinterJobResponse.class));
//
//						jobDetails.add(detailsResponse);
//					});
			}
		
		}
		
		return response;
	}
	
	private List<HomePagePrintersResponse> doPrinterTransactionsCaluculations(List<Printer> printers, Long branchCode){
		
		List<HomePagePrintersResponse> printersResponses = new ArrayList<>();
		
		List<String> printerNames = printers.stream().map(printer -> printer.getPrinterName()).collect(Collectors.toSet()).stream().collect(Collectors.toList());
		LocalDate currentDate = LocalDate.now();
		LocalDateTime startDateTime = LocalDateTime.of(currentDate, LocalTime.of(0, 0));
	    LocalDateTime endDateTime = LocalDateTime.of(currentDate, LocalTime.of(12, 0));
	    
	    List<PrintTransaction> transactions = transactionRepository.findBystartTimeBetweenAndBranchCodeAndPrinterNameIn(startDateTime, endDateTime, branchCode, printerNames);
		
		printerNames.stream().forEach(printerName ->{
			
			HomePagePrintersResponse printerResponse = new HomePagePrintersResponse();
			
			Printer printer = printers.stream().filter( printer1 -> printer1.getPrinterName().equals(printerName) && printer1.getBranchCode() == branchCode).findFirst().get();
			
			List<PrintTransaction> totalDoneJobs = new ArrayList<>();
			List<PrintTransaction> printToDoJobs = new ArrayList<>();
			List<PrintTransaction> holdTodoJobs= new ArrayList<>();
			
			
			List<PrintTransaction> transactionsForThisPrinter = transactions.stream().filter( transaction -> transaction.getPrinterName().equals(printerName) && transaction.getBranchCode() == branchCode).toList();
					
			if(transactionsForThisPrinter != null && !transactionsForThisPrinter.isEmpty()) {
				
				printerResponse.setTotalTodo(transactionsForThisPrinter.size());
				
				transactionsForThisPrinter.stream().forEach(transaction -> {
					
					if(transaction.getType().equals(PrinterJobType.PRINT) && transaction.getStatus().equals(StatusType.DONE)) {
						totalDoneJobs.add(transaction);
					}
					
					if(transaction.getType().equals(PrinterJobType.PRINT) && transaction.getStatus().equals(StatusType.TODO)) {
						printToDoJobs.add(transaction);
					}
					
					if(transaction.getType().equals(PrinterJobType.HOLD) && transaction.getStatus().equals(StatusType.TODO)) {
						holdTodoJobs.add(transaction);
					}
					
				});
			}
			
			printerResponse.setPrinterID(printer.getPrinterID());
			printerResponse.setPrinterName(printer.getPrinterName());
			printerResponse.setPrinterType(printer.getPrinterType());
			printerResponse.setTotalDone(totalDoneJobs.size());
			printerResponse.setPrintToDo(printToDoJobs.size());
			printerResponse.setHoldTodo(holdTodoJobs.size());
			
			if(transactionsForThisPrinter.size() == totalDoneJobs.size()) {
				printerResponse.setStatus(StatusColor.GREEN);
			}
			else if (totalDoneJobs.size() > 0 && printToDoJobs.size() == 0 && holdTodoJobs.size() >= 0) {
				printerResponse.setStatus(StatusColor.YELLOW);
			}
			else if (totalDoneJobs.size() > 0 && printToDoJobs.size() >= 0 && holdTodoJobs.size() >= 0) {
				printerResponse.setStatus(StatusColor.ORANGE);
			}
			else {
				printerResponse.setStatus(StatusColor.RED);
			}
			
			printersResponses.add(printerResponse);
			
		});
		
		return printersResponses;
	}


}