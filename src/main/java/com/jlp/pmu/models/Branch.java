package com.jlp.pmu.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Branch")
public class Branch {

	@Id
	@Column(name = "BranchCode")
	private Long branchCode;

	@Column(name = "BranchName")
	private String branchName;

	@Column(name = "Mnemonic")
	private String mnemonic;

	@ElementCollection
	@CollectionTable(name = "pmu_servers", joinColumns = @JoinColumn(name = "branch_code"))
	@Column(name = "pmu_server")
	private List<String> pmuServers;

	@Column(name = "BranchStatus")
	private Boolean branchStatus;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "last_updated_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime lastUpdatedTime;

	@Column(name = "comment")
	private String comment;

	@JsonIgnore
	@ManyToMany(mappedBy = "branches", cascade = CascadeType.MERGE)
	private Set<User> users = new HashSet<>();

}
