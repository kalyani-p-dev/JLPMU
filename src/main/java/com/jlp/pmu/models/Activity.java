package com.jlp.pmu.models;

import java.time.LocalDateTime;

import com.jlp.pmu.enums.ObjectType;

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
@Table(name = "Activity")
public class Activity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ActivityId")
	private Long activityId;

	@Column(name = "CreatedBy", nullable = false)
	private String createdBy;

	@Column(name = "UpdatedBy")
	private String updatedBy;

	@Column(name = "LastUpdatedTime", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime lastupdatedtime;

	@Column(name = "Comment")
	private String comment;

	@Column(name = "ObjectType")
	@Enumerated(EnumType.STRING)
	private ObjectType objectType;

	@Column(name = "ObjectID")
	private String objectID;

}
