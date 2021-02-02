package com.fajar.medicalinventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.medicalinventory.entity.Configuration;

public interface ConfigurationRepository extends JpaRepository<Configuration, Long>{

	Configuration findTop1ByCode(String configCode);
	
	 

}
