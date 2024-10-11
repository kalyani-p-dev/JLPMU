package com.jlp.pmu.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.jlp.pmu.models.Branch;

@DataJpaTest
class BranchRepositoryTest {
	
	@Autowired
	BranchRepository branchRepository;
	
	Branch branch = new Branch();
	Branch branch2 = new Branch();
	
	@BeforeEach
	void setup() {
		branch.setBranchCode(42L);
		branch.setBranchName("John Lewis London");
		branch.setBranchStatus(true);
		branch.setCreatedBy("David Warner");
		branch.setLastUpdatedTime(LocalDateTime.now());
		branch.setMnemonic("LD");
		branch.setPmuServers(new ArrayList<String>(List.of("GW-S-AB1","GW-S-AF1")));
		branch.setUpdatedBy("Kane Williamson");
		branch.setComment("Added by David Warner and updated by Kane Williamson");
		
		branch2.setBranchCode(43L);
		branch2.setBranchName("John Lewis Glassgow");
		branch2.setBranchStatus(true);
		branch2.setCreatedBy("David Warner");
		branch2.setLastUpdatedTime(LocalDateTime.now());
		branch2.setMnemonic("GS");
		branch2.setPmuServers(new ArrayList<String>(List.of("GW-S-AC1","GW-S-AF1")));
		branch2.setUpdatedBy("Kane Williamson");
		branch2.setComment("Added by David Warner and updated by Kane Williamson");
	}
	
	@Test
	public void saveBranch_test() {
		Branch savedBranch = branchRepository.save(branch);
		
		assertNotNull(savedBranch);
		assertThat(savedBranch.getBranchCode()).isEqualTo(branch.getBranchCode());
	}
	
	@Test
	public void updateBranch_test() {
		
		branchRepository.save(branch);
		
		Branch savedBranch = branchRepository.findById(branch.getBranchCode()).get();
		
		savedBranch.setBranchName("John Lewis Manchester");
		savedBranch.setMnemonic("MC");
		
		Branch updatedBranch = branchRepository.save(savedBranch);
		
		assertNotNull(updatedBranch);
		assertEquals("John Lewis Manchester", updatedBranch.getBranchName());
		assertEquals("MC", updatedBranch.getMnemonic());
		
	}
	
	@Test
	public void findByBranchStatus_query_test() {
		
		branchRepository.saveAll(List.of(branch, branch2));
		
		List<Branch> branches = branchRepository.findByBranchStatus(true);
		
		assertNotNull(branches);
		assertFalse(branches.isEmpty());
		assertThat(branches.size()).isEqualTo(2);
	}
	
	@Test
	public void findAllPMUServers_query_test() {
		
		branchRepository.saveAll(List.of(branch, branch2));
		
		List<String> pmuServers = branchRepository.findAllPMUServers();
		
		assertNotNull(pmuServers);
		assertFalse(pmuServers.isEmpty());
		assertThat(pmuServers.size()).isEqualTo(3);
		
	}
	
	@Test
	public void findByBranchCodeOrMnemonic_query_test() {
		
		branchRepository.saveAll(List.of(branch, branch2));
		
		List<Branch> branches = branchRepository.findByBranchCodeOrMnemonic(branch.getBranchCode(), branch2.getMnemonic());
		
		assertNotNull(branches);
		assertFalse(branches.isEmpty());
		assertThat(branches.size()).isEqualTo(2);
	}
	
	@Test
	public void findAllMnemonic_query_test() {
		
		branchRepository.saveAll(List.of(branch, branch2));
		
		List<String> mnemonics = branchRepository.findAllMnemonic();
		
		assertNotNull(mnemonics);
		assertFalse(mnemonics.isEmpty());
		assertThat(mnemonics.size()).isEqualTo(2);
	}
	
	@Test
	public void findByMnemonic_query_test() {
		
		branchRepository.saveAll(List.of(branch, branch2));
		
		Branch branchTest = branchRepository.findByMnemonic(branch.getMnemonic()).get();
		
		assertNotNull(branchTest);
		assertEquals(branch.getBranchCode(), branchTest.getBranchCode());
	}
}
