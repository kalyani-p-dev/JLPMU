package com.jlp.pmu.service.impl;

//Java Program to Illustrate Creation Of
//Service implementation class

import java.io.File;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;	
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.jlp.pmu.pojo.EmailDetails;
import com.jlp.pmu.service.EmailService;

import jakarta.mail.internet.MimeMessage;

//Annotation
@Service
//Class
//Implementing EmailService interface
public class EmailServiceImpl implements EmailService {

	@Autowired 
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}") 
	private String sender;

	// Method 1
	// To send a simple email
	public String sendSimpleMail(EmailDetails details)
	{

		// Try block to check for exceptions
		try {

			// Creating a simple mail message
			SimpleMailMessage mailMessage
				= new SimpleMailMessage();

			// Setting up necessary details
			mailMessage.setFrom("no-reply.pmu@dev.johnlewis.co.uk");
			mailMessage.setTo(details.getRecipient());
			mailMessage.setText(details.getMsgBody());
			mailMessage.setSubject(details.getSubject());

			// Sending the mail
			javaMailSender.send(mailMessage);
			return "Mail Sent Successfully...";
		}

		// Catch block to handle the exceptions
		catch (Exception e) {
			return "Error while Sending Mail :"+e.getMessage();
		}
	}
	
	
	public String sendMailWithAttachment(EmailDetails details) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper;
		 try {
			 mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			 mimeMessageHelper.setFrom("no-reply.pmu@dev.johnlewis.co.uk");
	         mimeMessageHelper.setTo(details.getRecipient());
	         mimeMessageHelper.setText(details.getMsgBody());
	         mimeMessageHelper.setSubject(details.getSubject());
	         FileSystemResource file = new FileSystemResource(new File(details.getAttachment()));
	         mimeMessageHelper.addAttachment(file.getFilename(), file);
	         javaMailSender.send(mimeMessage);
	         return "Mail Sent Successfully...";
		 }catch(Exception e) {
			 return "Error while sending mail!!!"+e.getMessage(); 
		 }
	}

	// Method 2
	// To send an email with attachment
	/*
	 * public String sendMailWithAttachment(EmailDetails details) { // Creating a
	 * mime message MimeMessage mimeMessage = javaMailSender.createMimeMessage();
	 * MimeMessageHelper mimeMessageHelper;
	 * 
	 * try {
	 * 
	 * // Setting multipart as true for attachments to // be send mimeMessageHelper
	 * = new MimeMessageHelper(mimeMessage, true);
	 * mimeMessageHelper.setFrom(sender);
	 * mimeMessageHelper.setTo(details.getRecipient());
	 * mimeMessageHelper.setText(details.getMsgBody());
	 * mimeMessageHelper.setSubject( details.getSubject());
	 * 
	 * // Adding the attachment FileSystemResource file = new FileSystemResource(
	 * new File(details.getAttachment()));
	 * 
	 * mimeMessageHelper.addAttachment( file.getFilename(), file);
	 * 
	 * // Sending the mail javaMailSender.send(mimeMessage); }
	 * 
	 * // Catch block to handle MessagingException catch (MessagingException e) {
	 * 
	 * // Display message when exception occurred return
	 * "Error while sending mail!!!"; } return "Mail sent Successfully";
	 * 
	 * }
	 */
}

