package com.jlp.pmu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jlp.pmu.models.Printer;

public interface HomeRepository extends JpaRepository<Printer, Long> {
	
	@Query(value = "SELECT listOfStationary FROM (SELECT DISTINCT p.stationaryList as listOfStationary FROM Printer p) ORDER BY listOfStationary ASC")
	List<String> findAllStaionary();
}
