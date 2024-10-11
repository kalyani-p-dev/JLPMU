package com.jlp.pmu.dataingestion;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;
import com.jlp.pmu.enums.PrinterJobStatus;
import com.jlp.pmu.models.Printer;
import com.jlp.pmu.models.Report;
import com.jlp.pmu.pojo.ReportMetaData;
import com.jlp.pmu.repository.PrinterRepository;
import com.jlp.pmu.repository.ReportRepository;
import com.jlp.pmu.service.JobDetailsService;
import com.jlp.pmu.utility.PrinterUtility;
import com.jlp.pmu.utility.Utility;

@Component
@Service

public class MessageProcessor {

	 @Autowired
	 JobDetailsService jobDetailsService;
	 
	 @Autowired
		ReportRepository reportRepository;
	 
		@Autowired
		PrinterRepository printerRepository;
		
		
		@Autowired
		PrinterUtility printerUtilObj;
	 
	Properties propObj=null;
	ReportMetaData repMetaDataObj= null;
	 Utility utilObj =null;
	 
	 
	 
	
	 
	 
	 public MessageProcessor() throws IOException
	 {
		 utilObj = new Utility();
		 propObj=utilObj.readPropertiesFile();
		 
		 
		 
	 }
	
	
	
	private String appendHtmlContentInEachLine(List htmlContentForTheTemplateParameter)
	{
		String finalHtmlContentPerRecord="",eachValue="";
		
		Iterator<String> eachValueInsideOneCBSRecord;
		
				
				if(htmlContentForTheTemplateParameter!=null&& htmlContentForTheTemplateParameter.size()>0)
				{
					eachValueInsideOneCBSRecord = htmlContentForTheTemplateParameter.iterator();
					while(eachValueInsideOneCBSRecord.hasNext())
					{
						eachValue = eachValueInsideOneCBSRecord.next();
						
						
						if(StringUtils.isEmpty(eachValue)||StringUtils.isBlank(eachValue))
							continue;
						
						eachValue = StringUtils.trim(eachValue);
						
						
						
						//finalHtmlContentPerRecord=finalHtmlContentPerRecord+eachValue+"<br></br>";
					finalHtmlContentPerRecord=finalHtmlContentPerRecord+eachValue+"<br></br>";
						
						//finalHtmlContentPerRecord=finalHtmlContentPerRecord+eachValue;
						
						if(StringUtils.containsIgnoreCase(finalHtmlContentPerRecord, "&"))
								finalHtmlContentPerRecord= finalHtmlContentPerRecord.replaceAll("&", "&amp;");
						//finalHtmlContentPerRecord=finalHtmlContentPerRecord+eachValueInsideOneCBSRecord.next();
						
					}
				}
		
		
		
		return finalHtmlContentPerRecord;
	}
	
	
	@Scheduled(fixedRate = 10000)
	 public void scheduleInputJobProcessing()
	 {
		 SimpleDateFormat dateFormat = new SimpleDateFormat(
		            "dd-MM-yyyy HH:mm:ss.SSS");
		 
		        String strDate = dateFormat.format(new Date());
		 
		        
		        
		        

				String dirInputAsciiFile = propObj.getProperty("inputSourceDirPath");
				
				System.out.println(
			            "Polling for EBCDIC file - "+" Dir location "+dirInputAsciiFile+" time="
			            + strDate);
				File directory = new File(dirInputAsciiFile); 
				  
		        // Create an object of Class MyFilenameFilter 
		        // Constructor with name of file which is being 
		        // searched 
				
		  
		        // store all names with same name  
		        // with/without extension 
		        String[] flist = directory.list() ;
		  
		        // Empty array 
		       
		  
		            // Print all files with same name in directory 
		            // as provided in object of MyFilenameFilter 
		            // class 
		            for (int i = 0; i < flist.length; i++) { 
		               // System.out.println(flist[i]+" found"); 
		                processInputFile(dirInputAsciiFile+flist[i]);
		            
		        } 
			
	 }
	 

	
	
	private String processAllCBSContent()
	{
		String htmlContent="";
		
		//String[] eachCbsList=asciFileContent.split("1\\?");
		
		List listOfAllCBSRecords = repMetaDataObj.getListOfUniqueRecordsInSourceFile();
		
		String exclusionKeyInPropertyFile ="CBS_"+repMetaDataObj.getReportName()+"_exclusionList";
		
		
		int counter=0;
		
		String eachCBSRecordContentInSourceFile=null;
		List<String> listOfAllCBSReacordAfterFormatting= new ArrayList<String>();
		
Iterator<String> eachCBSLineIterator;
String[] cbsRecordAfterFormatting;
		
		if(listOfAllCBSRecords!=null&& listOfAllCBSRecords.size()>0)
		{
			eachCBSLineIterator = listOfAllCBSRecords.iterator();
			
			while(eachCBSLineIterator.hasNext())
			{
				counter++;
				
				
				
				eachCBSRecordContentInSourceFile= eachCBSLineIterator.next();
				
				if(utilObj.isLineContainsString(eachCBSRecordContentInSourceFile, propObj.getProperty(exclusionKeyInPropertyFile)))
					continue;
				
				if(StringUtils.containsIgnoreCase(eachCBSRecordContentInSourceFile, repMetaDataObj.getReportName()))
					eachCBSRecordContentInSourceFile=StringUtils.substringAfterLast(eachCBSRecordContentInSourceFile, "?1");
				
				cbsRecordAfterFormatting = StringUtils.split(eachCBSRecordContentInSourceFile, "?");
				
				if(cbsRecordAfterFormatting!=null&&cbsRecordAfterFormatting.length>0)
				{
					listOfAllCBSReacordAfterFormatting = Arrays.asList(cbsRecordAfterFormatting);
					
				
				//	htmlContent = htmlContent+appendHtmlContentInEachLine(listOfAllCBSReacordAfterFormatting)+"<br><br/>";
					htmlContent = htmlContent+appendHtmlContentInEachLine(listOfAllCBSReacordAfterFormatting);
					
				}
				
				
				
			}
			
		}
		
		
		return htmlContent;
	}
	
	
	private void processEachToken(List <String>listOfAllWordsInALine)
	{
		String htmlContentForTheTemplateParameter=null;
		if(listOfAllWordsInALine!=null&&listOfAllWordsInALine.size()>0)
		{
			String eachUniqueWordInTemplate,definitionOfVariableDefinedInTemplate=null,keyInPropertyFile=null;
			Iterator<String> eachTokenIter=null;
			
			eachTokenIter = listOfAllWordsInALine.iterator();
			
			
			
			
			while(eachTokenIter.hasNext())
			{
				eachUniqueWordInTemplate= eachTokenIter.next();
				
				if(StringUtils.startsWithIgnoreCase(eachUniqueWordInTemplate, "$"))
				{
					
					
					repMetaDataObj.setSimpleTabularDataComingInSource(false);
						
					keyInPropertyFile =repMetaDataObj.getKeyPrefix()+repMetaDataObj.getReportName()+"_"+StringUtils.substringAfter(eachUniqueWordInTemplate, "$");
					definitionOfVariableDefinedInTemplate= propObj.getProperty(keyInPropertyFile);
					
					//if(StringUtils.equalsIgnoreCase(definitionOfVariableDefinedInTemplate, "generateAll")&&repMetaDataObj.isTabularReport())
						
					if(StringUtils.equalsIgnoreCase(definitionOfVariableDefinedInTemplate, "generateAll"))
					{
							
						htmlContentForTheTemplateParameter=utilObj.generateTabularReport(repMetaDataObj);
					}
					else
					{
						htmlContentForTheTemplateParameter=processAllCBSContent();
					}
						if(StringUtils.length(eachUniqueWordInTemplate)>1)
						repMetaDataObj.setKeyValueOfTemplate(eachUniqueWordInTemplate, htmlContentForTheTemplateParameter);
					}
				
				
					
				
				
			}
			
			
		}
	}
	
	
	
	synchronized public void processInputFile(String absPathofInputFile)
	{
		
		
		long branchIdLongValue;
		String absPathofAsciiFile,printerName="",dept=null,subDept=null;
		
		 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		 
		 
		
		if(StringUtils.containsIgnoreCase(absPathofInputFile, "ascii"))
			absPathofAsciiFile=absPathofInputFile;
		else
		{
		EbcdicToAscii ebcdiFileProcessorObj = new EbcdicToAscii();
		
		FormatInput dataFormatObj = new FormatInput();
		
		File asciiFileObj = ebcdiFileProcessorObj.convertEbcdicToAscii(absPathofInputFile);
		String updatedAsciiFileAbsPath = StringUtils.substringBeforeLast(asciiFileObj.getAbsolutePath(), ".")+"_ascii.txt";
		
		utilObj.createFile(updatedAsciiFileAbsPath, dataFormatObj.refineAscciFileContent(asciiFileObj.getAbsolutePath()));
		
		System.out.println("updatedAsciiFileAbsPath=="+updatedAsciiFileAbsPath);
		
		//asciiFileObj.
		/*
		if(!utilObj.copyTheFileToADirectory(utilObj.prop.getProperty("InProgressFolderPath"), absPathofAsciiFile))
		{
			System.out.println("File already preocessed");
			return;
			
		}
		*/
		
		//String absPathofAsciiFile = asciiFileObj.getAbsolutePath();
		
		 absPathofAsciiFile = updatedAsciiFileAbsPath;
		 return;
		 
		}
		
		System.out.println("Starting the processing of "+absPathofAsciiFile);
		
		repMetaDataObj = utilObj.populateReportMetaData(absPathofAsciiFile);
		repMetaDataObj.setAbsPathOfAsciiFile(absPathofAsciiFile);
		LocalDateTime arrivalTime = LocalDateTime.now(); 
		repMetaDataObj.getTransactionVariableList().put("arrivalTime",arrivalTime );
		
		
		String htmlFileAbsPath;
		
		
		if(StringUtils.isBlank(repMetaDataObj.getBranchId())||StringUtils.isBlank(repMetaDataObj.getReportName())||!StringUtils.isAlphanumeric(repMetaDataObj.getBranchId()))
		{
			System.out.println("Error occured during the file processor");
			utilObj.createFile(propObj.getProperty("errorDirectoryOfTemplate")+StringUtils.substringAfterLast(absPathofAsciiFile,"/")+".err", repMetaDataObj.getSourceFileContent());
			return;
		}
		else
		{
			File inpuTfileObj = new File(absPathofAsciiFile);
			inpuTfileObj.delete();
			
		}
		
		
		
		List listOfAllLinesInTemplate= utilObj.fileContentAsLineList(repMetaDataObj.getHtmlFileContentWithParameter());
		List <String>listOfAllWordsInALine;
		
		String singleLineInFile;
		
		Iterator eachLineIterator;
		
		if(listOfAllLinesInTemplate!=null&& listOfAllLinesInTemplate.size()>0)
		{
			eachLineIterator = listOfAllLinesInTemplate.iterator();
			
			while(eachLineIterator.hasNext())
			{
				singleLineInFile=(String) eachLineIterator.next();
				
				if(StringUtils.containsIgnoreCase(singleLineInFile, "$Row"))
				{
					String[] listWithRowColumnValue = singleLineInFile.split("\\$Row_");//StringUtils.split(entry,"Row");
					//ArrayList newList = new ArrayList<>();

					for(int i=0;i<listWithRowColumnValue.length;i++) {
						if(i==0) {
							continue;
						}
						//newList.add("$Row_"+listWithRowColumnValue[i].substring(0, listWithRowColumnValue[i].indexOf("#")));
						repMetaDataObj.setKeyValueOfTemplate("$Row_"+listWithRowColumnValue[i].substring(0, listWithRowColumnValue[i].indexOf("#")), null);
						repMetaDataObj.setSimpleTabularDataComingInSource(true);
					}
					continue;
				}
				
				listOfAllWordsInALine=utilObj.getTheListOfAllWordsInLine(singleLineInFile);
			
				
				processEachToken(listOfAllWordsInALine);
				
			
			}
			
		}
		
		String htmlContent;
		
		if(repMetaDataObj.isSimpleTabularDataComingInSource())
		{
			utilObj.replaceDynamicVariablesForAllNonGenericTemplate(repMetaDataObj);
			
		}
		else
			utilObj.replaceDynamicVariableInTemplate(repMetaDataObj);
		
	
		
		
		
		
		
		//utilObj.repMetaData.setFinalHtmlContent(utilObj.replaceSpecialCharacterWithEmptySpace(repMetaDataObj.getFinalHtmlContent(), propObj.getProperty("PdfExcusionCharacters")));
		
		
		if(repMetaDataObj.isBarCodeNeeded())
		utilObj.insertBarCodeToHtmlForTabularReport(repMetaDataObj.getBarCodeDefinition());
	//htmlFileAbsPath = propObj.getProperty("htmlOutputDirectory")+repMetaDataObj.getBranchId()+"\\"+repMetaDataObj.getReportName()+".html";
		
		//htmlFileAbsPath =propObj.getProperty("applicationDataRootDir")+"\\output\\"+ propObj.getProperty("BranchName_"+repMetaDataObj.getBranchId())+"\\"+propObj.getProperty("htmlOutputDirectory")+"\\"+repMetaDataObj.getReportName()+"-"+utilObj.getUniqueFileNameBasedOnDateAndTime()+".html";
		
		String businessFriendlyReportName = propObj.getProperty("ReportName_"+repMetaDataObj.getReportName());
		
		
		
		
		if(StringUtils.isEmpty(businessFriendlyReportName))
			businessFriendlyReportName=	repMetaDataObj.getReportName();
		
		utilObj.repMetaData.setBusinessFriendlyReportName(businessFriendlyReportName);
		
		htmlFileAbsPath =propObj.getProperty("applicationDataRootDir")+"/output/"+ propObj.getProperty("BranchName_"+repMetaDataObj.getBranchId())+"/"+propObj.getProperty("htmlOutputDirectory")+"/"+businessFriendlyReportName+"-"+utilObj.getUniqueFileNameBasedOnDateAndTime()+".html";
		
		System.out.println("htmlFileAbsPath=="+htmlFileAbsPath);
		
	
		utilObj.createFile(htmlFileAbsPath,repMetaDataObj.getFinalHtmlContent());
		
		
		   LocalDateTime startTimeOfProcessing = LocalDateTime.now();  
		   
		
		
		
		
	
		utilObj.repMetaData.setAbsPathOfHtmlFile(htmlFileAbsPath);
		
		// String pdfFileAbsPath = propObj.getProperty("SourceDirectoryOfPDF")+repMetaDataObj.getBranchId()+"\\"+repMetaDataObj.getReportName()+".pdf";
			
		 String pdfFileAbsPath = StringUtils.replace(htmlFileAbsPath, "html", "pdf");
		 System.out.println("pdfFileAbsPath=="+pdfFileAbsPath);
		 
		 String processedFileAbsPath = StringUtils.replace(htmlFileAbsPath, "html", "processed");
		 
		 
		 processedFileAbsPath= StringUtils.substringBeforeLast(processedFileAbsPath, "/")+"/"+StringUtils.substringAfterLast(absPathofAsciiFile, "/");
		 
		 System.out.println("processedFileAbsPath=="+processedFileAbsPath);
		 
		 utilObj.createFile(processedFileAbsPath, utilObj.repMetaData.getSourceFileContent());
		 
		 
		 String zplFileAbsPath = StringUtils.replace(htmlFileAbsPath, "html", "zpl");
		 
		 System.out.println("zplFileAbsPath=="+zplFileAbsPath);
		 
		 System.out.println("Populating JOb Object and print transaction obj such as reportDescription");
		 
		 utilObj.htmlToPdf(pdfFileAbsPath);
		 File toBePrintedFile;
		 
		 PrinterJobStatus printJobStatus=null;
		 
		 
		 printerUtilObj.initializePrinterProperties();

		 //repMetaDataObj.setJobStatus("JOB_COMPLETE");
		 //repMetaDataObj.setNoOfCopies(2);
		 printerUtilObj.repMetaDataObj=repMetaDataObj;
		 
		 System.out.println("Initialized printerUtilObj "+printerUtilObj);
		 
	 branchIdLongValue = Integer.parseInt(repMetaDataObj.getBranchId());
	 
	 System.out.println("branchIdLongValue=="+branchIdLongValue);
		 
		 Integer branchId = (int) (long) branchIdLongValue;
		 
		 System.out.println("branchId obj=="+branchId);
		 
		 utilObj.populateTransactionVariables(repMetaDataObj);
		 
		 dept=(String) repMetaDataObj.getTransactionVariableList().get("deptCode");
		 subDept=(String) repMetaDataObj.getTransactionVariableList().get("subDeptCode");
		 
		 System.out.println("dept and subDept from input file=="+dept+" And "+subDept);
		
		 
		 printerName = printerUtilObj.getPrinterName(branchId, repMetaDataObj.getReportName(),dept,subDept);
		 
		 System.out.println("printerName=="+printerName+" Hold status "+repMetaDataObj.isHold());
		 
		 if(repMetaDataObj.keyValueOfZPLTemplate.size()>0)
		 {
			 utilObj.createFile(zplFileAbsPath, repMetaDataObj.getZplOutputFileContent());
			 toBePrintedFile = new File(zplFileAbsPath);
			 
			 
				 
		 }
		 else
		 {
			 toBePrintedFile = new File(pdfFileAbsPath);
			 
			 if(toBePrintedFile.exists()&&StringUtils.isNotBlank(printerName)&&!repMetaDataObj.isHold())
			 {
				 repMetaDataObj.setStartTime(startTimeOfProcessing);
				 printJobStatus= printerUtilObj.startPrintJob(pdfFileAbsPath, printerName);
			 }
		 }
		 
		 System.out.println("printJobStatus=="+printJobStatus);
		 
		 repMetaDataObj.setPrinterName(printerName);
			
		 
		 repMetaDataObj.setPdfFileAbsPath(pdfFileAbsPath);
		 
		 utilObj.populatePrintTransactionDetails(repMetaDataObj);
		 
		 System.out.println("Before saving in DB=="+repMetaDataObj.getTransactionVariableList());
			
	
			
			
		 String response = jobDetailsService.storeTransactionVariables(repMetaDataObj),absPathOfRetryFile="",firstRetryFile,secondRetryFile,thridReTryFile;
		File firstRetryFileObj,secondRetryFileObj,thirdRetryFileObj;
		 
		 if(printJobStatus.name().equalsIgnoreCase(PrinterJobStatus.PRINTER_SERVICE_UNAVAILABLE.name()))
		 {
			 firstRetryFile="/pmu/dev/retry/"+StringUtils.substringAfterLast(repMetaDataObj.getAbsPathOfAsciiFile(), "/")+"_1";
			 secondRetryFile="/pmu/dev/retry/"+StringUtils.substringAfterLast(repMetaDataObj.getAbsPathOfAsciiFile(), "/")+"_2";
			 thridReTryFile="/pmu/dev/retry/"+StringUtils.substringAfterLast(repMetaDataObj.getAbsPathOfAsciiFile(), "/")+"_3";

			 firstRetryFileObj = new File(firstRetryFile);
			 secondRetryFileObj = new File(secondRetryFile);
			 thirdRetryFileObj = new File(thridReTryFile);
			 
			 
			 
			  
			 
			 if(!firstRetryFileObj.exists()&&!secondRetryFileObj.exists()&&!thirdRetryFileObj.exists())
			 {
				 absPathOfRetryFile=firstRetryFile;
				 
			 }
			 if(firstRetryFileObj.exists()&&!secondRetryFileObj.exists()&&!thirdRetryFileObj.exists())
			 {
				 absPathOfRetryFile=secondRetryFile;
				 firstRetryFileObj.delete();
			 }
			 if(firstRetryFileObj.exists()&&secondRetryFileObj.exists()&&!thirdRetryFileObj.exists())
			 {
				 absPathOfRetryFile=thridReTryFile;
				 secondRetryFileObj.delete();
				 
			 }
			 
			 
			 utilObj.createFile(absPathOfRetryFile, repMetaDataObj.getAbsPathOfAsciiFile());
		 }
		 else
		 {
			 processedFileAbsPath= StringUtils.substringBeforeLast(processedFileAbsPath, "/")+"/"+StringUtils.substringAfterLast(absPathofAsciiFile, "/");
			 utilObj.createFile(processedFileAbsPath, utilObj.repMetaData.getSourceFileContent());
			 
		 }
		 
		 
		 
		 
		 
		 
		 
		
		
		
		//utilObj.geteratePDf();
		if(repMetaDataObj.isMaskingNeeded())
			utilObj.createFile("/pmu/cdspickup_masked.txt", repMetaDataObj.getSourceFileContent());
		
		
		
 
 return ;
 
	
	}


}
