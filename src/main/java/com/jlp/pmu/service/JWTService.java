package com.jlp.pmu.service;

import java.util.Map;

import com.jlp.pmu.pojo.UserDetails;

import io.jsonwebtoken.Claims;

public interface JWTService {
	public Map<String, Object> getUserDetailsFromToken(String token) throws Exception;
	public UserDetails generateUserFromToken(String token);
	public String extractUsername(String token);
	public Boolean validateToken(String token, String username);
	public String generateJWTToken(UserDetails userDetails);
	

}
