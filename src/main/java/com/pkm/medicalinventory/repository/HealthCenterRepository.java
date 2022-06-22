package com.pkm.medicalinventory.repository;

import com.pkm.medicalinventory.entity.HealthCenter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthCenterRepository extends JpaRepository<HealthCenter, Long> {

	HealthCenter findTop1ByCode(String masterHealthCenterCode);

	 
}
