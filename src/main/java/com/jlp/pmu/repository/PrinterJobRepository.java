package com.jlp.pmu.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jlp.pmu.models.PrinterJob;

public interface PrinterJobRepository extends JpaRepository<PrinterJob, Long>{
	List<PrinterJob> findByPrintJobIdIn(Set<Long> printJobIds);
}
