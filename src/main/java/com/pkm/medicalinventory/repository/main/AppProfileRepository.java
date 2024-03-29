package com.pkm.medicalinventory.repository.main;

import com.pkm.medicalinventory.entity.ApplicationProfile;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppProfileRepository extends JpaRepository<ApplicationProfile, Long> {
 

	ApplicationProfile findByAppCode(String appCode); 

}
