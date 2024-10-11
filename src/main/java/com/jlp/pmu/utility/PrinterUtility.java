package com.jlp.pmu.utility;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.MediaSizeName;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.jlp.pmu.dataingestion.MessageProcessor;
import com.jlp.pmu.enums.PrinterJobStatus;
import com.jlp.pmu.enums.PrinterJobType;
import com.jlp.pmu.enums.StatusType;
import com.jlp.pmu.models.Printer;
import com.jlp.pmu.models.Report;
import com.jlp.pmu.models.Template;
import com.jlp.pmu.pojo.ReportMetaData;
import com.jlp.pmu.repository.PrinterRepository;
import com.jlp.pmu.repository.ReportRepository;
import com.jlp.pmu.repository.TemplateRepository;
import com.jlp.pmu.service.JobDetailsService;
import com.jlp.pmu.service.impl.PrinterServiceImpl;
import com.jlp.pmu.service.impl.ReportSeviceImpl;



@Service



public class PrinterUtility {
	
	@Autowired
    JobDetailsService jobDetailsService;
    
    @Autowired
       ReportRepository reportRepository;
    
       @Autowired
       PrinterRepository printerRepository;
       
       @Autowired
       ReportSeviceImpl reportSeviceImpl;
       
       @Autowired
       PrinterServiceImpl printerSeviceImpl;
       
       @Autowired
       TemplateRepository templateRepository;
	
	 HashMap<String,PrintService> printerServicesMapObj=new HashMap<String,PrintService> ();
	public ReportMetaData repMetaDataObj;
	Utility utilObj;
	
	public void initializePrinterProperties()
	{
		System.out.println("Inside  initializePrinterProperties");
		
		
		utilObj = new Utility();
		
		
		
		
		
		if(printerServicesMapObj.size()==0&&PrinterJob.lookupPrintServices().length>0)
		{
			  PrintService[] services = PrinterJob.lookupPrintServices();
	            
	            

	            for(int i = 0 ;i <services.length;i++) {
	            printerServicesMapObj.put(services[i].getName(), services[i]);            	
		}
		}
	}
	
	public String getPrinterName(long branchCode,String reportName,String dept,String subDept)
	{
		String printerName="";
	
		
		System.out.println("branchCode inside  getPrinterName =="+branchCode);
		System.out.println("branchCode inside  reportName =="+reportName);
		
		if(StringUtils.isEmpty(dept))
			dept="EMPTY";	
		
		if(StringUtils.isEmpty(subDept))
			subDept="EMPTY";	
		
		
		Optional<Template> tepminfo=templateRepository.findBytemplateName(reportName+".html");
        if(tepminfo.isPresent()) {
            String tempDescription=tepminfo.get().getTemplateDescription();
            repMetaDataObj.setTemplateDescription(tempDescription);
        }
		
		
		repMetaDataObj.setNoOfCopies(1);
			Optional<Report> optionalReport = reportRepository.findReportByReportNameAndBranchCode(true,reportName,branchCode,dept,subDept);
 
			if (optionalReport.isPresent()) {
				System.out.println("Unable to locate printer name...");
				long printerId = optionalReport.get().getPrinterId();
				
				//optionalReport.get().getPrintjoboptions()
			
				repMetaDataObj.setPrinterId(printerId);
				repMetaDataObj.setNoOfCopies(optionalReport.get().getCopies());
				
				
				repMetaDataObj.setHold(optionalReport.get().getPrintjoboptions());
				
				repMetaDataObj.setPrinterName(printerName);
				repMetaDataObj.setDepartment(dept);
				
				
				Optional<Printer> printerInfo = printerRepository.findById(printerId);
				
				

				if (printerInfo.isPresent()) {
					
					

					if (printerInfo.get().getRedirectExist()) {
						printerName = printerInfo.get().getRedirectPrintName();
					} else {
						printerName = printerInfo.get().getPrinterName();
					}
				//	repMetaDataObj.setHold(optionalReport.get().);
				}
				System.out.println("printerName==" + printerName);

			}
     
      
      
      
      
    
     

		
		
		return printerName;
	}

	
	
	
	
	@Scheduled(fixedRate = 10000)
	public void retryPrintJobs()
	{
		File directory = new File("/pmu/dev/retry/"); 
		String absPthOfRetryFile;
		  
        // Create an object of Class MyFilenameFilter 
        // Constructor with name of file which is being 
        // searched 
		
		try
		{
			MessageProcessor messageProcessorObj = new MessageProcessor();
			
			// with/without extension 
	        String[] flist = directory.list() ;
	  
	        // Empty array 
	       
	  
	            // Print all files with same name in directory 
	            // as provided in object of MyFilenameFilter 
	            // class 
	            for (int i = 0; i < flist.length; i++) { 
	               // System.out.println(flist[i]+" found"); 
	            	absPthOfRetryFile ="/pmu/dev/retry/"+ flist[i];
	            	if(StringUtils.endsWithIgnoreCase(absPthOfRetryFile, "_3"))
	            		continue;
	            	messageProcessorObj.processInputFile(absPthOfRetryFile);
	               
	            
	        } 
	            
			
			
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
  
        // store all names with same name  
        

	}
	
	
	
	synchronized public PrinterJobStatus startPrintJob(String absPathOfFileToBePrinted,String printerName)
	{
		System.out.println("Inside startPrintJob absPathOfFileToBePrinted ="+absPathOfFileToBePrinted+" printerName="+printerName);
        PrinterJobStatus printJobStatus=PrinterJobStatus.DATA_TRANSFER_COMPLETE;
		String printStatus = null;
			
            PrintRequestAttributeSet requestAttributeSet = new HashPrintRequestAttributeSet();

            requestAttributeSet.add(MediaSizeName.ISO_A4);

            requestAttributeSet.add(new JobName("JLP", Locale.ENGLISH));

            PrintService defaultService = printerServicesMapObj.get(printerName) ;
            if(defaultService.getAttributes()==null)
            
            	printJobStatus=	PrinterJobStatus.PRINTER_SERVICE_UNAVAILABLE;
            else
            {
           
           
            

            
            	try {
           
            FileInputStream fis = new FileInputStream(absPathOfFileToBePrinted);
            Doc printDoc;
            
            if(StringUtils.containsAnyIgnoreCase(absPathOfFileToBePrinted, "zpl"))
            	printDoc = new SimpleDoc(utilObj.readEntireFileIntoString(absPathOfFileToBePrinted).getBytes(), DocFlavor.INPUT_STREAM.AUTOSENSE, null);
            else
            	
             printDoc = new SimpleDoc(fis, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
            
            //Doc doc = new SimpleDoc(zpl_data.getBytes(), flvr, null);
            DocPrintJob job = defaultService.createPrintJob();
            
            PrintStatus monitor = new PrintStatus();

           // repMetaDataObj.setPrintJobId(job.hashCode());
            //repMetaDataObj.getTransactionVariableList().put("printerJob", job.hashCode());

            job.addPrintJobListener(monitor);

            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
            
            int numCopies=1;
            repMetaDataObj.setNoOfCopies(numCopies);
            
            attributes.add(new Copies(numCopies));
          
                 job.print(printDoc, null);
                 
                 printJobStatus=monitor.printStatusFromListner;
                 
                 printStatus=printJobStatus.name();
                 
                 
                 if(StringUtils.containsIgnoreCase(printStatus, "fail")||StringUtils.containsIgnoreCase(printStatus, "unavail"))
                		 printJobStatus=PrinterJobStatus.JOB_FAILED;

            
            fis.close();
            
           
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
 		 
          
            repMetaDataObj.setPrinterJobStatus(printJobStatus);

                 } catch (Exception e) {
                	 System.out.println("Error during printing the file .."+absPathOfFileToBePrinted+" in the printer "+printerName);
                	 printJobStatus=	PrinterJobStatus.JOB_FAILED;
            e.printStackTrace();

                 }
        }
            
            
            
            if(StringUtils.containsIgnoreCase(printStatus, "fail")||StringUtils.containsIgnoreCase(printStatus, "unavail"))
            {
           		 printJobStatus=PrinterJobStatus.JOB_NOT_COMPLETE;
           		 repMetaDataObj.setJobStatus(StatusType.TODO);
           		 repMetaDataObj.setPrinterJobStatus(printJobStatus);
           		// repMetaDataObj.setPrinterJobType(PrinterJobType.HOLD);
           		 
            }
            else
            {
            	 repMetaDataObj.setJobStatus(StatusType.DONE);
           		 repMetaDataObj.setPrinterJobStatus(PrinterJobStatus.JOB_COMPLETE);
           		 repMetaDataObj.setPrinterJobType(PrinterJobType.PRINT);
           		
            }
      
            LocalDateTime endTimeOfProcessing = LocalDateTime.now();  

    		long branchIdLongValue, printerJobId;
    		branchIdLongValue = Integer.parseInt(repMetaDataObj.getBranchId());
    //printerJobId = Integer.parseInt(repMetaDataObj.getPrintJobId());

    //int pageCount=StringUtils.indexOfAny(repMetaDataObj.getFinalHtmlContent(), "page-break");
    //repMetaDataObj.getTransactionVariableList().put("pages", pageCount);
    		repMetaDataObj.setEndTime(endTimeOfProcessing);
           // repMetaDataObj.getTransactionVariableList().put("endTime", repMetaDataObj.getEndTime());
            repMetaDataObj.setHold(false);
            

    return printJobStatus;


	}	
	

}
