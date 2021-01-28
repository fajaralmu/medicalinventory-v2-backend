package com.fajar.medicalinventory.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.entity.HealthCenter;

@Repository
public interface HealthCenterRepository extends CrudRepository<HealthCenter, Long> {

	 
}
