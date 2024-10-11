package com.jlp.pmu.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Properties;

import org.springframework.stereotype.Service;

@Service
public class PMUUtility {
	
	public static Properties prop=null;
	
	public String getFileExtension(File file) {
	    String name = file.getName();
	    int lastIndexOf = name.lastIndexOf(".");
	    if (lastIndexOf == -1) {
	        return ""; // empty extension
	    }
	    return name.substring(lastIndexOf);
	}
	
	public static Long convertToTimeStamp(LocalDateTime datTime) {
		try {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//
//            Date parsedDate = dateFormat.parse(dateString);
            Timestamp timestamp = Timestamp.valueOf(datTime);

            Long timestampValue =timestamp.getTime() / 1000;
            
            return timestampValue;
//            System.out.println("timestampValue: " + timestampValue);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
	}
	
public Properties getAllProperties() {
	String fileName="\\pmu\\properties\\jlp.properties";
      FileInputStream fis = null;
      prop = null;
      try {
         fis = new FileInputStream(fileName);
         prop = new Properties();
         prop.load(fis);
         fis.close();
      } catch(FileNotFoundException fnfe) {
         fnfe.printStackTrace();
      } catch(IOException ioe) {
         ioe.printStackTrace();
      } finally {
        
      }
      return prop;
   }

}
