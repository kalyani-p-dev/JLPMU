package com.jlp.pmu.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "FormStat")
public class FormStat {

	@Id
	@Column(name = "FormStatID")
	private Long formStatID;

	@Column(name = "FormStatValue")
	private String formStatValue;

}
