package com.jlp.pmu.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TemplateUpdateResponse {
	private int tempId;
    private String templateName;
    private String templateDescription;
    private LocalDateTime dateTime;
    private long fileSize;

}
