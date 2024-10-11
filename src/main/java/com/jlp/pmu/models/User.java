package com.jlp.pmu.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.jlp.pmu.enums.UserType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@Column(name = "user_id")
	private String userId;

	@Column(name = "firstname")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;

	@Column(name = "status")
	private Boolean status;
	
	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "last_updated_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime lastUpdatedTime;

	@Column(name = "comment")
	private String comment;
	
	@Column(name = "present_in_all_branches")
	private Boolean presentInAllBranches;

	@Column(name = "user_type")
	@Enumerated(EnumType.STRING)
	private UserType userType;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
    		name = "users_roles",
   		joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
   		inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    )
    private Set<Roles> roles = new HashSet<Roles>();


	@Column(name = "email_address")
	private String emailAddress;

	@Column(name = "active_directory")
	private Boolean activeDirectory;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
    		name = "users_branches",
    		joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
    		inverseJoinColumns = @JoinColumn(name = "branch_id", referencedColumnName = "BranchCode")
    )
    private Set<Branch> branches = new HashSet<Branch>();

}
