package com.jlp.pmu.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LoginActivityRequest {
	private String userId;
	private LocalDateTime loggedInTime;
	}
