package com.jlp.pmu.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jlp.pmu.enums.PrinterType;
import com.jlp.pmu.models.Printer;
import com.jlp.pmu.models.Report;

public interface PrinterRepository extends JpaRepository<Printer, Long> {

	Boolean existsByPrinterNameAndBranchCode(String printerName, Long branchcode);
//List<Printer> findByBranchCodeAndPrinterType(Long branchCode, String printerType);

	@Query("SELECT p.printerType FROM Printer p WHERE p.printerName = :printerName AND p.branchCode = :branchCode")
	PrinterType findPrinterTypeByPrinterNameAndBranchCode(String printerName, Long branchCode);

	@Query("SELECT p.printerName FROM Printer p WHERE p.status =:status AND p.printerType = :printType AND p.branchCode = :branchCode")
	List<String> findByCombinedPrintTypeAndBranchCode(boolean status,PrinterType printType, Long branchCode);

	@Query("SELECT DISTINCT p.printerType FROM Printer p WHERE p.branchCode = :branchCode")
	List<PrinterType> findAllPrinterTypes(Long branchCode);

	List<Printer> findByPrinterIDIn(Set<Long> printerIds);
	
	@Query(value = "SELECT p FROM Printer p WHERE p.status =:status AND p.printerID =:printerID")
	Printer findByPrinterIDAndStatus(Long printerID);

	Optional<Printer> findByBranchCodeAndPrinterName(Long branchCode, String printerName);
	
	@Query(value = "SELECT p FROM Printer p WHERE p.status =:status AND p.branchCode =:branchCode ORDER BY lastUpdatedTime DESC")
	List<Printer> findByStatusAndBranchCodeOrderbydate(boolean status, Long branchCode);

	Optional<Printer> findByPrinterIDAndBranchCode(Long printerID, Long branchCode);
	
	@Query(value = "SELECT p.* FROM printer p JOIN report r ON p.printerid = r.printer_id WHERE p.branch_code = :branchCode AND p.status = :status AND r.reportname in :reportNames", nativeQuery = true)
	List<Printer> filterByReportNamesAndReportType(List<String> reportNames, Boolean status, Long branchCode);

	@Query(value = "SELECT p.* FROM (( printer p JOIN printer_job j ON p.printerid = j.printer_id) JOIN print_transaction t ON j.print_job_id = t.print_job_id ) WHERE p.branch_code = :branchCode AND p.status = :status AND t.type = :jobType", nativeQuery = true)
	List<Printer> fetchHoldTransactionsPrinters(Long branchCode, Boolean status, String jobType);

	@Query(value = "SELECT p.* FROM (( printer p JOIN printer_job j ON p.printerid = j.printer_id) JOIN print_transaction t ON j.print_job_id = t.print_job_id ) WHERE p.branch_code = :branchCode AND p.status = :branchStatus AND t.type = :jobType AND t.status = :JobStatus", nativeQuery = true)
	List<Printer> fetchPrintedOrUnPrintedTransactionsPrinters(Long branchCode, Boolean branchStatus, String jobType, String JobStatus);
	

}
