package com.jlp.pmu.controllers;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.jlp.pmu.utility.PrintOutputGenerator;


@Controller
public class GenericOutputGeneration {


@RestController
@RequestMapping("pmu/v1/api/startPrint")
public class ProductController {
	
	
	@PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public void startPrintJob(@RequestBody String inputData,@RequestParam(value="branchCode", required=false, defaultValue="") String branchCode) {
		System.out.println("Branch code trinity"+branchCode);	
		int countOfXmlStartTag=0,countOfXmlEndTag=0;
		
		countOfXmlStartTag = StringUtils.countMatches(inputData, "<");
		countOfXmlEndTag = StringUtils.countMatches(inputData, ">");
		
		if(!(countOfXmlStartTag>0&&countOfXmlEndTag>0&&countOfXmlEndTag==countOfXmlStartTag))
		{
		
		 JSONObject jsonObject = new JSONObject(inputData);
		 inputData = XML.toString(jsonObject);
		}
		
		
		//order="<message><products><product><property><name>messageType</name><text>salesData</text></property><property><name>productName</name><text>Sample product name</text></property><property><name>productID</name><text>1234</text></property><property><name>productDescription</name><text>Test JLP</text></property><property><name>storeName</name><text>JLP Function Test</text></property><property><name>storeAddress</name><text>yes</text></property><property><name>manufactureName</name><text>Testing with giles</text></property><property><name>manufactureAddress</name><text>yes</text></property><property><name>productExpiryDate</name><text>Pre_Zs_test_result value</text></property><property><name>Pre_tightness_test</name><text>Pre_tightness_test value</text></property><property><name>FunctionTest</name><text>FunctionTest value</text></property><property><name>applianceType</name><text>applianceType value</text></property><property><name>storeId</name><text>123</text></property><property><name>salesChannelElement</name><text>JLP Online order value</text></property><property><name>salesDayElement</name><text>31-April-2024</text></property></product><product><property><name>messageType</name><text>salesData</text></property><property><name>productName</name><text>Sample product name 1</text></property><property><name>productID</name><text>12345</text></property><property><name>productDescription</name><text>Test JLP 2</text></property><property><name>storeName</name><text>JLP Function Test 2</text></property><property><name>storeAddress</name><text>yes</text></property><property><name>manufactureName</name><text>Testing with giles 2</text></property><property><name>manufactureAddress</name><text>yes</text></property><property><name>productExpiryDate</name><text>Pre_Zs_test_result value 2 </text></property><property><name>Pre_tightness_test</name><text>Pre_tightness_test value</text></property><property><name>FunctionTest</name><text>FunctionTest value</text></property><property><name>applianceType</name><text>applianceType value</text></property><property><name>storeId</name><text>456</text></property><property><name>applianceType</name><text>applianceType value 2</text></property><property><name>salesChannelElement</name><text>Amazon Online order value</text></property><property><name>salesDayElement</name><text>30-April-2024</text></property></product></products></message>";
		Document mqXmlDocument=null;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		
		try
		{
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		
		 mqXmlDocument = dBuilder.parse(new InputSource(new java.io.StringReader(inputData)));
	        mqXmlDocument.getDocumentElement().normalize();
	     	PrintOutputGenerator ob = new PrintOutputGenerator();
	    	ob.initializeDataSource();
	    	ob.mqXmlDocument=mqXmlDocument;
	    	
	        ob.getTheKeyValuesForTemplateFromMessageQueue(ob.getTheMQValue(mqXmlDocument,"messageType"));
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
		

}

    
    @PostMapping({"/", "/print"})
    public String printcontroller(Model model, @RequestParam(value="deliveryNumber", required=false, defaultValue="World") String deliveryNumber) throws IOException {
     	
     	
     	PrintOutputGenerator ob = new PrintOutputGenerator();
     	
     	
     	
 		ob.initializeDataSource();
 		
 		File directoryPath = new File("C:\\projects\\PMU\\api_Input");
 	      //List of all files and directories
 	      String contents[] = directoryPath.list();
 	      System.out.println("List of files and directories in the specified directory:::");
 	      for(int i=0; i<contents.length; i++) {
 	    	  
 	    	// System.out.println("xml abs path ="+"C:\\\\projects\\\\JohnLewis\\\\PMU\\\\POC\\\\MQ\\"+contents[i]);
 	    	 
 	          ob.initializeMQXmlDoc("C:\\\\projects\\\\PMU\\\\api_Input\\"+contents[i]);
 	          
 	         
 	         ob.getTheKeyValuesForTemplateFromMessageQueue(ob.getTheMQValue(ob.mqXmlDocument,"messageType"));
 	      }
 	      
 	      
 		
 		
 		
 

 //model.addAttribute("name", "JMJ");
     	
       //  model.addAttribute("esbData", ob.getTheESBData(deliveryNumber));
         return "friar";
     }
    
}
