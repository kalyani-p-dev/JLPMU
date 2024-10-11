package com.jlp.pmu.dto;

import java.util.Set;

import com.jlp.pmu.enums.UserType;
import com.jlp.pmu.models.Branch;
import com.jlp.pmu.models.Roles;

import lombok.Builder;
import lombok.Data;


@Data

public class UserResponse {

	private String userId;
	private String firstName;
	private String lastName;
	private Set<String> branches;
	//private Set<Branch> branches;
	//private Set<String> branches;
	private Set<String> location;
	private UserType userType;
	private String emailAddress;
	//private Boolean activeDirectory;
	private Boolean status;
	private Set<Roles> roles;
	private Boolean presentInAllBranches;
}
