package com.jlp.pmu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.jlp.pmu.models.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
	
	@Query(value = "SELECT r FROM Report r WHERE r.reportName = :reportName AND r.branchCode = :branchCode AND r.dept = :dept AND r.subDept = :subDept")
	Optional<Report> findReportByCombinations(String reportName, Long branchCode, String dept, String subDept);

	@Query(value = "SELECT r FROM Report r WHERE r.status =:status AND r.branchCode =:branchCode ORDER BY lastUpdatedTime DESC")
	List<Report> findByStatusAndBranchCodeOrderbyDate(boolean status, Long branchCode);
	
	@Query(value = "SELECT r FROM Report r WHERE r.status =:status AND r.reportName = :reportName AND r.branchCode = :branchCode AND r.dept = :dept AND r.subDept = :subDept")
	Optional<Report> findReportByReportNameAndBranchCode(boolean status,String reportName, Long branchCode, String dept, String subDept);

}
