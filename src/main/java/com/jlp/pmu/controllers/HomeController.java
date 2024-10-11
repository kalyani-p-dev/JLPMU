package com.jlp.pmu.controllers;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jlp.pmu.constant.Constant;
import com.jlp.pmu.dataingestion.EbcdicToAscii;
import com.jlp.pmu.dto.FilterPrinterRequest;
import com.jlp.pmu.dto.HomePagePrintersResponse;
import com.jlp.pmu.dto.JobDetailsListRequest;
import com.jlp.pmu.dto.JobDetailsResponse;
import com.jlp.pmu.dto.PrinterRequest;
import com.jlp.pmu.dto.SearchJobDetailsRequest;
import com.jlp.pmu.dto.TransactionResponse;
import com.jlp.pmu.service.HomeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import com.jlp.pmu.dto.HomeTestPrintRequest;
import com.jlp.pmu.dto.PrintJobDetailsRequest;


@RestController
@CrossOrigin("*")
@RequestMapping("/pmu/v1/api/home")
public class HomeController {

	@Autowired
	HomeService homeService;
	
	@GetMapping("/show-job-details")
	public ResponseEntity getListOfJobDetailsOfPrinter(@RequestBody JobDetailsListRequest requestDto,HttpServletRequest httpRequest,
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
			List<JobDetailsResponse> response = homeService.getListOfJobDetailsOfPrinter(requestDto);
			return new ResponseEntity<List<JobDetailsResponse>>(response, HttpStatus.OK);
		}
			
	}
	
	@GetMapping("/getPrinters/branch/{branchCode}")
	public ResponseEntity getHomePagePrinters(@PathVariable Long branchCode,HttpServletRequest httpRequest,
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
			List<HomePagePrintersResponse> printers = homeService.getHomePagePrinters(branchCode);
			return new ResponseEntity<List<HomePagePrintersResponse>>(printers, HttpStatus.OK);
		}
		
	}
	
//	@GetMapping("/filter-printers")
//	public ResponseEntity<List<HomePagePrintersResponse>> filterPrinters(@RequestBody FilterPrinterRequest request){
//		List<HomePagePrintersResponse> printers = homeService.filterPrinters(request);
//		return new ResponseEntity<List<HomePagePrintersResponse>>(printers, HttpStatus.OK);
//	}
	
	@PostMapping("/filter-printers")
	public ResponseEntity filterPrinters(@RequestBody FilterPrinterRequest request,HttpServletRequest httpRequest,
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
			List<HomePagePrintersResponse> printers = homeService.filterPrinters(request);
			return new ResponseEntity<List<HomePagePrintersResponse>>(printers, HttpStatus.OK);
		}
		
		
		
	}
	
	@GetMapping("/search-job-details/{branchCode}/{serachKey}")
	public ResponseEntity searchJobDetails(@PathVariable Long branchCode, @PathVariable String serachKey,HttpServletRequest httpRequest,
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
			List<TransactionResponse> response = homeService.searchJobDetails(branchCode, serachKey);
			return new ResponseEntity<List<TransactionResponse>>(response, HttpStatus.OK);
		}	
		
	}
	
	@PostMapping("/PrintTestPage")
	public ResponseEntity PrintTestPageDetails(@RequestBody HomeTestPrintRequest requestDto,HttpServletRequest httpRequest,
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
			homeService.printtestpageDetails(requestDto);
			return new ResponseEntity<String>("test print added", HttpStatus.CREATED);	
		}		 
	}
	
	@GetMapping("/ASCIIFIle/{file}")
	public ResponseEntity getASCIIFIle(@PathVariable String file,HttpServletRequest httpRequest,
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
			EbcdicToAscii ebcdiToAsciiObj = new EbcdicToAscii();
			String ebcdicFile="/pmu/input/"+file;
			File destinationfile =ebcdiToAsciiObj.convertEbcdicToAscii(ebcdicFile);
	        return new ResponseEntity<>("Welcome", HttpStatus.OK);
		}
		
		
	}
}
