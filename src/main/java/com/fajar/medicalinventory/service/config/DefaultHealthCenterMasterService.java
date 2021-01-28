package com.fajar.medicalinventory.service.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.repository.HealthCenterRepository;

@Service
public class DefaultHealthCenterMasterService {
	
	@Autowired
	private HealthCenterRepository healthCenterRepository;
	@PostConstruct
	public void init () {
		checkMasterHealthCenter();
	}
	private void checkMasterHealthCenter() {
		// TODO Auto-generated method stub
		
	}

}
