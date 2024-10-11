package com.jlp.pmu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jlp.pmu.models.User;

public interface UserRepository extends JpaRepository<User, String> {
	
	List<User> findBystatus(Boolean status);
	List<User> findByUserIdAndStatus(String userId,Boolean status);
	
}
