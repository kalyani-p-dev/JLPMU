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
import com.jlp.pmu.dto.PrinterResponse;
import com.jlp.pmu.dto.ReportRequest;
import com.jlp.pmu.dto.ReportResponse;
import com.jlp.pmu.service.ReportService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin("*")
@RequestMapping("/pmu/v1/api/report")
public class ReportController {
	
	@Autowired
	ReportService reportService;
	
	@PostMapping("/add-report")
	public ResponseEntity addReport(@RequestBody ReportRequest requestDto,HttpServletRequest httpRequest,
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
			ReportResponse response = reportService.addReport(requestDto);
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}		
	
		
	}
	
	@PutMapping("/update-report")
	public ResponseEntity updateReport(@RequestBody ReportRequest requestDto,HttpServletRequest httpRequest,
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
			ReportResponse response = reportService.updateReport(requestDto);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		
	}
	
	@GetMapping("/get-all-reports/branch/{branchCode}")
	public ResponseEntity getAllReports(@PathVariable Long branchCode,HttpServletRequest httpRequest,
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
			List<ReportResponse> response = reportService.getAllReports(branchCode);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}	
	}
	
	
	@PutMapping("/inactive-report")
	public ResponseEntity inactiveReport(@RequestBody ReportRequest requestDto,HttpServletRequest httpRequest,
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
			ReportResponse response = reportService.inactiveReport(requestDto);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}		
		
	}

}
