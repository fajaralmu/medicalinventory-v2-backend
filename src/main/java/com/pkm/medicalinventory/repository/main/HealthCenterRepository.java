package com.pkm.medicalinventory.repository.main;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pkm.medicalinventory.entity.HealthCenter;

@Repository
public interface HealthCenterRepository extends JpaRepository<HealthCenter, Long> {

	HealthCenter findTop1ByCode(String masterHealthCenterCode);
}
