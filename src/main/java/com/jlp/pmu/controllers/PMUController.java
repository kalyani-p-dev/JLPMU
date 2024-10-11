package com.jlp.pmu.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jlp.pmu.dataingestion.MessageProcessor;

@RestController
@RequestMapping("/pmu/v1/api")
public class PMUController {
	
	@Autowired
	MessageProcessor messageProcessor;
	
	//Testing
	@GetMapping("/hello")
	public String tesGet() {
		
		String test = "/home/J84570352UA/input/cbs_in/test.PROCESSED";
		checkMFTReportType(test);
		return "WELCOME JMS Test scheduling ";
	}
	
	 private String checkMFTReportType(String absPathOfEbcdiFile) {
		 System.out.println("test1 :"+absPathOfEbcdiFile);
			String dataString = "";
			String headerValue = "";
			try (FileInputStream ebcdicInputStream = new FileInputStream(absPathOfEbcdiFile);
					InputStreamReader ebcdicReader = new InputStreamReader(ebcdicInputStream, "IBM500");) {
				int data;
				while ((data = ebcdicReader.read()) != -1) {
					dataString += (char) data;
				}
				String[] items = dataString.split(" ");
				headerValue = StringUtils.substring(items[0], 0, 6);
			} catch (IOException e) {
				System.out.println("error in checkMFTReportType :"+e.getMessage());
			}try {
				System.out.println("test2 :"+absPathOfEbcdiFile);
			String filePathAndName = "/home/J84570352UA/input/cbs_in/test.PROCESSED";
	        ClassLoader classLoader = getClass().getClassLoader();
	        File file = new File(classLoader.getResource(".").getFile() + filePathAndName );
	        System.out.println("file filepath :"+file.getPath());
	 System.out.println("headerValue :"+headerValue);
			}catch (Exception e) {
				System.out.println("error in checkMFTReportType :"+e.getMessage());
			}
			return headerValue;
		}
	  
	
	@GetMapping("/testMessage")
	public String testMessage() throws Exception {

		messageProcessor.processInputFile("C:\\projects\\PMU\\asciiFiles\\unix\\cdsprintman_MQ");
		return "test success";
	}
}
