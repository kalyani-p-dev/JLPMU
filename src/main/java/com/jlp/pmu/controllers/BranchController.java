package com.jlp.pmu.controllers;

import java.util.ArrayList;
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
import com.jlp.pmu.dto.BranchRequest;
import com.jlp.pmu.dto.BranchResponse;
import com.jlp.pmu.service.BranchService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin("*")
@RequestMapping("/pmu/v1/api/branch")
public class BranchController {

	@Autowired
	BranchService branchService;

	@PostMapping("/add-branch")
	public ResponseEntity addBranchDetails(@RequestBody BranchRequest requestDto,HttpServletRequest request,HttpServletResponse response) {
		String errorMessage = (String) request.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = response.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
		BranchResponse responseDto = branchService.addBranchDetails(requestDto);
		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
		}

	}

	@GetMapping("/get-all-branch")
	public ResponseEntity getAllBranchDetails(HttpServletRequest request,HttpServletResponse response) {
		System.out.println("testing branch");
		List<BranchResponse> branches = new ArrayList<BranchResponse>();
		String errorMessage = (String) request.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = response.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
			branches = branchService.getAllBranchDeatils();
			 return new ResponseEntity<List<BranchResponse>>(branches, HttpStatus.OK);
		}
		
		
	}

	@GetMapping("/get-branch/{branchCode}")
	public ResponseEntity getBranchDetailsByBranchID(@PathVariable Long branchCode,HttpServletRequest request,HttpServletResponse httpResponse) {
		String errorMessage = (String) request.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
		BranchResponse response = branchService.getBranchDetailsByID(branchCode);
		return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
	}

	@GetMapping("/get-pmu-servers")
	public ResponseEntity getAllPMUServers(HttpServletRequest  httpRequest,HttpServletResponse httpResponse) {
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
		List<String> response = branchService.getAllPMUServers();
		return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/get-allMnemonic")
	public ResponseEntity getAllMnemonic(HttpServletRequest  httpRequest,HttpServletResponse httpResponse) {
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
		List<String> response = branchService.getAllMnemonic();
		return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@PutMapping("/inactive")
	public ResponseEntity deleteBranchDetailsByBranchCode(@RequestBody BranchRequest requestDto,HttpServletRequest  httpRequest,HttpServletResponse httpResponse) {
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
		BranchResponse response = branchService.doInactiveBranchByBranchCode(requestDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	}

}
