package com.jlp.pmu.models;

import java.time.LocalDateTime;

import com.jlp.pmu.enums.PrinterJobStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "PrinterJob")
public class PrinterJob {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PrintJobId")
	private Long printJobId;

	@Column(name = "JobStatus")
	@Enumerated(EnumType.STRING)
	private PrinterJobStatus jobStatus;

	@Column(name = "JobStartTime")
	private LocalDateTime jobStartTime;

	@Column(name = "JobEndTime")
	private LocalDateTime jobEndTime;

	@Column(name = "printerId")
	private Long printerId;

	@Column(name = "Hold")
	private Boolean hold;
	
	@Column(name = "noOfCopies")
	private int noOfCopies;

}
