package com.pkm.medicalinventory.inventory;

import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.model.ConfigurationModel;

public interface InventoryConfigurationService {

	ConfigurationModel updateConfig( WebRequest webRequest);
	ConfigurationModel getConfiguration();

}
