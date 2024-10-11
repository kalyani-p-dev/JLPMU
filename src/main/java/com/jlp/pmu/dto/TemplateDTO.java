package com.jlp.pmu.dto;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class TemplateDTO {
	private int tempId;
    private String templateName;
    private String templateDescription;
    private LocalDateTime dateTime;
    //private String date;
    //private String Time;
    private long fileSize;

}
