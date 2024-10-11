package com.jlp.pmu.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import lombok.Data;

@Data
public class TemplateRequest {
	//private String templateName;
	
	private String description;
	//private String templateDescription;
	//private List<FileDetailsDTO> fileName;
    private MultipartFile file;
	//public LocalDateTime dateTime;
    
		
    

}
