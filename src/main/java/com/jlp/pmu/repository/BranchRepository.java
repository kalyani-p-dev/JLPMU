package com.jlp.pmu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jlp.pmu.models.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long> {
	
	List<Branch> findByBranchStatus(Boolean status);
	
	@Query(value = "SELECT listOfPmuServers FROM (SELECT DISTINCT b.pmuServers as listOfPmuServers FROM Branch b) ORDER BY listOfPmuServers ASC")
	List<String> findAllPMUServers();
	
	List<Branch> findByBranchCodeOrMnemonic(Long branchCode, String Mnemonic);
	
	//@Query(value = "SELECT listOfmnemonic FROM (SELECT DISTINCT b.mnemonic as listOfmnemonic FROM Branch b) ORDER BY listOfmnemonic ASC")
	@Query(value = "SELECT b.mnemonic FROM Branch b where b.branchStatus =true")
	List<String> findAllMnemonic();

	Optional<Branch> findByMnemonic(String mnemonic);
	
	
}
