package com.jlp.pmu.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jlp.pmu.dto.FilterJobDetailsRequest;
import com.jlp.pmu.dto.JobDetailsListRequest;
import com.jlp.pmu.dto.JobDetailsResponse;
import com.jlp.pmu.dto.PrintJobDetailsRequest;
import com.jlp.pmu.dto.PrintSelectedPagesRequest;
import com.jlp.pmu.dto.TransactionResponse;
import com.jlp.pmu.service.JobDetailsService;

@RestController
@CrossOrigin("*")
@RequestMapping("/pmu/v1/api/job-details")
public class JobDetailsController {
	
	@Autowired
	JobDetailsService jobDetailsService;
	
	@GetMapping("/get-job-details")
	public ResponseEntity<List<JobDetailsResponse>> getListOfJobDetails(@RequestBody JobDetailsListRequest request){
		List<JobDetailsResponse> response = jobDetailsService.getListOfJobDetails(request);
		return new ResponseEntity<List<JobDetailsResponse>>(response, HttpStatus.OK);
	}
	
	@PostMapping("/print-selected-pages")
	public ResponseEntity<String> printSelectedPages(@RequestBody PrintSelectedPagesRequest request){
		String response = jobDetailsService.printSelectedPages(request);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
	
	@PostMapping("/reprocess-job")
	public ResponseEntity<String> reprocessJob(@RequestBody List<TransactionResponse> jobDetails){
		String response = jobDetailsService.reprocessJob(jobDetails);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
	
	@PostMapping("/PrintJobDetails")
	public ResponseEntity<String> printJobDetails(@RequestBody PrintJobDetailsRequest requestDto) {

		 jobDetailsService.printJobDetails(requestDto);
		return new ResponseEntity<String>("alternative printer added", HttpStatus.CREATED);
		
	}
	
//	@GetMapping("/filter-transactions")
//	public ResponseEntity<List<JobDetailsResponse>> filterJobDetails(@RequestBody FilterJobDetailsRequest request){
//		List<JobDetailsResponse> response = jobDetailsService.getAll_or_filter_jobDetails(request);
//		return new ResponseEntity<List<JobDetailsResponse>>(response, HttpStatus.OK);
//	}
	
	@PostMapping("/filter-transactions")
	public ResponseEntity<JobDetailsResponse> filterJobDetails(@RequestBody FilterJobDetailsRequest request){
		JobDetailsResponse response = jobDetailsService.getAll_or_filter_jobDetails(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
