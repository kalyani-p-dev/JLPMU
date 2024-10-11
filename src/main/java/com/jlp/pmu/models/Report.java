package com.jlp.pmu.models;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "Report")
public class Report {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ReportId")
	private Long reportId;

	@Column(name = "reportname")
	private String reportName;

	@Column(name = "dept")
	private String dept;

	@Column(name = "subdept")
	private String subDept;

	@Column(name = "printer_id")
	private Long printerId;

	@Column(name = "printjoboptions")
	private Boolean printjoboptions;

	@Column(name = "comments")
	private String comments;

	@Column(name = "branch_code")
	private Long branchCode;

	@Column(name = "Copies")
	private int copies;

	@Column(name = "status")
	private Boolean status;
	
	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "last_updated_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime lastUpdatedTime;

}
