package com.jlp.pmu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jlp.pmu.enums.RoleType;
import com.jlp.pmu.models.Roles;

public interface RoleRepository extends JpaRepository<Roles,String>{
	
	Optional<Roles> findByRoleTypes(RoleType roleTypes);

}
