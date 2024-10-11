package com.jlp.pmu.service;

import com.jlp.pmu.dto.LoginActivityRequest;
import com.jlp.pmu.dto.LoginActivityResponse;
import com.jlp.pmu.dto.LoginResponse;
import com.jlp.pmu.pojo.UserDetails;

import io.jsonwebtoken.Claims;

public interface LoginService {
	
	 String getAccessToken(String code);
	 public String LoginRedirectService(String code);
	 public LoginActivityResponse addLoginActivity(UserDetails loginActivityRequest);
	 public LoginResponse LoginProcessRedirect(String code);
	 public String callSessionRevoke(String jwtToken);
	 public String refreshJWTTokens(String jwtToken,Claims testclaims);
	
	
	
	
	
	

}
