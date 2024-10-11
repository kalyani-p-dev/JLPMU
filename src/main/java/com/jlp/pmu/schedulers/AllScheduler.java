package com.jlp.pmu.schedulers;

import java.time.LocalDateTime;
import java.util.Properties;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.jlp.pmu.utility.Utility;

import lombok.extern.log4j.Log4j2;

@Configuration
@EnableScheduling
@Log4j2
public class AllScheduler {
	
	
	public static Properties prop = null;
	
	
	@Scheduled(cron = "0 0/1 * * * ?")
	 public void moveMQFiles() {
	  System.out.println("In AllScheduler of moveMQFiles "+ LocalDateTime.now());
	  log.info("In AllScheduler of moveMQFiles "+ LocalDateTime.now());
	  try {
	  prop=Utility.readPropertiesFile();
	   String sourceDirectory = prop.getProperty("SourceDirectoryOfProcessedFilesMQ");
	   String destinationDirectory = prop.getProperty("DestDirectoryOfProcessedFilesMQ");

	   Path sourcePath = Paths.get(sourceDirectory);
	   Path destinationPath = Paths.get(destinationDirectory);

	   Files.walk(sourcePath).filter(Files::isRegularFile).forEach(file -> {
	    try {
	     Path relativePath = sourcePath.relativize(file);
	     Path destinationFile = destinationPath.resolve(relativePath);
	     System.out.println("sourcePath:"+sourcePath);
		   System.out.println("destinationPath:"+destinationPath);
	     Files.move(file, destinationFile, StandardCopyOption.REPLACE_EXISTING);
	     System.out.println("AllScheduler moveMQFiles,File moved successfully: " + file);
	     log.info("AllScheduler moveMQFiles,File moved successfully: " + file);
	    } catch (IOException e) {
	     e.printStackTrace();
	     System.err.println("AllScheduler moveMQFiles,Error moving the file: " + e.getMessage());
	     log.error("AllScheduler moveMQFiles,Error moving the file : "+e.getMessage());
	    }
	   });

	  } catch (IOException e) {
	   e.printStackTrace();
	   System.err.println("AllScheduler moveMQFiles,Error moving files: " + e.getMessage());
	  }
	 }
	
	@Scheduled(cron = "0 0/1 * * * ?")
	 public void moveCBSFiles() {
	  System.out.println("In AllScheduler of moveFiles moveCBSFiles:"+ LocalDateTime.now());
	  log.info("In AllScheduler of moveFiles moveCBSFiles:"+ LocalDateTime.now());
	  try {
	  prop=Utility.readPropertiesFile();
	   String sourceDirectory = prop.getProperty("SourceDirectoryOfProcessedFilesCBS");
	   String destinationDirectory = prop.getProperty("DestDirectoryOfProcessedFilesCBS");

	   Path sourcePath = Paths.get(sourceDirectory);
	   Path destinationPath = Paths.get(destinationDirectory);
	   System.out.println("sourcePath:"+sourcePath);
	   System.out.println("destinationPath:"+destinationPath);
	   Files.walk(sourcePath).filter(Files::isRegularFile).forEach(file -> {
	    try {
	     Path relativePath = sourcePath.relativize(file);
	     Path destinationFile = destinationPath.resolve(relativePath);

	     Files.move(file, destinationFile, StandardCopyOption.REPLACE_EXISTING);
	     System.out.println("AllScheduler moveCBSFiles,File moved successfully: " + file);
	     log.info("AllScheduler moveCBSFiles ,File moved successfully: " + file);
	    } catch (IOException e) {
	     e.printStackTrace();
	     System.err.println("AllScheduler moveCBSFiles,Error moving the file: " + e.getMessage());
	     log.error("AllScheduler moveCBSFiles,Error moving the file : "+e.getMessage());
	    }
	   });

	  } catch (IOException e) {
	   e.printStackTrace();
	   System.err.println("AllScheduler moveCBSFiles ,Error moving files: " + e.getMessage());
	  }
	 }



}
