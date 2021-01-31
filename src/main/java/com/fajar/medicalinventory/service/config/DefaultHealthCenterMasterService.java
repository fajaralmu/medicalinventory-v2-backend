package com.fajar.medicalinventory.service.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.entity.HealthCenter;
import com.fajar.medicalinventory.repository.EntityRepository;
import com.fajar.medicalinventory.repository.HealthCenterRepository;

@Service
public class DefaultHealthCenterMasterService {

	@Autowired
	private BindedValues bindedValues;
	@Autowired
	private HealthCenterRepository healthCenterRepository;
	@Autowired
	private EntityRepository entityRepository;
	

	/**
	 * 
	 */
	@PostConstruct
	public void init() {
		getMasterHealthCenter();
	}

	public HealthCenter getMasterHealthCenter() {
		HealthCenter healthCenter = healthCenterRepository.findTop1ByCode(bindedValues.getMasterHealthCenterCode());
		if (null !=  healthCenter) {
			return healthCenter;
		}
		healthCenter = new HealthCenter();
		healthCenter.setCode(bindedValues.getMasterHealthCenterCode());
		healthCenter.setName(bindedValues.getMasterHealthCenterName());
		healthCenter.setAddress("Address");
		return entityRepository.save(healthCenter);
	}
	
	public boolean isMasterHealthCenter(HealthCenter healthCenter) { 
		String code = healthCenter.getCode();
		return getMasterHealthCenterCode().equals(code);
	}
	public String getMasterHealthCenterCode() {
		return bindedValues.getMasterHealthCenterCode();
	}

}
