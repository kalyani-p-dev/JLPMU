package com.jlp.pmu.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.jlp.pmu.constant.Constant;
import com.jlp.pmu.dto.FilterPrinterRequest;
import com.jlp.pmu.dto.PrinterNamesListRequset;
import com.jlp.pmu.dto.PrinterRequest;
import com.jlp.pmu.dto.PrinterResponse;
import com.jlp.pmu.dto.RedirectPrinterRequest;
import com.jlp.pmu.dto.RedirectionPrinterRequest;
import com.jlp.pmu.dto.UserResponse;
import com.jlp.pmu.enums.JobTypeInFilter;
import com.jlp.pmu.enums.ObjectType;
import com.jlp.pmu.enums.PrinterJobType;
import com.jlp.pmu.enums.PrinterType;
import com.jlp.pmu.enums.StatusType;
import com.jlp.pmu.exception.PrinterAlreadyExistException;
import com.jlp.pmu.exception.PrinterNotFoundException;
import com.jlp.pmu.exception.UserNotFoundException;
import com.jlp.pmu.models.Activity;
import com.jlp.pmu.models.PrintTransaction;
import com.jlp.pmu.models.Printer;
import com.jlp.pmu.models.User;
import com.jlp.pmu.repository.ActivityRepository;
import com.jlp.pmu.repository.BranchRepository;
import com.jlp.pmu.repository.PrinterRepository;
import com.jlp.pmu.repository.UserRepository;
import com.jlp.pmu.service.PrinterService;

import lombok.extern.log4j.Log4j2;
@Service
@Log4j2
public class PrinterServiceImpl implements PrinterService {

	@Autowired
	PrinterRepository printerRepository;

	@Autowired
	ActivityRepository activityRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	BranchRepository branchRepository;
	
	@Autowired
	ModelMapper modelMapper;

	@Override
	public PrinterResponse addPrinterDetails(PrinterRequest requestDto) {

		User user = userRepository.findById(requestDto.getUserId()).orElseThrow(() -> new UserNotFoundException(
				Constant.USER_NOT_FOUND_MESSAGE + requestDto.getUserId(), Constant.USER_NOT_FOUND));
		if (printerRepository.existsByPrinterNameAndBranchCode(requestDto.getPrinterName(),
				requestDto.getBranchCode())) {
			throw new PrinterAlreadyExistException(Constant.PRINTER_IS_ALREADY_EXISTS_GIVEN_PRINTERNAME
					+ requestDto.getPrinterName() + Constant.AND_BRANCHCODE + requestDto.getBranchCode(),
					Constant.PRINTER_ALREADY_EXISTS);
		}


		Printer printer = new Printer();
		printer.setBranchCode(requestDto.getBranchCode());
		printer.setCreatedBy(user.getFirstName() + " " + user.getLastName());
		printer.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
		printer.setStatus(true);
		printer.setLastUpdatedTime(LocalDateTime.now());
		printer.setPrinterName(requestDto.getPrinterName());
		printer.setPmuPrinterName(requestDto.getPmuPrinterName());
		printer.setPrinterPATH(requestDto.getPrinterPATH());
		printer.setPrinterType(requestDto.getPrinterType());
		printer.setComments(requestDto.getComments());
		printer.setPmuServer(requestDto.getPmuServer());
		printer.setLocalAttachedPrinter(requestDto.getLocalAttachedPrinter());

		Printer printerResponse = printerRepository.save(printer);
		Activity activity = new Activity();

		activity.setCreatedBy(user.getFirstName() + " " + user.getLastName());
		activity.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
		activity.setLastupdatedtime(LocalDateTime.now());
		activity.setComment(Constant.ADDED_BY + user.getFirstName() + " " + user.getLastName());
		activity.setObjectType(ObjectType.PRINTER);
		activity.setObjectID(printerResponse.getPrinterID().toString());

		activityRepository.save(activity);

		log.info(Constant.PRINTER_DETAILS_ADDED_SUCCESSFULLY, printerResponse.getPrinterID());

		return modelMapper.map(printerResponse, PrinterResponse.class) ;
	}


	@Override
	public PrinterResponse updatePrinterDetails(PrinterRequest request) {
		User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new PrinterNotFoundException(
				Constant.USER_NOT_FOUND_MESSAGE + request.getUserId(), Constant.USER_NOT_FOUND));

		Printer printer = printerRepository.findById(request.getPrinterID()).orElseThrow(
				() -> new PrinterNotFoundException(Constant.THE_PRINTER_WAS_NOT_FOUND_WITH_GIVEN_ID + request.getPrinterID(),
						Constant.PRINTER_NOT_FOUND));

		printer.setBranchCode(request.getBranchCode());
		printer.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
		printer.setLastUpdatedTime(LocalDateTime.now());
		printer.setPrinterName(request.getPrinterName());
		printer.setPmuPrinterName(request.getPmuPrinterName());
		printer.setPrinterPATH(request.getPrinterPATH());
		printer.setPrinterType(request.getPrinterType());
		printer.setComments(request.getComments());
		printer.setPmuServer(request.getPmuServer());
		printer.setLocalAttachedPrinter(request.getLocalAttachedPrinter());

		Printer printerResponse = printerRepository.save(printer);

		Activity activity = new Activity();

		activity.setCreatedBy(printer.getCreatedBy());
		activity.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
		activity.setLastupdatedtime(LocalDateTime.now());
		activity.setComment(Constant.ADDED_BY + printer.getCreatedBy() + Constant.AND_UPDATED_BY + user.getFirstName() + " "
				+ user.getLastName());
		activity.setObjectType(ObjectType.PRINTER);
		activity.setObjectID(printerResponse.getPrinterID().toString());

		activityRepository.save(activity);
		log.info(Constant.PRINTER_DETAILS_UPDATED_SUCCESSFULLY, request.getUserId());

		PrinterResponse response = modelMapper.map(printerResponse, PrinterResponse.class);
		return response;

	}

	@Override
	public PrinterResponse doInactivePrinterByprinterID(PrinterRequest request) {

		User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserNotFoundException(
				Constant.USER_NOT_FOUND_MESSAGE + request.getUserId(), Constant.USER_NOT_FOUND));

		log.info(Constant.INACTIVATING_PRINTER_DETAILS_BY_PMUPRINTERNAME, request.getPmuPrinterName());

		Printer printer = printerRepository.findById(request.getPrinterID())
				.orElseThrow(() -> new PrinterNotFoundException(
						Constant.THE_PRINTER_WITH_GIVEN_CODE + request.getPrinterID() + Constant.WAS_NOT_FOUND,
						Constant.PRINTER_NOT_FOUND));

		String comment = Constant.ADDED_BY + printer.getCreatedBy() + Constant.AND_INACTIVATED_BY + user.getFirstName() + " "
				+ user.getLastName();

		printer.setStatus(false);

		printer.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
		printer.setLastUpdatedTime(LocalDateTime.now());
		printer.setComments(comment);

		printerRepository.save(printer);

		Activity activity = new Activity();
		activity.setCreatedBy(printer.getCreatedBy());
		activity.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
		activity.setLastupdatedtime(LocalDateTime.now());
		activity.setComment(comment);
		activity.setObjectType(ObjectType.PRINTER);
		activity.setObjectID(request.getPrinterID().toString());

		activityRepository.save(activity);

		log.info(Constant.PRINTER_INACTIVATED_SUCCESSFULLY_WITH_PMUPRINTERNAME, request.getPmuPrinterName());
		PrinterResponse response = modelMapper.map(printer, PrinterResponse.class);
		return response;
	}

	@Override
	public List<String> getPrinterNamesList(String userId, Long branchCode, String printerName) {

		 userRepository.findById(userId).orElseThrow(
				() -> new UserNotFoundException(Constant.USER_NOT_FOUND_WITH_ID + userId, Constant.USER_NOT_FOUND));

		PrinterType printType = printerRepository.findPrinterTypeByPrinterNameAndBranchCode(printerName,
				branchCode);
		List<String> listOfPrinterName= printerRepository.findByCombinedPrintTypeAndBranchCode(true,printType, branchCode);
		listOfPrinterName.remove(printerName);
		return listOfPrinterName;
	}
	
	@Override
	public List<PrinterType> getAllPrinterTypes(Long branchCode) {
		return printerRepository.findAllPrinterTypes(branchCode);
	}

	@Override
	public PrinterResponse updateRedirectPrinter(RedirectPrinterRequest requestDto) {
		 userRepository.findById(requestDto.getUserId()).orElseThrow(
				() -> new UserNotFoundException(Constant.USER_NOT_FOUND_WITH_ID + requestDto.getUserId(), Constant.USER_NOT_FOUND));
		
		Printer printer = printerRepository.findByBranchCodeAndPrinterName(requestDto.getBranchCode(), requestDto.getPrinterName())
				.orElseThrow(() -> new PrinterNotFoundException(
						Constant.THE_PRINTER_WITH_GIVEN_PRINTER_NAME + requestDto.getPrinterName() + Constant.AND_BRANCHCODE+ requestDto.getBranchCode() +Constant.WAS_NOT_FOUND,
						Constant.PRINTER_NOT_FOUND));
		if (printer != null) {
            
            printer.setRedirectPrintName(requestDto.getRedirectPrintName());
            printer.setRedirectExist(true);
           
            printerRepository.save(printer);
        
            PrinterResponse response = modelMapper.map(printer, PrinterResponse.class);
    		return response;
		}
		return null;
        
}

	@Override
	public PrinterResponse removeRedirectionPrinter(RedirectPrinterRequest requestDto) {
		
		userRepository.findById(requestDto.getUserId()).orElseThrow(
				() -> new UserNotFoundException(Constant.USER_NOT_FOUND_WITH_ID+ requestDto.getUserId(), Constant.USER_NOT_FOUND));
		
		Printer printer = printerRepository.findByBranchCodeAndPrinterName(requestDto.getBranchCode(), requestDto.getPrinterName())
				.orElseThrow(() -> new PrinterNotFoundException(
						Constant.THE_PRINTER_WITH_GIVEN_PRINTER_NAME + requestDto.getPrinterName() + Constant.AND_BRANCHCODE+ requestDto.getBranchCode() +Constant.WAS_NOT_FOUND,
						Constant.PRINTER_NOT_FOUND));
		if (printer != null) {
            
            
			printer.setRedirectPrintName(null);
            printer.setRedirectExist(false);
           
            printerRepository.save(printer);
        
            PrinterResponse response = modelMapper.map(printer, PrinterResponse.class);
    		return response;
		}
		
		return null;
	}

	@Override
	public List<PrinterResponse> getListOfPrinterManagement(Long branchCode) {
		List<Printer> printers = printerRepository.findByStatusAndBranchCodeOrderbydate(true,branchCode);
        return mapPrintersToResponses(printers);
    }

    private List<PrinterResponse> mapPrintersToResponses(List<Printer> printers) {
        return printers.stream()
        		       .sorted((printer1, printer2) -> printer2.getLastUpdatedTime().compareTo(printer1.getLastUpdatedTime()))
                       .map(printer -> modelMapper.map(printer, PrinterResponse.class))
                       .collect(Collectors.toList());
    }
    
    

	@Override
	public List<PrinterResponse> filterPrinters(FilterPrinterRequest request) {
		
		List<Printer> printersFilteredByRepReportNames = new ArrayList<>();
		List<Printer> printersFilteredByGenReportNames = new ArrayList<>();
		
		List<Printer> printers = printerRepository.findByStatusAndBranchCodeOrderbydate(true, request.getBranchCode());

		if( !request.getIsDataToBeFiltered() ) {
			if (printers != null && !printers.isEmpty()) {
				return mapPrintersToResponses(printers);
			}
			
			return Collections.emptyList();
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

		return mapPrintersToResponses(printers);
	}


	public Optional<Printer> findById(long printerId) {
		 Optional<Printer> printerInfo = printerRepository.findById(printerId);
		return printerInfo;
	}
		
	
	
	
}
	
