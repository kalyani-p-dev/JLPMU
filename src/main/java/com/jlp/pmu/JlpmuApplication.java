package com.jlp.pmu;
 
import java.io.IOException;
import java.util.ArrayList;
 
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
 
import com.jlp.pmu.dataingestion.EbcdicToAscii;
import com.jlp.pmu.dataingestion.MessageProcessor;
 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
 
@SpringBootApplication
@EnableScheduling
public class JlpmuApplication {
	private static final Logger LOGGER = LogManager.getLogger(JlpmuApplication.class);
 
	public static void main(String[] args) throws IOException {
		SpringApplication.run(JlpmuApplication.class, args);
		System.out.println("Starting of PMU server CCC");
		LOGGER.info("Info level log message");
 
 
	}
 
}