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

@Entity
@Table(name = "LoginActivity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginActivity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; 
    
    @Column(name = "user_id")
    private String userId; 

    @Column(name = "logged_in_time")
    private LocalDateTime loggedInTime;

    @Column(name = "logged_out_time")
    private LocalDateTime loggedOutTime;
    
    @Override
    public String toString() {
        return "LoginActivity{" +
                "userId=" + userId +
                ", loggedInTime=" + loggedInTime +
                ", loggedOutTime=" + loggedOutTime +
                '}';
    }
}
