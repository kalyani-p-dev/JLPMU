package com.jlp.pmu.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jlp.pmu.dto.JobDetailsResponse;
import com.jlp.pmu.enums.PrinterJobType;
import com.jlp.pmu.models.PrintTransaction;

public interface PrintTransactionRepository extends JpaRepository<PrintTransaction, Long>{

	List<PrintTransaction> findByBranchCodeAndReportNameIn(Long branchCode, List<String> reportNames);
	
	List<PrintTransaction> findByBranchCodeAndPrinterNameIn(Long branchCode, List<String> printerNames);

	List<PrintTransaction> findByBranchCode(Long branchCode);

	List<PrintTransaction> findByBranchCodeAndTypeIn(Long branchCode, List<PrinterJobType> printerJobTypes);

	List<PrintTransaction> findByBranchCodeAndStationaryIn(Long branchCode, List<String> stationeries);
	
	@Query(value = "SELECT t.* FROM ((print_transaction t JOIN printer_job j ON t.print_job_id = j.print_job_id) JOIN printer r ON r.printerid = j.printer_id) WHERE r.printer_type IN :printerTypes AND t.printer_name in :printerNames AND t.branch_code = :branchCode", nativeQuery = true)
	List<PrintTransaction> filterByPrinterTypes(List<String> printerTypes, List<String> printerNames, Long branchCode);

//	@Query(value = "SELECT t.* FROM ((print_transaction t JOIN printer_job j ON t.print_job_id = j.print_job_id) JOIN report r ON r.printer_id = j.printer_id) WHERE t.report_name = r.reportname AND t.branch_code = r.branch_code AND t.dept_code = r.dept AND t.report_name in ('report1') AND r.reporttype = 'REP';")
//	@Query(value = "SELECT t.* FROM ((print_transaction t JOIN printer_job j ON t.print_job_id = j.print_job_id) JOIN report r ON r.printer_id = j.printer_id) WHERE t.report_name = r.reportname AND t.branch_code = r.branch_code  AND t.report_name in :repReportNames AND r.reporttype = :reportType", nativeQuery = true)
	@Query(value = "SELECT t.* FROM ((print_transaction t JOIN printer_job j ON t.print_job_id = j.print_job_id) JOIN report r ON r.printer_id = j.printer_id) WHERE t.report_name = r.reportname AND t.branch_code = r.branch_code  AND t.report_name in :reportNames AND t.branch_code = :branchCode AND t.printer_name in :printerNames" , nativeQuery = true)
	List<PrintTransaction> filterByReportNamesAndReportType(List<String> reportNames, List<String> printerNames, Long branchCode);

	@Query(value = "SELECT t.* FROM print_transaction t WHERE t.branch_code = :branchCode AND DATE_PART('day', now() :::: timestamp - t.start_time :::: timestamp) < 30 AND ( regexp_like( REPLACE(t.customer_name, ' ', ''), :searchKey , 'i') OR regexp_like( REPLACE(t.report_name, ' ', ''), :searchKey , 'i') OR regexp_like( REPLACE(t.default_title, ' ', ''), :searchKey , 'i') OR regexp_like( REPLACE(t.ref, ' ', ''), :searchKey , 'i'))", nativeQuery = true)
	List<PrintTransaction> searchJobDetails(Long branchCode, String searchKey);

	List<PrintTransaction> findBystartTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

	List<PrintTransaction> findBystartTimeBetweenAndBranchCodeAndPrinterNameIn(LocalDateTime startDateTime, LocalDateTime endDateTime, Long branchCode, List<String> printerNames);


}
