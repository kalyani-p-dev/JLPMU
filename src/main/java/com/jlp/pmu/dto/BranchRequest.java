package com.jlp.pmu.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BranchRequest {
	private String userId;
	private Long branchCode ;
	private String branchName;
	private String mnemonic;
	private List<String> pmuServers;
	private String comment;
}
