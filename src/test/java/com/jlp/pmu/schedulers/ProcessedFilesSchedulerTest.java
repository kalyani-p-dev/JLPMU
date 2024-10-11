package com.jlp.pmu.schedulers;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jlp.pmu.utility.PMUUtility;

@ExtendWith(MockitoExtension.class)
public class ProcessedFilesSchedulerTest {

    @Mock
    private PMUUtility pmuUtility;

    @Test
    public void testProcessedFilesCleaning() {
        // Mocking properties
        Properties prop = new Properties();
        prop.setProperty("SourceDirectoryOfProcessedFiles", "C:\\PMURewrite\\processedFiles\\MFT processed files\\AB Branch\\PrintOutputGenerator.java");
        prop.setProperty("ProcessedFilesCleaningTime", "7");
        prop.setProperty("ProcessedFilesExtension", ".java");

        when(pmuUtility.getAllProperties()).thenReturn(prop);

        // Create an instance of ProcessedFilesScheduler
        ProcessedFilesScheduler scheduler = new ProcessedFilesScheduler();
        scheduler.PMUUtility = pmuUtility;

        // Call the method to be tested
        scheduler.processedFilesCleaning();

        // Add assertions here to verify the behavior of the method
        // For example, you can check if files were deleted or not based on the test scenario
        
        Path sourcePath = Path.of("C:\\PMURewrite\\processedFiles\\");
        try (Stream<Path> files = Files.list(sourcePath)) {
            boolean filesExist = files.anyMatch(file -> file.toString().endsWith(".java"));
            assertFalse(filesExist, "Files with .Java extension should have been deleted");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    @Test
    public void testBackupOfProcessedFiles() {
    	Properties prop = new Properties();
        prop.setProperty("SourceOfBackupProcessedFiles", "C:\\PMURewrite\\SourceOfBackupProcesseFile");
        prop.setProperty("DestinationOfBackupProcesseFile", "C:\\PMURewrite\\DestinationOfBackupProcesseFile");
        Path destinationPath = Paths.get("C:\\PMURewrite\\DestinationOfBackupProcesseFile");
    
    PMUUtility mockUtility = mock(PMUUtility.class);
    when(mockUtility.getAllProperties()).thenReturn(new Properties());

    ProcessedFilesScheduler scheduler = new ProcessedFilesScheduler();
    scheduler.PMUUtility = mockUtility;

    // Test backupOfProcessedFiles method
    assertDoesNotThrow(() -> scheduler.backupOfProcessedFiles());
    // Assert that the file was moved successfully
    assertTrue(Files.exists(destinationPath));
}
}

