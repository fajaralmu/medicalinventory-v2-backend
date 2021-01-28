package com.fajar.medicalinventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.medicalinventory.entity.ApplicationProfile;

public interface AppProfileRepository extends JpaRepository<ApplicationProfile, Long> {
 

	ApplicationProfile findByAppCode(String appCode); 

}
