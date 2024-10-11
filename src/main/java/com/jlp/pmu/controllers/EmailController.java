package com.jlp.pmu.controllers;

//Java Program to Create Rest Controller that
//Defines various API for Sending Mail

//Importing required classes

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jlp.pmu.constant.Constant;
import com.jlp.pmu.pojo.EmailDetails;
import com.jlp.pmu.service.EmailService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//Annotation
@RestController
@RequestMapping("/pmu/v1/api/email")
//Class
public class EmailController {

	@Autowired
	private EmailService emailService;

	// Sending a simple Email
	@PostMapping("/sendMail")
	public ResponseEntity sendMail(@RequestBody EmailDetails details, HttpServletRequest httpRequest,
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
		String status = emailService.sendSimpleMail(details);
		return new ResponseEntity<>(status, HttpStatus.OK);
		}
	}

	// Sending email with attachment

	@PostMapping("/sendMailWithAttachment")
	public ResponseEntity sendMailWithAttachment(@RequestBody EmailDetails details,HttpServletRequest httpRequest,HttpServletResponse httpResponse) {
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		
		
		}else {
		String status = emailService.sendMailWithAttachment(details);
		return new ResponseEntity<>(status, HttpStatus.OK);
		}
	}

}
