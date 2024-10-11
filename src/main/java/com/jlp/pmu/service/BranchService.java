package com.jlp.pmu.service;

import java.util.List;

import com.jlp.pmu.dto.BranchRequest;
import com.jlp.pmu.dto.BranchResponse;

public interface BranchService {
	BranchResponse addBranchDetails(BranchRequest requestDto);
	BranchResponse getBranchDetailsByID(Long branchCode);
	BranchResponse doInactiveBranchByBranchCode(BranchRequest request);
	List<BranchResponse> getAllBranchDeatils();
	List<String> getPMUServersByBranchID(Long branchCode);
	List<String> getAllPMUServers();
	List<String> getAllMnemonic();
	
}
