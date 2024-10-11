package com.jlp.pmu.utility;

import java.io.FileInputStream;

import java.io.FileNotFoundException;

import java.io.IOException;

import java.io.InputStream;

import java.util.Locale;

 

import javax.print.Doc;

import javax.print.DocFlavor;

import javax.print.DocPrintJob;

import javax.print.PrintException;

import javax.print.PrintService;

import javax.print.PrintServiceLookup;

import javax.print.SimpleDoc;

import javax.print.attribute.HashPrintRequestAttributeSet;

import javax.print.attribute.PrintRequestAttributeSet;

import javax.print.attribute.standard.JobName;

import javax.print.attribute.standard.MediaSizeName;

import javax.print.event.PrintJobAdapter;

import javax.print.event.PrintJobEvent;

import javax.print.event.PrintJobListener;

import com.jlp.pmu.enums.PrinterJobStatus;

import java.awt.print.PrinterException;

import java.awt.print.PrinterJob;

import java.io.BufferedInputStream;

import java.io.File;

import javax.print.PrintService;

import javax.print.PrintServiceLookup;


 

 

 

class PrintStatus extends PrintJobAdapter {

               

    private boolean completed = false;
    
    
    public PrinterJobStatus printStatusFromListner;
 

    @Override

    public void printJobCanceled(PrintJobEvent pje) {

               

    	 System.out.println("Job printJobCanceled custom "+pje);
    	 System.out.println("Job printJobCanceled custom new DATA_TRANSFER_COMPLETE"+pje.DATA_TRANSFER_COMPLETE);
    	 System.out.println("Job printJobCanceled custom new JOB_CANCELED"+pje.JOB_CANCELED);
    	 System.out.println("Job printJobCanceled custom new JOB_COMPLETE"+pje.JOB_COMPLETE);
    	 System.out.println("Job printJobCanceled custom new JOB_FAILED"+pje.JOB_FAILED);
    	 System.out.println("Job printJobCanceled custom new NO_MORE_EVENTS"+pje.NO_MORE_EVENTS);
    	 System.out.println("Job printJobCanceled custom new REQUIRES_ATTENTION"+pje.REQUIRES_ATTENTION);
    	 
    	 
         
         if(pje.getPrintEventType()==pje.DATA_TRANSFER_COMPLETE)
         {
           	System.out.println("Custom status Data transfer complete");
           	printStatusFromListner = PrinterJobStatus.DATA_TRANSFER_COMPLETE;
           	
         } 
           if(pje.getPrintEventType()==pje.JOB_CANCELED)
           {
              	System.out.println("Custom status Data transfer JOB_CANCELED");
              	printStatusFromListner = PrinterJobStatus.JOB_CANCELED;
              	
            } 

           if(pje.getPrintEventType()==pje.JOB_COMPLETE)
           {
             	System.out.println("Custom status Data transfer JOB_COMPLETE");
             	printStatusFromListner = PrinterJobStatus.JOB_COMPLETE;
             	
           } 

           if(pje.getPrintEventType()==pje.JOB_FAILED)
           {
            	System.out.println("Custom status Data transfer JOB_FAILED");
            	printStatusFromListner = PrinterJobStatus.JOB_FAILED;
            	
          } 
           

           if(pje.getPrintEventType()==pje.NO_MORE_EVENTS)
           {

           	System.out.println("Custom status Data transfer NO_MORE_EVENTS");
           	printStatusFromListner = PrinterJobStatus.NO_MORE_EVENTS;
           	
         } 

           if(pje.getPrintEventType()==pje.REQUIRES_ATTENTION)
           {

              	System.out.println("Custom status Data transfer REQUIRES_ATTENTION");
              	printStatusFromListner = PrinterJobStatus.REQUIRES_ATTENTION;
              	
            } 


        signalCompletion();

    }

 

    @Override

    public void printJobCompleted(PrintJobEvent pje) {

                System.out.println("Job printJobCompleted custom "+pje);
                System.out.println("Job printJobCompleted custom new DATA_TRANSFER_COMPLETE"+pje.DATA_TRANSFER_COMPLETE);
           	 System.out.println("Job printJobCompleted custom new JOB_CANCELED"+pje.JOB_CANCELED);
           	 System.out.println("Job printJobCompleted custom new JOB_COMPLETE"+pje.JOB_COMPLETE);
           	 System.out.println("Job printJobCompleted custom new JOB_FAILED"+pje.JOB_FAILED);
           	 System.out.println("Job printJobCompleted custom new NO_MORE_EVENTS"+pje.NO_MORE_EVENTS);
           	 System.out.println("Job printJobCompleted custom new REQUIRES_ATTENTION"+pje.REQUIRES_ATTENTION);
                
           	 if(pje.getPrintEventType()==pje.DATA_TRANSFER_COMPLETE)
             {
               	System.out.println("Custom status Data transfer complete");
               	printStatusFromListner = PrinterJobStatus.DATA_TRANSFER_COMPLETE;
               	
             } 
               if(pje.getPrintEventType()==pje.JOB_CANCELED)
               {
                  	System.out.println("Custom status Data transfer JOB_CANCELED");
                  	printStatusFromListner = PrinterJobStatus.JOB_CANCELED;
                  	
                } 

               if(pje.getPrintEventType()==pje.JOB_COMPLETE)
               {
                 	System.out.println("Custom status Data transfer JOB_COMPLETE");
                 	printStatusFromListner = PrinterJobStatus.JOB_COMPLETE;
                 	
               } 

               if(pje.getPrintEventType()==pje.JOB_FAILED)
               {
                	System.out.println("Custom status Data transfer JOB_FAILED");
                	printStatusFromListner = PrinterJobStatus.JOB_FAILED;
                	
              } 
               

               if(pje.getPrintEventType()==pje.NO_MORE_EVENTS)
               {

               	System.out.println("Custom status Data transfer NO_MORE_EVENTS");
               	printStatusFromListner = PrinterJobStatus.NO_MORE_EVENTS;
               	
             } 

               if(pje.getPrintEventType()==pje.REQUIRES_ATTENTION)
               {

                  	System.out.println("Custom status Data transfer REQUIRES_ATTENTION");
                  	printStatusFromListner = PrinterJobStatus.REQUIRES_ATTENTION;
                  	
                } 


                

        signalCompletion();

    }

 

    @Override

    public void printJobFailed(PrintJobEvent pje) {

    	  System.out.println("JLP JprintJobFailed event "+pje.getPrintEventType());
    	   System.out.println("Job JprintJobFailed custom new DATA_TRANSFER_COMPLETE"+pje.DATA_TRANSFER_COMPLETE);
         	 System.out.println("Job JprintJobFailed custom new JOB_CANCELED"+pje.JOB_CANCELED);
         	 System.out.println("Job JprintJobFailed custom new JOB_COMPLETE"+pje.JOB_COMPLETE);
         	 System.out.println("Job JprintJobFailed custom new JOB_FAILED"+pje.JOB_FAILED);
         	 System.out.println("Job JprintJobFailed custom new NO_MORE_EVENTS"+pje.NO_MORE_EVENTS);
         	 System.out.println("Job JprintJobFailed custom new REQUIRES_ATTENTION"+pje.REQUIRES_ATTENTION);
    	  
         	 if(pje.getPrintEventType()==pje.DATA_TRANSFER_COMPLETE)
             {
               	System.out.println("Custom status Data transfer complete");
               	printStatusFromListner = PrinterJobStatus.DATA_TRANSFER_COMPLETE;
               	
             } 
               if(pje.getPrintEventType()==pje.JOB_CANCELED)
               {
                  	System.out.println("Custom status Data transfer JOB_CANCELED");
                  	printStatusFromListner = PrinterJobStatus.JOB_CANCELED;
                  	
                } 

               if(pje.getPrintEventType()==pje.JOB_COMPLETE)
               {
                 	System.out.println("Custom status Data transfer JOB_COMPLETE");
                 	printStatusFromListner = PrinterJobStatus.JOB_COMPLETE;
                 	
               } 

               if(pje.getPrintEventType()==pje.JOB_FAILED)
               {
                	System.out.println("Custom status Data transfer JOB_FAILED");
                	printStatusFromListner = PrinterJobStatus.JOB_FAILED;
                	
              } 
               

               if(pje.getPrintEventType()==pje.NO_MORE_EVENTS)
               {

               	System.out.println("Custom status Data transfer NO_MORE_EVENTS");
               	printStatusFromListner = PrinterJobStatus.NO_MORE_EVENTS;
               	
             } 

               if(pje.getPrintEventType()==pje.REQUIRES_ATTENTION)
               {

                  	System.out.println("Custom status Data transfer REQUIRES_ATTENTION");
                  	printStatusFromListner = PrinterJobStatus.REQUIRES_ATTENTION;
                  	
                } 


        signalCompletion();

    }

 

    @Override

    public void printJobNoMoreEvents(PrintJobEvent pje) {

    	//pje.
                System.out.println("JLP Job printJobNoMoreEvents 1 "+pje.getPrintEventType());
                System.out.println("JLP printJobNoMoreEvents event "+pje.getPrintEventType());
         	   System.out.println("Job printJobNoMoreEvents custom new DATA_TRANSFER_COMPLETE"+pje.DATA_TRANSFER_COMPLETE);
              	 System.out.println("Job printJobNoMoreEvents custom new JOB_CANCELED"+pje.JOB_CANCELED);
              	 System.out.println("Job printJobNoMoreEvents custom new JOB_COMPLETE"+pje.JOB_COMPLETE);
              	 System.out.println("Job printJobNoMoreEvents custom new JOB_FAILED"+pje.JOB_FAILED);
              	 System.out.println("Job printJobNoMoreEvents custom new NO_MORE_EVENTS"+pje.NO_MORE_EVENTS);
              	 System.out.println("Job printJobNoMoreEvents custom new REQUIRES_ATTENTION"+pje.REQUIRES_ATTENTION);
              	 if(pje.getPrintEventType()==pje.DATA_TRANSFER_COMPLETE)
                 {
                   	System.out.println("Custom status Data transfer complete");
                   	printStatusFromListner = PrinterJobStatus.DATA_TRANSFER_COMPLETE;
                   	
                 } 
                   if(pje.getPrintEventType()==pje.JOB_CANCELED)
                   {
                      	System.out.println("Custom status Data transfer JOB_CANCELED");
                      	printStatusFromListner = PrinterJobStatus.JOB_CANCELED;
                      	
                    } 

                   if(pje.getPrintEventType()==pje.JOB_COMPLETE)
                   {
                     	System.out.println("Custom status Data transfer JOB_COMPLETE");
                     	printStatusFromListner = PrinterJobStatus.JOB_COMPLETE;
                     	
                   } 

                   if(pje.getPrintEventType()==pje.JOB_FAILED)
                   {
                    	System.out.println("Custom status Data transfer JOB_FAILED");
                    	printStatusFromListner = PrinterJobStatus.JOB_FAILED;
                    	
                  } 
                   

                   if(pje.getPrintEventType()==pje.NO_MORE_EVENTS)
                   {

                   	System.out.println("Custom status Data transfer NO_MORE_EVENTS");
                   	printStatusFromListner = PrinterJobStatus.NO_MORE_EVENTS;
                   	
                 } 

                   if(pje.getPrintEventType()==pje.REQUIRES_ATTENTION)
                   {

                      	System.out.println("Custom status Data transfer REQUIRES_ATTENTION");
                      	printStatusFromListner = PrinterJobStatus.REQUIRES_ATTENTION;
                      	
                    } 


                

        signalCompletion();

    }

 

    private void signalCompletion() {

    	System.out.println("Inside signalCompletion ");

synchronized (PrintStatus.this) {

 

completed = true;

 

PrintStatus.this.notify();

 

}

 

}

 

    public synchronized void waitForJobCompletion() {

 
System.out.println("Inside waitForJobCompletion ");
try {

 

while (!completed) {

 

wait();

 

}

 

} catch (InterruptedException e) {

 

}

    }

 

}

              