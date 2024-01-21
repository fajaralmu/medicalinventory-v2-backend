package com.pkm.medicalinventory.inventory;

import com.pkm.medicalinventory.entity.HealthCenter;

public interface WarehouseService {
	 

	public HealthCenter getMasterHealthCenter();
	public boolean isMasterHealthCenter(HealthCenter healthCenter);

	public String getMasterHealthCenterCode();
	public void checkLocationRecord(HealthCenter location);
}
