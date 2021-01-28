package com.fajar.medicalinventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.entity.HealthCenter;

@Repository
public interface HealthCenterRepository extends JpaRepository<HealthCenter, Long> {

	HealthCenter findTop1ByCode(String masterHealthCenterCode);

	 
}
