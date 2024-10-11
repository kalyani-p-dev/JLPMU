package com.jlp.pmu.dto;

import java.util.List;

import lombok.Data;

@Data
public class BranchResponse{

	private Long branchCode;
	private String comment;
	private String branchName;
	private String mnemonic;
	private List<String> pmuServers;

}
