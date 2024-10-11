package com.jlp.pmu.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jlp.pmu.dto.FilterJobDetailsRequest;
import com.jlp.pmu.dto.JobDetailsListRequest;
import com.jlp.pmu.dto.JobDetailsResponse;
import com.jlp.pmu.dto.PrintJobDetailsRequest;
import com.jlp.pmu.dto.PrintSelectedPagesRequest;
import com.jlp.pmu.dto.TransactionResponse;
import com.jlp.pmu.enums.PrinterJobType;
import com.jlp.pmu.enums.StatusColor;
import com.jlp.pmu.enums.StatusType;
import com.jlp.pmu.exception.UserNotFoundException;
import com.jlp.pmu.models.PrintTransaction;
import com.jlp.pmu.models.PrinterJob;
import com.jlp.pmu.pojo.ReportMetaData;
import com.jlp.pmu.repository.PrintTransactionRepository;
import com.jlp.pmu.repository.PrinterJobRepository;
import com.jlp.pmu.repository.UserRepository;
import com.jlp.pmu.service.JobDetailsService;
import com.jlp.pmu.utility.PMUUtility;

@Service
public class JobDetailsServiceImpl implements JobDetailsService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PrintTransactionRepository transactionRepository;
	
	@Autowired
	PrinterJobRepository jobRepository;
	
	@Autowired
	ModelMapper modelMapper;
	
	private int printed;

	@Override
	public List<JobDetailsResponse> getListOfJobDetails(JobDetailsListRequest request) {
		
		
		
		userRepository.findById(request.getUserId()).orElseThrow(() -> new UserNotFoundException(
				"The User was not found with given ID: " + request.getUserId(), "USER_NOT_FOUND"));
		
		List<JobDetailsResponse> response = new ArrayList<JobDetailsResponse>();
		
		List<PrintTransaction> transactions = new ArrayList<PrintTransaction>();
		
		if( request.getReportNames() != null && !request.getReportNames().isEmpty() ) {
			transactions = transactionRepository.findByBranchCodeAndReportNameIn(request.getBranchCode(), request.getReportNames());
		}
		
		else if( request.getPrinterNames() != null && !request.getPrinterNames().isEmpty() ){
			transactions = transactionRepository.findByBranchCodeAndPrinterNameIn(request.getBranchCode(), request.getPrinterNames());
		}
		
		
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
					
					//detailsResponse.setTime(PMUUtility.convertToTimeStamp(transaction.getTime()));
					
					if(transaction.getPdfPath() != null && !transaction.getPdfPath().isBlank() && StringUtils.containsIgnoreCase(transaction.getPdfPath(), ".pdf")) {
						String[] array = transaction.getPdfPath().split("\\\\");
						if(array.length > 0) {
							//detailsResponse.setPdfName(array[array.length-1]);
						}
					}
					response.add(detailsResponse);
				});
			}
		}
		
		return response;
	}

	@Override
	public String printSelectedPages(PrintSelectedPagesRequest request) {
		
		String reportName = request.getPrinterTransaction().getReportName();
		String printerName = request.getPrinterTransaction().getPrinterName();
		Long branchCode = request.getPrinterTransaction().getBranchCode();
		
		int startRange = 1;
		int endRange = request.getPrinterTransaction().getPages();
		
		if( !request.getPrintsAllPages() ) {
			startRange = request.getStartRange();
			endRange = request.getEndRange();
		}
		
		return null;
	}

	@Override
	public String reprocessJob(List<TransactionResponse> transactionResponse) {
		transactionResponse.stream().forEach(transaction -> {
			String reportName = transaction.getReportName();
			String printerName = transaction.getPrinterName();
			Long branchCode = transaction.getBranchCode();
		});
		return null;
	}
	
	public void printJobDetails(PrintJobDetailsRequest requestDto) {
		
		String printerName = requestDto.getPrinterTransaction().getPrinterName();
        if(requestDto.getPrintToAlternatePrinter()) {
        	printerName = requestDto.getAlternaterPrinterName();
        }
		//JobDetailsResponse printerJobDetails = requestDto.getPrinterJobDetails();
        //String userId = printerJobDetails.getUserId();
        //System.out.println("Received the details for user ID: " + userId);
        System.out.println(" Printer Name: " + printerName);
        
		
	}

	@Override
	public JobDetailsResponse getAll_or_filter_jobDetails(FilterJobDetailsRequest request) {
		
		JobDetailsResponse jobDetails = new JobDetailsResponse();
		List<PrintTransaction> transactions = new ArrayList<>();
		System.out.println(request.getIsDataToBeFiltered());
		
		if(request.getPrinterNames() == null || request.getPrinterNames().isEmpty()) 
			return jobDetails;
		
		
		transactions = transactionRepository.findByBranchCodeAndPrinterNameIn(request.getBranchCode(), request.getPrinterNames());
		
		if(!request.getIsDataToBeFiltered()) {
			return convertToJobDetailsResponse(transactions);
		}
// filter by printer types
		if( request.getPrinterTypes() != null && !request.getPrinterTypes().isEmpty()) {
			
			List<String> printerTypes = request.getPrinterTypes().stream().map(type -> type.toString()).toList();
			System.out.println("printerTypes:"+printerTypes);
			System.out.println("printerName:"+request.getPrinterNames());
			transactions =  transactionRepository.filterByPrinterTypes(printerTypes, request.getPrinterNames(), request.getBranchCode());
			System.out.println("transactions:"+transactions);
			if(transactions == null || transactions.isEmpty()) {
				return jobDetails;
			}
		}			
		
// converting into jobDetails DTO
		
		if (!transactions.isEmpty()) {
			
			jobDetails = convertToJobDetailsResponse(transactions);
			
		}
					
		return jobDetails;
	}

	@Override
	public String storeTransactionVariables(ReportMetaData metaData) {
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		PrintTransaction transaction = mapper.convertValue(metaData.getTransactionVariableList(), PrintTransaction.class);
		
		PrinterJob job = new PrinterJob();
		
		job.setNoOfCopies(metaData.getNoOfCopies());
		job.setJobStatus(metaData.getPrinterJobStatus());
		job.setJobStartTime(metaData.getStartTime());
		job.setJobEndTime(metaData.getEndTime());
		job.setPrinterId(metaData.getPrinterId());
		job.setHold(metaData.isHold());
		jobRepository.save(job);
		
	
		transaction.setPrinterJob(job.getPrintJobId());

		transactionRepository.save(transaction);
		
		return "Transaction Variables stored successfully";
	}
	
	
	
	private JobDetailsResponse convertToJobDetailsResponse(List<PrintTransaction> transactions) {
		
		JobDetailsResponse jobDetails = new JobDetailsResponse();
		List<TransactionResponse> printerTransaction =new ArrayList<>();
		List<TransactionResponse> printedTransaction =new ArrayList<>();
			transactions.stream().forEach(transaction -> {
				TransactionResponse transactionResponse	= modelMapper.map(transaction, TransactionResponse.class);
				if (transactionResponse.getEndTime() != null) {
					transactionResponse.setFormattedTime(PMUUtility.convertToTimeStamp(transactionResponse.getEndTime()));
				}else {
					transactionResponse.setFormattedTime(PMUUtility.convertToTimeStamp(transactionResponse.getStartTime()));
				}
				/*
				 * if(transaction.getPdfPath() != null && !transaction.getPdfPath().isBlank() &&
				 * StringUtils.containsIgnoreCase(transaction.getPdfPath(), ".pdf")) { String[]
				 * array = transaction.getPdfPath().split("\\\\"); if(array.length > 0) {
				 * detailsResponse.setPdfName(array[array.length-1]); } }
				 */
				PrinterJobType type=transaction.getType();
				StatusType status=transaction.getStatus();
				if("PRINT".equals(type.toString()) && "DONE".equals(status.toString())) {
					transactionResponse.setStatusColor(StatusColor.GREEN);
					printedTransaction.add(transactionResponse);
					printed++;
				}else if("PRINT".equals(type.toString()) && "TODO".equals(status.toString())) {
					transactionResponse.setStatusColor(StatusColor.ORANGE);
				}else if("HOLD".equals(type.toString()) && "TODO".equals(status.toString())) {
					transactionResponse.setStatusColor(StatusColor.YELLOW);
				}
				System.out.println("transactionResponse:"+transactionResponse);
				printerTransaction.add(transactionResponse);
			});
			jobDetails.setPrinterTransaction(printerTransaction);
			jobDetails.setPrinted(printedTransaction.size());
			jobDetails.setUnprinted(transactions.size()-printedTransaction.size());
			
			System.out.println("jobDetails:"+jobDetails);
		
		return jobDetails;
	}
	
	
//	@Override
//	public List<JobDetailsResponse> filterTransactions(FilterPrinterRequest request) {
//		
//		List<JobDetailsResponse> jobDetails = new ArrayList<>();
//		List<PrintTransaction> transactions = new ArrayList<>();
//		
//		List<PrintTransaction> transactionsFilteredByRepReportNames = new ArrayList<>();
//		
//		transactions = transactionRepository.findByBranchCode(request.getBranchCode());
//		
//		if( request.getPrinterJobTypes() != null && !request.getPrinterJobTypes().isEmpty()) {
//			transactions = transactionRepository.findByBranchCodeAndTypeIn(request.getBranchCode(), request.getPrinterJobTypes());
//			
//			if(transactions == null || transactions.isEmpty()) {
//				return jobDetails;
//			}
//		}
//		
//		
//		if( request.getStationeries() != null && !request.getStationeries().isEmpty()) {
//			
////			List<PrintTransaction> transactionsFilteredByStationeries = transactionRepository.findByBranchCodeAndStationaryIn(request.getBranchCode(), request.getStationeries());
////			
////			if(transactionsFilteredByStationeries == null || transactionsFilteredByStationeries.isEmpty()) {
////				return jobDetails;
////			}
////			
////			Set<Long> transactionIds = transactionsFilteredByStationeries.stream().map(transaction -> transaction.getTransactionId()).collect(Collectors.toSet());
////			transactions = transactions.stream().filter(transaction -> transactionIds.contains(transaction.getTransactionId())).toList();
//			
//			List<String> stationaries = request.getStationeries();
//			transactions = transactions.stream().filter(transaction -> stationaries.contains(transaction.getStationary())).toList();
//			if(transactions == null || transactions.isEmpty()) {
//				return jobDetails;
//			}
//		}
//		
//		
//		if( request.getPrinterTypes() != null && !request.getPrinterTypes().isEmpty()) {
//			
////			List<Long> list = Arrays.asList(1L,2L);
////			
////			String printerTypes = this.convertIntoStringQueryParameter(list);
////			String printerTypes = StringUtils.replaceEach(request.getPrinterTypes().toString(), new String[] {"[","]"}, new String[] {"(",")"});
//			
//			List<String> printerTypes = request.getPrinterTypes().stream().map(type -> type.toString()).toList();
//			List<PrintTransaction> transactionsFilteredByPrinterTypes =  transactionRepository.filterByPrinterTypes(printerTypes);
//			
//			if(transactionsFilteredByPrinterTypes == null || transactionsFilteredByPrinterTypes.isEmpty()) {
//				return jobDetails;
//			}
//			
//			Set<Long> transactionIds = transactionsFilteredByPrinterTypes.stream().map(transaction -> transaction.getTransactionId()).collect(Collectors.toSet());
//			transactions = transactions.stream().filter(transaction -> transactionIds.contains(transaction.getTransactionId())).toList();
//			
//			if(transactions == null || transactions.isEmpty()) {
//				return jobDetails;
//			}
//		}
//		
//		
//		if( request.getRepReportNames() != null && !request.getRepReportNames().isEmpty()) {
//			transactionsFilteredByRepReportNames =  transactionRepository.filterByReportNamesAndReportType(request.getRepReportNames(), ReportType.REP.toString());
//			Set<Long> transactionIds = transactionsFilteredByRepReportNames.stream().map(transaction -> transaction.getTransactionId()).collect(Collectors.toSet());
//			transactions = transactions.stream().filter(transaction -> transactionIds.contains(transaction.getTransactionId())).toList();
//		}
//		
//		if( request.getGenReportNames() != null && !request.getGenReportNames().isEmpty()) {
//			transactionsFilteredByRepReportNames =  transactionRepository.filterByReportNamesAndReportType(request.getRepReportNames(), ReportType.REP.toString());
//			Set<Long> transactionIds = transactionsFilteredByRepReportNames.stream().map(transaction -> transaction.getTransactionId()).collect(Collectors.toSet());
//			transactions = transactions.stream().filter(transaction -> transactionIds.contains(transaction.getTransactionId())).toList();
//		}
//
//		
//		if (!transactions.isEmpty()) {
//
//			Set<Long> printerJobIds = transactions.stream().map(trans -> trans.getPrinterJob())
//												  .collect(Collectors.toSet());
//			
//			List<PrinterJob> printJobs = jobRepository.findByPrintJobIdIn(printerJobIds);
//			Map<Long, PrinterJob> printerJobsMap = new HashMap<Long, PrinterJob>();
//
//			if (!printJobs.isEmpty()) {
//
//				printJobs.stream().forEach(job -> printerJobsMap.put(job.getPrintJobId(), job));
//				
//				transactions.stream().forEach(transaction -> {
//
//					JobDetailsResponse detailsResponse = new JobDetailsResponse();
//					detailsResponse.setPrinterTransaction(modelMapper.map(transaction, TransactionResponse.class));
//
//					PrinterJob printJob = printerJobsMap.get(transaction.getPrinterJob());
//					detailsResponse.setPrinterJob(modelMapper.map(printJob, PrinterJobResponse.class));
//
//					jobDetails.add(detailsResponse);
//				});
//			}
//		}
//		
//		return jobDetails;
//	} 

}
