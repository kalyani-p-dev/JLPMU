package com.jlp.pmu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jlp.pmu.models.Activity;

public interface ActivityRepository extends JpaRepository<Activity, Integer>{

}
