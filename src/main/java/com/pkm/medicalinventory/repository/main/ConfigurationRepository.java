package com.pkm.medicalinventory.repository.main;

import com.pkm.medicalinventory.entity.Configuration;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationRepository extends JpaRepository<Configuration, Long>{

	Configuration findTop1ByCode(String configCode);
	
	 

}
