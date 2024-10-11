package com.jlp.pmu.service;

import com.jlp.pmu.pojo.EmailDetails;

//Java Program to Illustrate Creation Of
//Service Interface

//Importing required classes


//Interface
public interface EmailService {

	// Method
	// To send a simple email
	String sendSimpleMail(EmailDetails details);

	// Method
	// To send an email with attachment
	String sendMailWithAttachment(EmailDetails details);
}

