package com.jlp.pmu.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class LoginActivityResponse {
    private Long id; 
    private String userId; 
    
}
