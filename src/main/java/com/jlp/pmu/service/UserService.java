package com.jlp.pmu.service;

import java.util.List;

import com.jlp.pmu.dto.UserRequest;
import com.jlp.pmu.dto.UserResponse;

public interface UserService {
	UserResponse addUserDetails(UserRequest requestDto);
	
	UserResponse updateUserDetails(UserRequest request);
	List<UserResponse> getAllUserDeatils();
	List<UserResponse> getAllUserDeatilsByuserID(String userid);

}
