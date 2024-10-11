package com.jlp.pmu.service.impl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jlp.pmu.dto.LoginActivityResponse;
import com.jlp.pmu.dto.LoginResponse;
import com.jlp.pmu.dto.UserResponse;
import com.jlp.pmu.models.LoginActivity;
import com.jlp.pmu.models.Roles;
import com.jlp.pmu.pojo.UserDetails;
import com.jlp.pmu.repository.LoginActivityRepository;
import com.jlp.pmu.service.JWTService;
import com.jlp.pmu.service.LoginService;
import com.jlp.pmu.service.UserService;
import com.jlp.pmu.utility.JwtTokenUtil;

import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;



@Service
@Log4j2
public class LoginServiceImpl implements LoginService{
	@Autowired
	JWTService jwtService;
	@Autowired 
	JwtTokenUtil jwtTokenUtil;
	@Autowired
	LoginActivityRepository loginActivityRepository;
	@Autowired
	ModelMapper modelMapper;
	@Autowired
	UserService userService;
	@Autowired
	com.jlp.pmu.utility.PMUUtility PMUUtility;
	@Value("${redirect-uri}")
	private String redirectURL;
	@Value("${client-secret}")
	private String clientSecret;
	@Value("${client-id}")
	private String clientID;
	@Value("${token-url}")
	private String tokenURL;
	@Value("${session-management-uri}")
	private String sessionManagementURI;
	
	//public static Properties prop =null;
	
	
	public LoginResponse LoginProcessRedirect(String code) {
		//prop = PMUUtility.getAllProperties();
		LoginResponse loginResponse = new LoginResponse();
		try {
			String accessToken = getAccessToken(code);
			System.out.println("accessToken :"+accessToken);
			UserDetails userDetails = jwtService.generateUserFromToken(accessToken);
			List<UserResponse> users = userService.getAllUserDeatilsByuserID(userDetails.getUserName());
			if(users != null && users.size() > 0) {
			Set<Roles> roles = users.get(0).getRoles();
			List<String> Adgroups =  getAdGroupsForUser(userDetails.getAdLists(),roles);
			addLoginActivity(userDetails);
			if(Adgroups.size() > 0) {
				loginResponse.setADGroups(Adgroups);
				loginResponse.setSuccess(true);
				loginResponse.setUserName(userDetails.getUserName());
				loginResponse.setEmailID(userDetails.getEmailID());
				loginResponse.setFullName(userDetails.getFirstName()+" "+userDetails.getLastName());
				String JWTTokwn  = jwtTokenUtil.generateToken(userDetails);
				loginResponse.setJWTToken(JWTTokwn);
			}else {
				loginResponse.setSuccess(false);
				loginResponse.setErroMessage("AD group is not mapped for this user, Please contact system administrator");
			}}else {
				loginResponse.setSuccess(false);
				loginResponse.setErroMessage("User group not mapped in PMU, Please contact system administrator");
			}
		}catch(Exception e) {
			loginResponse.setSuccess(false);
			loginResponse.setErroMessage("error occured in login process:"+e.getMessage());
		}
		return loginResponse;
	}
	
	List<String> getAdGroupsForUser(List<String> AdGroupsFromAPI,Set<Roles> roles){
		//prop = PMUUtility.getAllProperties();
		List<Roles> adGroups = new ArrayList<Roles>();
		List<String> adRoles = new ArrayList<String>();
		Iterator<Roles> iterate = roles.iterator();
		HashMap<String, Integer> map = new HashMap<>();
		map.put("ADMIN", 2);
        map.put("MODERATOR", 1);
        map.put("USER",0);
	    while(iterate.hasNext()) {
	    	Roles role = iterate.next();
	    	String roleType =  role.getRoleTypes().toString();
	    		if(AdGroupsFromAPI.contains(roleType)) {
	    			adGroups.add(role);
	    			//adRoles.add(roleType);
	    		}
			} 
			if (adGroups.size() > 1) {
				int privelage = 0;
				for (Roles temp : adGroups) {
					String roleType = temp.getRoleTypes().toString();
					if (adRoles.size() > 0) {
						int tempPrivelage = map.get(roleType);
						if(tempPrivelage > privelage) {
							adRoles = new ArrayList<String>();
							privelage = map.get(roleType);
							adRoles.add(roleType);
						}
			
					}else {
						privelage = map.get(roleType);
						adRoles.add(roleType);
					}
				}
			} else if (adGroups.size() > 0) {
				adRoles.add(adGroups.get(0).getRoleTypes().toString());
			}
				 
		return adRoles;
	}
	
	public String LoginRedirectService(String code) {
		//prop = PMUUtility.getAllProperties();
		String responseString ="";	
		String accessToken = getAccessToken(code);
		UserDetails userDetails = jwtService.generateUserFromToken(accessToken);
		String JWTTokwn  = jwtTokenUtil.generateToken(userDetails);
		addLoginActivity(userDetails);
		System.out.println("JWTTokwn :"+JWTTokwn);
		return JWTTokwn;
	}
	
	
	public String getAccessToken(String code) {
		//prop = PMUUtility.getAllProperties();
		String accessToken = "";
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<String, String>();
		formData.add("code", code);
		formData.add("grant_type", "authorization_code");
		formData.add("redirect_uri",redirectURL);
		String auth = clientID+":"+clientSecret;
		byte[] encodeAuth = java.util.Base64.getEncoder().encode(auth.getBytes());
		String authHeader = "Basic "+new String(encodeAuth);
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", authHeader);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> requestEntity =  new HttpEntity<>(formData,headers);
		RestTemplate restTemplate = new RestTemplate();
		 ResponseEntity<String> responseEnity = restTemplate.exchange(tokenURL, HttpMethod.POST,requestEntity,String.class);
		 HttpStatusCode statusCode = responseEnity.getStatusCode();
		 if(statusCode == HttpStatus.OK) { 
			 
			 JsonParser jsonParser = new JsonParser();
			 JsonObject jsonObj =(JsonObject)jsonParser.parse(responseEnity.getBody().toString());
			 accessToken = jsonObj.get("access_token").toString();
			// accessToken = respObj.toString();
			}else {
			 return null;
		 }
		return accessToken;
	}
	@Override
	public LoginActivityResponse addLoginActivity(UserDetails loginActivityRequest) {
		//prop = PMUUtility.getAllProperties();
		LoginActivity loginActivity = new LoginActivity();
		loginActivity.setUserId(loginActivityRequest.getUserName());
		loginActivity.setLoggedInTime(LocalDateTime.now());
		LoginActivity response = loginActivityRepository.save(loginActivity);
		return modelMapper.map(response,LoginActivityResponse.class);
	}
	
	
	public String callSessionRevoke(String jwtToken) {
		Claims testclaims = jwtTokenUtil.getAllClaimsFromToken(jwtToken, null);
		System.out.println("testclaims :"+testclaims);
		System.out.println(" and testclaims :"+testclaims.get("userdetails"));
		ObjectMapper mapper = new ObjectMapper();
		UserDetails userdetails = mapper.convertValue(testclaims.get("userdetails"), UserDetails.class);
		System.out.println(" and code :"+userdetails.getPingSRI());
		String response = logoutAPI(userdetails.getPingSRI());
		return response;
	}
	
	
	 public String logoutAPI(String pingSRI) {
	        try {
	            String tokenURL = sessionManagementURI + pingSRI + "/revoke";
	            String auth = clientID+":"+clientSecret;
	            String authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

	            HttpHeaders headers = new HttpHeaders();
	            headers.setContentType(MediaType.APPLICATION_JSON);
	            headers.set("X-XSRF-Header", "PingFederate");
	            headers.set("Authorization", authHeader);
	            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
	            RestTemplate restTemplate = new RestTemplate();
	            ResponseEntity<String> responseEntity = restTemplate.exchange(tokenURL, HttpMethod.POST, requestEntity, String.class);
	            HttpStatusCode statusCode = responseEntity.getStatusCode();
	            System.out.println("StatusCode: {}"+statusCode);
	            if(statusCode == HttpStatus.OK) { 
	            	
	            }
	            return responseEntity.getBody().toString();
	        } catch (Exception e) {
	        	System.out.println("Exception occurred: {}"+e.getMessage());
	        	return "error occured";
	        }
	    }
	 
	 public String refreshJWTTokens(String jwtToken,Claims testclaims) {
		 return jwtTokenUtil.refreshToken(jwtToken, testclaims);
	 }
	 
	 

}
