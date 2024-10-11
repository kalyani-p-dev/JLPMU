package com.jlp.pmu.models;

import java.time.LocalDate;
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
@Table(name= "Template")
public class Template {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name ="temp_id")
	private int tempId;
	
	@Column(name ="template_name")
	private String templateName;
	
	@Column(name ="template_description")
	private String templateDescription;
	
	@Column(name ="date_time")
	private LocalDateTime dateTime;
	
	@Column(name ="file_size") 
	private Long fileSIze;
	
	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_by")
	private String updatedBy;
	
	@Column(name = "Status")
	private Boolean status;
	
	@Column(name = "LastUpdatedTime", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime lastupdatedtime;

	@Column(name = "Comment")
	private String comment;
	
	
	
}
