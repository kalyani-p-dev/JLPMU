package com.jlp.pmu.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import org.apache.commons.lang3.StringUtils;

import com.jlp.pmu.utility.Utility;

public class MyEventListener implements MessageListener {
	
	int fileCount = 1;
	
	Utility utilObj =null;

	  @Override
	  public void onMessage(Message message) {
		  System.out.println("onMessage 1");
	    try {
	      if (message instanceof TextMessage) {
	    	  System.out.println("onMessage 2");
	        TextMessage textMessage = (TextMessage) message;
	        String stringMessage = textMessage.getText();
	        saveTextToFile(stringMessage,"mqFile1"+fileCount);
	        fileCount = fileCount+1;
	        //do something with your message from queue
	        System.out.println("onMessage 3 ");
	        System.out.println("message received :"+stringMessage);
	      }
	    } catch (JMSException e) {
	    	 System.out.println("Exception in onMessage :"+e);
	    }
	  }
	  
	  private void saveTextToFile(String text, String fileName) {
		  	System.out.println("saveTextToFile:"+fileName);
	        String filePathAndName = "/home/J84514553UA/pmu/input/" + fileName+ ".PROCESSED";
	        utilObj = new Utility();
	        utilObj.createFile(filePathAndName, text);
			/*
			 * ClassLoader classLoader = getClass().getClassLoader(); File file = new
			 * File(classLoader.getResource(".").getFile() + filePathAndName );
			 * System.out.println("file filepath :"+file.getPath()); FileWriter fileWriter =
			 * null; try { fileWriter = new FileWriter(file); PrintWriter printWriter = new
			 * PrintWriter(fileWriter); printWriter.print(text); printWriter.close(); }
			 * catch (IOException e) {
			 * System.out.println("IOException in saveTextToFile:"+e.getMessage());
			 * e.printStackTrace(); }catch (Exception e) {
			 * System.out.println("Exception in saveTextToFile:"+e.getMessage());
			 * e.printStackTrace(); }
			 */
	    }
	  
	  private String checkMFTReportType(String absPathOfEbcdiFile) {
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
				e.printStackTrace();
			}
	 
			return headerValue;
		}
	  
	  	
}