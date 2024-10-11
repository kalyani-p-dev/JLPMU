package com.jlp.pmu.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jlp.pmu.constant.Constant;
import com.jlp.pmu.dto.FilterPrinterRequest;
import com.jlp.pmu.dto.JobDetailsResponse;
import com.jlp.pmu.dto.PrinterNamesListRequset;
import com.jlp.pmu.dto.PrinterRequest;
import com.jlp.pmu.dto.PrinterResponse;
import com.jlp.pmu.dto.RedirectPrinterRequest;
import com.jlp.pmu.dto.RedirectionPrinterRequest;
import com.jlp.pmu.dto.ReportResponse;
import com.jlp.pmu.enums.PrinterType;
import com.jlp.pmu.service.PrinterService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin("*")
@RequestMapping("/pmu/v1/api/printer")


public class PrinterController {
	@Autowired
	PrinterService printerService;
	
	@PostMapping("/add-printer")
	public ResponseEntity addPrinterDetails(@RequestBody PrinterRequest requestDto,HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
			PrinterResponse responseDto = printerService.addPrinterDetails(requestDto);
			return new ResponseEntity<PrinterResponse>(responseDto, HttpStatus.CREATED);
		} 	
		}
	
	@PutMapping("/update-printer")
	public ResponseEntity updatePrinterDetails(@RequestBody PrinterRequest requestDto,HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
			PrinterResponse responseDto = printerService.updatePrinterDetails(requestDto);
			return new ResponseEntity<PrinterResponse>(responseDto, HttpStatus.OK);
		} 
	}
	
	@PutMapping("/inactive")
	public ResponseEntity deletePrinterDetailsByprinterID(@RequestBody PrinterRequest requestDto,HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
			PrinterResponse responseDto = printerService.doInactivePrinterByprinterID(requestDto);
			return new ResponseEntity<PrinterResponse>(responseDto, HttpStatus.OK);
		} 	
	}
	
	@GetMapping("/get-PrinterNameslist/user/{userId}/branch/{branchCode}/printer/{printerName}")
	public ResponseEntity getPrinterNamesList(@PathVariable String userId, @PathVariable Long branchCode, @PathVariable String printerName,HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
			List<String> printer = printerService.getPrinterNamesList(userId, branchCode, printerName);
			return new ResponseEntity<List<String>>(printer, HttpStatus.OK);
		}	
	}
	
	@GetMapping("/get-printer-types/branch/{branchCode}")
	public ResponseEntity getAllPrinterTypes(@PathVariable Long branchCode,HttpServletRequest httpRequest,
			HttpServletResponse httpResponse){
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
			List<PrinterType> responseList = printerService.getAllPrinterTypes(branchCode);
			return new ResponseEntity<List<PrinterType>>(responseList,HttpStatus.OK);
		}
		
	}
	
	@PutMapping("/update-RedirectPrinter")
	public ResponseEntity updateRedirectPrinter(@RequestBody RedirectPrinterRequest requestDto,HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
			PrinterResponse responseDto = printerService.updateRedirectPrinter(requestDto);
			return new ResponseEntity<PrinterResponse>(responseDto, HttpStatus.OK);
		}

		

	}
	
	
	@PutMapping("/remove-RedirectionPrinter")
	public ResponseEntity removeRedirectionPrinter(@RequestBody RedirectPrinterRequest requestDto,HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
			PrinterResponse responseDto = printerService.removeRedirectionPrinter(requestDto);
			return new ResponseEntity<PrinterResponse>(responseDto, HttpStatus.OK);
		}
	}
	
	@GetMapping("/get-ListOfPrinterManagement/branch/{branchCode}")
	public ResponseEntity getListOfPrinterManagement(@PathVariable Long branchCode,HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
			List<PrinterResponse> printer = printerService.getListOfPrinterManagement(branchCode);
			return new ResponseEntity<List<PrinterResponse>>(printer, HttpStatus.OK);
		}
		
		
	}
	
//	@GetMapping("/filter-printers")
//	public ResponseEntity<List<PrinterResponse>> filterPrinters(@RequestBody FilterPrinterRequest request){
//		List<PrinterResponse> printers = printerService.filterPrinters(request);
//		return new ResponseEntity<List<PrinterResponse>>(printers, HttpStatus.OK);
//	}
	
	@PostMapping("/filter-printers")
	public ResponseEntity filterPrinters(@RequestBody FilterPrinterRequest requestDto,HttpServletRequest httpRequest,
			HttpServletResponse httpResponse){
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
			List<PrinterResponse> printers = printerService.filterPrinters(requestDto);
			return new ResponseEntity<List<PrinterResponse>>(printers, HttpStatus.OK);
		}		
	}
}
