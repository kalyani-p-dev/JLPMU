package com.jlp.pmu.pojo;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import com.jlp.pmu.enums.PrinterJobStatus;
import com.jlp.pmu.enums.PrinterJobType;
import com.jlp.pmu.enums.StatusType;



public class ReportMetaData {
	
	
	String pdfFileAbsPath,absPathOfHtmlFile,barCodeDefinition,printerName,pdfPath,absPathOfAsciiFile,businessFriendlyReportName,department,subDepartment;
	
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getSubDepartment() {
		return subDepartment;
	}
	public void setSubDepartment(String subDepartment) {
		this.subDepartment = subDepartment;
	}

	boolean hold;
	
	public boolean isHold() {
		return hold;
	}
	public void setHold(boolean hold) {
		this.hold = hold;
	}
	public String getBusinessFriendlyReportName() {
		return businessFriendlyReportName;
	}
	public void setBusinessFriendlyReportName(String businessFriendlyReportName) {
		this.businessFriendlyReportName = businessFriendlyReportName;
	}
	public String getAbsPathOfAsciiFile() {
		return absPathOfAsciiFile;
	}
	public void setAbsPathOfAsciiFile(String absPathOfAsciiFile) {
		this.absPathOfAsciiFile = absPathOfAsciiFile;
	}

	private Long printJobId,printerId;
	
	public Long getPrinterId() {
		return printerId;
	}
	public void setPrinterId(Long printerId) {
		this.printerId = printerId;
	}
	public Long getPrintJobId() {
		return printJobId;
	}
	public void setPrintJobId(long i) {
		this.printJobId = i;
	}

	private LocalDateTime startTime;


	public LocalDateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	public LocalDateTime getEndTime() {
		return endTime;
	}
	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	private LocalDateTime endTime;

	
	public String getPdfPath() {
		return pdfPath;
	}
	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}
	public String getPrinterName() {
		return printerName;
	}
	public void setPrinterName(String printerName) {
		this.printerName = printerName;
	}

	PrinterJobStatus printerJobStatus;
	
	

	public PrinterJobStatus getPrinterJobStatus() {
		return printerJobStatus;
	}
	public void setPrinterJobStatus(PrinterJobStatus printerJobStatus) {
		this.printerJobStatus = printerJobStatus;
	}

	int numberOfLinesToBeInsertedToFillPage,noOfCopies;
	
	
	public int getNoOfCopies() {
		return noOfCopies;
	}
	public void setNoOfCopies(int noOfCopies) {
		this.noOfCopies = noOfCopies;
	}

	boolean alignFirstLineEachRecordIsNeeded,isTabularReport,isSimpleTabularDataComingInSource,isMaskingNeeded;
	
String reportName,headerValue;

String zplOutputFileContent,zplTemplateContent;


public MultiValuedMap<String,String> keyValueOfZPLTemplate=new ArrayListValuedHashMap<String, String>();
public MultiValuedMap<String, String> getKeyValueOfZPLTemplate() {
	return keyValueOfZPLTemplate;
}
public void setKeyValueOfZPLTemplate(MultiValuedMap<String, String> keyValueOfZPLTemplate) {
	this.keyValueOfZPLTemplate = keyValueOfZPLTemplate;
}

public String getZplTemplateContent() {
	return zplTemplateContent;
}
public void setZplTemplateContent(String zplTemplateContent) {
	this.zplTemplateContent = zplTemplateContent;
}
public String getZplOutputFileContent() {
	return zplOutputFileContent;
}
public void setZplOutputFileContent(String zplOutputFileContent) {
	this.zplOutputFileContent = zplOutputFileContent;
}
	
	
	public String getHeaderValue() {
		return headerValue;
	}
	public void setHeaderValue(String headerValue) {
		this.headerValue = headerValue;
	}
	
	int indexOfFirstPrintableCharacter;
	public int getIndexOfFirstPrintableCharacter() {
		return indexOfFirstPrintableCharacter;
	}
	public void setIndexOfFirstPrintableCharacter(int indexOfFirstPrintableCharacter) {
		this.indexOfFirstPrintableCharacter = indexOfFirstPrintableCharacter;
	}
	
	
	public boolean isMaskingNeeded() {
		return isMaskingNeeded;
	}
	public void setMaskingNeeded(boolean isMaskingNeeded) {
		this.isMaskingNeeded = isMaskingNeeded;
	}
	public boolean isSimpleTabularDataComingInSource() {
		return isSimpleTabularDataComingInSource;
	}
	public void setSimpleTabularDataComingInSource(boolean isSimpleTabularDataComingInSource) {
		this.isSimpleTabularDataComingInSource = isSimpleTabularDataComingInSource;
	}
	LinkedHashMap<Integer,List<String>> reportNumberMap;
	
	public LinkedHashMap<Integer, List<String>> getReportNumberMap() {
		
		return reportNumberMap;
	}
	public void setReportNumberMap(LinkedHashMap<Integer, List<String>> reportNumberMap) {
		this.reportNumberMap = reportNumberMap;
	}
	public boolean isTabularReport() {
		return isTabularReport;
	}
	public void setTabularReport(boolean isTabularReport) {
		this.isTabularReport = isTabularReport;
	}
	public boolean isAlignFirstLineEachRecordIsNeeded() {
		return alignFirstLineEachRecordIsNeeded;
	}
	public void setAlignFirstLineEachRecordIsNeeded(boolean alignFirstLineEachRecordIsNeeded) {
		this.alignFirstLineEachRecordIsNeeded = alignFirstLineEachRecordIsNeeded;
	}
	public int getNumberOfLinesToBeInsertedToFillPage() {
		return numberOfLinesToBeInsertedToFillPage;
	}
	public void setNumberOfLinesToBeInsertedToFillPage(int numberOfLinesToBeInsertedToFillPage) {
		this.numberOfLinesToBeInsertedToFillPage = numberOfLinesToBeInsertedToFillPage;
	}

	public String getBarCodeDefinition() {
		return barCodeDefinition;
	}
	public void setBarCodeDefinition(String barCodeDefinition) {
		this.barCodeDefinition = barCodeDefinition;
	}
	public boolean isBarCodeNeeded() {
		return barCodeNeeded;
	}
	public void setBarCodeNeeded(boolean barCodeNeeded) {
		this.barCodeNeeded = barCodeNeeded;
	}
	boolean barCodeNeeded=false;
	
	
	public String getAbsPathOfHtmlFile() {
		return absPathOfHtmlFile;
	}
	public void setAbsPathOfHtmlFile(String absPathOfHtmlFile) {
		this.absPathOfHtmlFile = absPathOfHtmlFile;
	}
	public String getPdfFileAbsPath() {
		return pdfFileAbsPath;
	}
	public void setPdfFileAbsPath(String pdfFileAbsPath) {
		this.pdfFileAbsPath = pdfFileAbsPath;
		this.pdfPath=pdfFileAbsPath;
	}
	
	String branchId;
	String htmlFileContentWithParameter,finalHtmlContent,sourceFileContent;
	String absPathOfBarCode;
	String keyPrefix="MQ_";
	public String getKeyPrefix() {
		return keyPrefix;
	}
	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}
	String barCodeContent;
	MultiValuedMap<String,String> keyValueOfTemplate=new ArrayListValuedHashMap<String, String>();
	public boolean isCbsFilePresentInTheSource;
	
	LinkedHashMap<String,Object> transactionVariableList=new LinkedHashMap<String, Object>();
	public Map<String, Object> getTransactionVariableList() {
		return transactionVariableList;
	}
	public void setTransactionVariableList(LinkedHashMap<String, Object> transactionVariableList) {
		this.transactionVariableList = transactionVariableList;
	}
	List listOfAllWordsInSourceFile,listOfEachLinesInFile,listOfAllSourceWordsInEachLine,listOfAllWordsInTemplateFile,listOfUniqueRecordsInSourceFile,listOfFinalHtmlLinesForTabular;
	
	
	

	public List getListOfFinalHtmlLinesForTabular() {
		return listOfFinalHtmlLinesForTabular;
	}
	public void setListOfFinalHtmlLinesForTabular(List listOfFinalHtmlLinesForTabular) {
		this.listOfFinalHtmlLinesForTabular = listOfFinalHtmlLinesForTabular;
	}
	public boolean isCbsFilePresentInTheSource() {
		return isCbsFilePresentInTheSource;
	}
	public void setCbsFilePresentInTheSource(boolean isCbsFilePresentInTheSource) {
		
		if(isCbsFilePresentInTheSource)
		this.keyPrefix="CBS_";
		
		this.isCbsFilePresentInTheSource = isCbsFilePresentInTheSource;
	}
	public MultiValuedMap<String,String>  getKeyValueOfTemplate() {
		return keyValueOfTemplate;
	}
	public void setKeyValueOfTemplate(String key,String value) {
		//this.keyValueOfTemplate = keyValueOfTemplate;
		keyValueOfTemplate.put(key, value);
	}
	
	public List getListOfUniqueRecordsInSourceFile() {
		return listOfUniqueRecordsInSourceFile;
	}
	public void setListOfUniqueRecordsInSourceFile(List listOfUniqueRecordsInSourceFile) {
		this.listOfUniqueRecordsInSourceFile = listOfUniqueRecordsInSourceFile;
	}
	
	public List getListOfAllWordsInSourceFile() {
		return listOfAllWordsInSourceFile;
	}
	public void setListOfAllWordsInSourceFile(List listOfAllWordsInSourceFile) {
		this.listOfAllWordsInSourceFile = listOfAllWordsInSourceFile;
	}

	public List getListOfAllWordsInTemplateFile() {
		return listOfAllWordsInTemplateFile;
	}
	public void setListOfAllWordsInTemplateFile(List listOfAllWordsInTemplateFile) {
		this.listOfAllWordsInTemplateFile = listOfAllWordsInTemplateFile;
	}
	public List getListOfEachLinesInFile() {
		return listOfEachLinesInFile;
	}
	public void setListOfEachLinesInFile(List listOfEachLinesInFile) {
		this.listOfEachLinesInFile = listOfEachLinesInFile;
	}
	public List getListOfAllWordsInEachLine() {
		return listOfAllSourceWordsInEachLine;
	}
	public void setListOfAllWordsInEachLine(String entireFileContent) {
		
		listOfAllSourceWordsInEachLine = new ArrayList<String>();
		
		this.listOfAllSourceWordsInEachLine = listOfAllSourceWordsInEachLine;
	}
	public String getSourceFileContent() {
		return sourceFileContent;
	}
	public void setSourceFileContent(String sourceFileContent) {
		this.sourceFileContent = sourceFileContent;
	}
	public String getHtmlFileContentWithParameter() {
		return htmlFileContentWithParameter;
	}
	public void setHtmlFileContentWithParameter(String htmlFileContentWithParameter) {
		this.htmlFileContentWithParameter = htmlFileContentWithParameter;
	}
	public String getFinalHtmlContent() {
		return finalHtmlContent;
	}
	public void setFinalHtmlContent(String finalHtmlContent) {
		this.finalHtmlContent = finalHtmlContent;
	}
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	
StatusType jobStatus;
PrinterJobType printerJobType;
	



	public PrinterJobType getPrinterJobType() {
	return printerJobType;
}
public void setPrinterJobType(PrinterJobType printerJobType) {
	this.printerJobType = printerJobType;
}
	public StatusType getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(StatusType jobStatus) {
		this.jobStatus = jobStatus;
	}

String templateDescription;

public String getTemplateDescription() {
	return templateDescription;
}
public void setTemplateDescription(String templateDescription) {
	this.templateDescription = templateDescription;
}



}
