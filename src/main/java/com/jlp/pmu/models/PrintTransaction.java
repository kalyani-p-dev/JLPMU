package com.jlp.pmu.models;

import java.time.LocalDateTime;

import com.jlp.pmu.enums.PrinterJobType;
import com.jlp.pmu.enums.StatusType;

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
@Table(name = "PrintTransaction")
public class PrintTransaction {
	@Id
	@Column(name = "transaction_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long transactionId;

	@Column(name = "printer_name")
	private String printerName;

	@Column(name = "stationary")
	private String stationary;

	@Column(name = "report_name")
	private String reportName;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private PrinterJobType type;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private StatusType status;

	@Column(name = "pdfpath")
	private String pdfPath;

	@Column(name = "fixture")
	private String fixture;

	@Column(name = "item")
	private String item;

	@Column(name = "print_job_id")
	private Long printerJob;

	@Column(name = "dept_code")
	private String deptCode;

	@Column(name = "arrival_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime arrivalTime;

	@Column(name = "customer_name")
	private String customerName;

	@Column(name = "Gen")
	private String gen;

	@Column(name = "default_title")
	private String defaultTitle;

	@Column(name = "time" , columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime time;

	@Column(name = "pages")
	private int pages;

	@Column(name = "ref")
	private String ref;

	@Column(name = "branchCode")
	private Long branchCode;

	@Column(name = "startTime", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime startTime;

	@Column(name = "endTime", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime endTime;

	@Column(name = "report_description")
	private String reportDescription;

}
