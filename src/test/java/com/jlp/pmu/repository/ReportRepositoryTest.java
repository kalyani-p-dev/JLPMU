package com.jlp.pmu.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.jlp.pmu.models.Report;

@DataJpaTest
class ReportRepositoryTest {
	
	@Autowired
	ReportRepository reportRepository;
	
	Report report = new Report();
	Report report2 = new Report();

	@BeforeEach
	void setUp() {
		
		//mocking report object
		
		report.setBranchCode(6L);
		report.setComments("added by John");
		report.setCopies(2);
		report.setDept("dept1");
		report.setPrinterId(3L);
		report.setPrintjoboptions(true);
		report.setReportName("CDSPRINTMAN");
		report.setSubDept("sub1");
		report.setCreatedBy("David");
		report.setUpdatedBy("Kane");
		report.setLastUpdatedTime(LocalDateTime.now());
		report.setStatus(true);
		
		report2.setBranchCode(6L);
		report2.setComments("added by John");
		report2.setCopies(2);
		report2.setDept("dept1");
		report2.setPrinterId(4L);
		report2.setPrintjoboptions(true);
		report2.setReportName("CDSPRINTMAN");
		report2.setSubDept("sub1");
		report2.setCreatedBy("David");
		report2.setUpdatedBy("Kane");
		report2.setLastUpdatedTime(LocalDateTime.now());
		report2.setStatus(true);

	}
	
	@Test
	public void findReportByCombinations_query_test() {
		
		reportRepository.saveAll(List.of(report, report2));
		
		Optional<Report> optional = reportRepository.findReportByCombinations(report.getReportName(), report.getBranchCode(), report.getDept(), report.getSubDept());
	
		assertTrue(optional.isPresent());
		assertEquals(report.getPrinterId(), optional.get().getPrinterId());
	}
	
	@Test
	public void findByStatusAndBranchCode_query_test() {
		
		reportRepository.saveAll(List.of(report, report2));
		
		List<Report> reports = reportRepository.findByStatusAndBranchCodeOrderbyDate(true, 6L);
		
		assertNotNull(reports);
		assertEquals(2, reports.size());
	}
	

}
