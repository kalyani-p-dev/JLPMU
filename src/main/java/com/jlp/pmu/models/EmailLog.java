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
@Table(name= "EmailLog")
public class EmailLog {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name ="Id")
	private int id;
	
	@Column(name ="EmaileTo")
	private String email_to;
	
	@Column(name ="UserName")
	private String user_name;
	
	@Column(name ="DateTime")
	private LocalDateTime date_time;
	
	
}


