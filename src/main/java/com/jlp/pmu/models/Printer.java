package com.jlp.pmu.models;

import java.time.LocalDateTime;
import java.util.List;

import com.jlp.pmu.enums.PrinterType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "Printer")
public class Printer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PrinterID")
	private Long printerID;

	@Column(name = "branch_code")
	private Long branchCode;

	@Column(name = "printer_name")
	private String printerName;

	@Column(name = "pmu_printer_name")
	private String pmuPrinterName;

	@Column(name = "pmuserver")
	private String pmuServer;

	@Column(name = "localattachedprinter")
	private boolean localAttachedPrinter;

	@Column(name = "Printer_type")
	@Enumerated(EnumType.STRING)
	private PrinterType printerType;
	
	@Column(name = "Stationary")
	private String stationary;

	@ElementCollection
    @CollectionTable(name = "stationaryList", joinColumns = @JoinColumn(name = "PrinterID"))
    @Column(name = "stationaryList")
    private List<String> stationaryList;

	@Column(name = "Status")
	private Boolean status;

	@Column(name = "StationaryActive")
	private Boolean stationaryActive;

	@Column(name = "PrinterpATH")
	private String printerPATH;

	@Column(name = "Comments")
	private String comments;

	@Column(name = "RedirectPrintName")
	private String redirectPrintName;

	@Column(name = "redirectexist")
	private Boolean redirectExist;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "last_updated_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime lastUpdatedTime;

}
