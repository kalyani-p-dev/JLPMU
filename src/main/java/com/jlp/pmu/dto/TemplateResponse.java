package com.jlp.pmu.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Data;

@Data
public class TemplateResponse {
	private int tempId;
    private String templateName;
    private String templateDescription;
    //private LocalDateTime dateTime;
    private String date;
    private String Time;
    private long fileSize;
    //private String Message;
}
