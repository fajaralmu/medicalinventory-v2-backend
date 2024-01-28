package com.pkm.medicalinventory.inventory.impl;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pkm.medicalinventory.component.BindedValues;
import com.pkm.medicalinventory.entity.HealthCenter;
import com.pkm.medicalinventory.exception.DataNotFoundException;
import com.pkm.medicalinventory.inventory.WarehouseService;
import com.pkm.medicalinventory.repository.main.EntityRepository;
import com.pkm.medicalinventory.repository.main.HealthCenterRepository;

@Service
public class WarehouseServiceImpl implements WarehouseService {
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
		if (null != healthCenter) {
			return healthCenter;
		}
		healthCenter = new HealthCenter();
		healthCenter.setCode(bindedValues.getMasterHealthCenterCode());
		healthCenter.setName(bindedValues.getMasterHealthCenterName());
		healthCenter.setAddress("Default Address");
		return entityRepository.save(healthCenter);
	}

	public boolean isMasterHealthCenter(HealthCenter healthCenter) {
		String code = healthCenter.getCode();
		return getMasterHealthCenterCode().equals(code);
	}

	public String getMasterHealthCenterCode() {
		return bindedValues.getMasterHealthCenterCode();
	}

	public void checkLocationRecord(HealthCenter location) {
		try {

			Optional<HealthCenter> loc = healthCenterRepository.findById(location.getId());
			loc.get();
		} catch (Exception e) {
			throw new DataNotFoundException("Location not found: " + e.getMessage());
		}

	}
}
