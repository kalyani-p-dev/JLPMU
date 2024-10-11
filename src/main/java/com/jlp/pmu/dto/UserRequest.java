package com.jlp.pmu.dto;

import java.util.List;

import com.jlp.pmu.enums.RoleType;
import com.jlp.pmu.enums.UserType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequest {

	private String userId;
	private String firstName;
	private String lastName;
	private Boolean status;
	private List<String> location;
	private UserType userType;
	private String emailAddress;
//	private Boolean activeDirectory;
	private List<RoleType> roles;
}
