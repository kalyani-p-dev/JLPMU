
package com.jlp.pmu.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jlp.pmu.constant.Constant;
import com.jlp.pmu.dto.PrinterResponse;
import com.jlp.pmu.dto.UserRequest;
import com.jlp.pmu.dto.UserResponse;
import com.jlp.pmu.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin("*")
@RequestMapping("/pmu/v1/api/user")
public class UserController {
	
	@Autowired
	UserService userService;

	@PostMapping("/add-user")
	public ResponseEntity addUserDetails(@RequestBody UserRequest requestDto,HttpServletRequest httpRequest,HttpServletResponse httpResponse) {
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
			UserResponse responseDto = userService.addUserDetails(requestDto);
			return new ResponseEntity<UserResponse>(responseDto, HttpStatus.CREATED);
		}	
		
		}

	
	@PutMapping("/update-user")
	public ResponseEntity updateUserDetails(@RequestBody UserRequest requestDto,HttpServletRequest httpRequest,HttpServletResponse httpResponse) {
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
			UserResponse responseDto = userService.updateUserDetails(requestDto);
			return new ResponseEntity<UserResponse>(responseDto, HttpStatus.OK);
		}	
		

	}
	
	@GetMapping("/get-all-user")
	public ResponseEntity getAllUserDetails(HttpServletRequest httpRequest,HttpServletResponse httpResponse) {
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
			List<UserResponse> users = userService.getAllUserDeatils();
			return new ResponseEntity<List<UserResponse>>(users, HttpStatus.OK);
		}
		
	}
	
	
}
