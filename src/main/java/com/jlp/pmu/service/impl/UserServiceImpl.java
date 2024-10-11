package com.jlp.pmu.service.impl;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jlp.pmu.constant.Constant;
import com.jlp.pmu.dto.UserRequest;
import com.jlp.pmu.dto.UserResponse;
import com.jlp.pmu.enums.ObjectType;
import com.jlp.pmu.enums.RoleType;
import com.jlp.pmu.exception.UserAlreadyExistException;
import com.jlp.pmu.exception.UserNotFoundException;
import com.jlp.pmu.models.Activity;
import com.jlp.pmu.models.Branch;
import com.jlp.pmu.models.Roles;
import com.jlp.pmu.models.User;
import com.jlp.pmu.repository.ActivityRepository;
import com.jlp.pmu.repository.BranchRepository;
import com.jlp.pmu.repository.RoleRepository;
import com.jlp.pmu.repository.UserRepository;
import com.jlp.pmu.service.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import lombok.extern.log4j.Log4j2;
@Service
@Log4j2

public class UserServiceImpl implements UserService{
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ActivityRepository activityRepository;
	
	@Autowired
	BranchRepository branchRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	ModelMapper modelMapper;

	@Override
	public UserResponse addUserDetails(UserRequest requestDto) {

		Optional<User> optional = userRepository.findById(requestDto.getUserId());
		
		if(optional.isPresent()) {
			throw new UserAlreadyExistException(Constant.USER_ALREADY_EXIST_WITH_GIVEN_USERID + requestDto.getUserId(),Constant.USER_ALREADY_EXISTS);
		}
		else {
			
			log.info(Constant.ADDING_USER_WITH_THE_DETAILS, requestDto);
			
			User user = new User();
			user.setUserId(requestDto.getUserId());
			user.setFirstName(requestDto.getFirstName());
			user.setLastName(requestDto.getLastName());
			user.setStatus(true);
			user.setUserType(requestDto.getUserType());
			user.setEmailAddress(requestDto.getEmailAddress());
			user.setActiveDirectory(true);
			user.setCreatedBy(user.getFirstName() + " " + user.getLastName());
			user.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
			user.setLastUpdatedTime(LocalDateTime.now());
			user.setComment(Constant.ADDED_BY + user.getFirstName() + " " + user.getLastName());
			
			
			/* Logic for roles*/
			
			List<RoleType> roletypes = requestDto.getRoles();
			
			if(roletypes.contains(RoleType.ADMIN)) {
				user.setPresentInAllBranches(true);
			}
			else {
				user.setPresentInAllBranches(false);
				
				List<String> branchMnemonics = requestDto.getLocation();
				Set<Branch> branches = branchMnemonics.stream()
		                .map(branchMnemonic -> branchRepository.findByMnemonic(branchMnemonic).get())
		                .collect(Collectors.toSet());
		        
		        
		        user.getBranches().clear();
		        
		        user.getBranches().addAll(branches);	        
		        branches.forEach(branch -> branch.getUsers().add(user));
		        
			}

	        Set<Roles> roles = roletypes.stream()
	                .map(roletype -> roleRepository.findByRoleTypes(roletype).get())
	                .collect(Collectors.toSet());
	        
	        
	        user.getRoles().clear();
	        user.getRoles().addAll(roles);

	        
	        /* Logic for location*/
	        
			
	        userRepository.save(user);
			
			Activity activity = new Activity();
			activity.setCreatedBy(user.getFirstName() + " " + user.getLastName());
			activity.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
			activity.setLastupdatedtime(LocalDateTime.now());
			activity.setComment(Constant.ADDED_BY + user.getFirstName() + " " + user.getLastName());

			activity.setObjectType(ObjectType.USER);
			
			activity.setObjectID(requestDto.getUserId());
			
			activityRepository.save(activity);
			
			log.info(Constant.BRANCH_DETAILS_ADDED_SUCCESSFULLY, requestDto.getUserId());
			
			UserResponse response = modelMapper.map(user, UserResponse.class);
			return extractMnemonicFromBranch(user, response);
		}
		
}
	
	@Override
	public UserResponse updateUserDetails(UserRequest request) {
	log.info(Constant.UPDATING_USER_WITH_THE_DETAILS, request);
	
	User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserNotFoundException(
			Constant.USER_NOT_FOUND_MESSAGE + request.getUserId(), Constant.USER_NOT_FOUND));
	
	
	user.setStatus(request.getStatus());
	user.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
	user.setLastUpdatedTime(LocalDateTime.now());
	user.setComment(Constant.ADDED_BY + user.getCreatedBy()+ Constant.AND_UPDATED_BY + user.getFirstName()+ user.getLastName());
	
	List<RoleType> roletypes = request.getRoles();

    Set<Roles> roles = roletypes.stream()
            .map(roletype -> roleRepository.findByRoleTypes(roletype).get())
            .collect(Collectors.toSet());
    
    user.getRoles().clear();
    user.getRoles().addAll(roles);    
	
    if(roletypes.contains(RoleType.ADMIN)) {
		user.setPresentInAllBranches(true);
	}
	else {
		user.setPresentInAllBranches(false);
		
		List<String> branchMnemonics = request.getLocation();
		Set<Branch> branches = branchMnemonics.stream()
                .map(branchMnemonic -> branchRepository.findByMnemonic(branchMnemonic).get())
                .collect(Collectors.toSet());
        
        
        user.getBranches().clear();
        
        user.getBranches().addAll(branches);	        
        branches.forEach(branch -> branch.getUsers().add(user));
        
	}


    userRepository.save(user);
	
	Activity activity = new Activity();
	activity.setCreatedBy(user.getCreatedBy());
	activity.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
	activity.setLastupdatedtime(LocalDateTime.now());
	activity.setComment(Constant.ADDED_BY + user.getCreatedBy()+ Constant.AND_UPDATED_BY + user.getFirstName()+ user.getLastName());
	activity.setObjectType(ObjectType.USER);
	
	activity.setObjectID(request.getUserId());
	activityRepository.save(activity);
	
	log.info(Constant.USER_DETAILS_UPDATED_SUCCESSFULLY, request.getUserId());
	
	UserResponse response = modelMapper.map(user, UserResponse.class);
	return extractMnemonicFromBranch(user, response);
}

	@Override
    public List<UserResponse> getAllUserDeatils() {
		List<User> users = userRepository.findBystatus(true);
        List<UserResponse> userResponses = users.stream().map(user ->modelMapper.map(user, UserResponse.class))
                                                                .toList();
        
        Map<String,Set<String>> userMnemonicsMap= new HashMap<String, Set<String>>();
        
        List<Branch> allBranchesList = branchRepository.findByBranchStatus(true);
        Set<Branch> allBranchesSet = new HashSet<Branch>();
        if(allBranchesList != null && !allBranchesList.isEmpty()) {
        	allBranchesSet = allBranchesList.stream().collect(Collectors.toSet());
        }
        
        Set<Branch> finalAllBranches = allBranchesSet;
        
        users.stream().forEach(user -> {
            Set<String> mnemonics = new HashSet<String>();
            Set<Branch> branches = user.getBranches();
            
            if(user.getPresentInAllBranches()) {
            	branches = finalAllBranches;
            }
            
            if( branches != null && !branches.isEmpty() ) {
                branches.stream().forEach( branch -> mnemonics.add(branch.getMnemonic()));
                userMnemonicsMap.put(user.getUserId(), mnemonics);
            }
            else {
                userMnemonicsMap.put(user.getUserId(), new HashSet<String>());
            }
        });
        
        userResponses = userResponses.stream().map(user -> {
                                                    user.setLocation(userMnemonicsMap.get(user.getUserId()));
                                                    return user;
                                               })
                                              .collect(Collectors.toList());
        
        return userResponses;
    }
	
	private UserResponse extractMnemonicFromBranch(User user, UserResponse response) {
		if(user.getBranches() != null && !user.getBranches().isEmpty()) {
			Set<String> mnemonics = new HashSet<String>();
			user.getBranches().stream().forEach(branch -> mnemonics.add(branch.getMnemonic()));
			response.setLocation(mnemonics);
		}
		return response;
	}
	

@Override
public List<UserResponse> getAllUserDeatilsByuserID(String userid) {
	List<User> users = userRepository.findByUserIdAndStatus(userid, true);
	List<UserResponse> userResponses = users.stream().map(user ->modelMapper.map(user, UserResponse.class))
															.toList();
	return userResponses;
}

	

}
