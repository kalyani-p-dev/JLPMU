package com.jlp.pmu.dto;

import java.util.List;

import lombok.Data;

@Data
public class BaseResponse {
  
	private String JWTToken;
	private List<String> ADGroups;
	private boolean isSuccess;
	private String ErroMessage;
	private String EmailID;
	private String userName;
	private String fullName;
}
