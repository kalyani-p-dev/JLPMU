package com.jlp.pmu.schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.jlp.pmu.utility.Utility;

import lombok.extern.log4j.Log4j2;

@Configuration
@EnableScheduling
@Log4j2
public class ProcessedFilesScheduler {

	@Autowired
	com.jlp.pmu.utility.PMUUtility PMUUtility;
	public static Properties prop = null;
	Utility utilObj =null;

	@Scheduled(cron = "0 0/5 * * * ?") // 20 seconds
	public void processedFilesCleaning() {
		System.out.println("Ruuning at :: " + LocalDateTime.now());
		//prop = PMUUtility.getAllProperties();
		prop=Utility.readPropertiesFile();
		try {
			String SourceDirectoryOfProcessedFiles = prop.getProperty("SourceDirectoryOfProcessedFiles");
			String days = prop.getProperty("ProcessedFilesCleaningTime");
			int numberDays = Integer.parseInt(days);
			String sourceDirectory = SourceDirectoryOfProcessedFiles;

			Path sourcePath = Paths.get(sourceDirectory);

			Files.walk(sourcePath).filter(Files::isRegularFile).forEach(file -> {
				long diff = new Date().getTime() - file.toFile().lastModified();
				String inputFileExtension = PMUUtility.getFileExtension(file.toFile());
				String validFileExtension = prop.getProperty("ProcessedFilesExtension");
				/*if (validFileExtension.equals(inputFileExtension) && diff > numberDays * 24 * 60 * 60 * 1000) {
					file.toFile().delete();
				}*/
				if (validFileExtension.contains(inputFileExtension) && diff > numberDays * 24 * 60 * 60 * 1000) {
					file.toFile().delete();
				}

			});
			log.info("Successfully Clean the old Processed Files from: {}", SourceDirectoryOfProcessedFiles);

		} catch (IOException e) {
			e.printStackTrace();
			log.info("Error moving files: {}", e.getMessage());
		}
	}

	@Scheduled(cron = "0 0/5 * * * ?") // 30 seconds
	public void backupOfProcessedFiles() {
		//prop = PMUUtility.getAllProperties();
		prop=Utility.readPropertiesFile();
		try {
			String days = prop.getProperty("BackupOfProcessedFilesTime");
			int numberDays = Integer.parseInt(days);
			
			String sourceOfBackupProcessedFiles = prop.getProperty("SourceOfBackupProcessedFiles");
			String destinationOfBackupProcesseFile = prop.getProperty("DestinationOfBackupProcesseFile");

			Path sourcePath = Paths.get(sourceOfBackupProcessedFiles);
			Path destinationPath = Paths.get(destinationOfBackupProcesseFile);

			Files.walk(sourcePath).filter(Files::isRegularFile).forEach(file -> {
				try {
					Path relativePath = sourcePath.relativize(file);
					Path destinationFile = destinationPath.resolve(relativePath);
					long diff = new Date().getTime() - file.toFile().lastModified();
					
					String inputFileExtension = PMUUtility.getFileExtension(file.toFile());
					String validFileExtension = prop.getProperty("ProcessedFilesExtension");
					if (validFileExtension.contains(inputFileExtension) && diff > numberDays * 24 * 60 * 60 * 1000) {
						Files.move(file, destinationFile, StandardCopyOption.REPLACE_EXISTING);
					}

					
					log.info("File moved successfully: " + file);
				} catch (IOException e) {
					e.printStackTrace();
					log.info("Error moving the file: " + e.getMessage());
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
			log.info("Error moving files: " + e.getMessage());
		}
	}

}
