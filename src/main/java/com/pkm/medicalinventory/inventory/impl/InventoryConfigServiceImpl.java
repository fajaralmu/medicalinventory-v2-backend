package com.pkm.medicalinventory.inventory.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pkm.medicalinventory.component.BindedValues;
import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.model.ConfigurationModel;
import com.pkm.medicalinventory.entity.Configuration;
import com.pkm.medicalinventory.inventory.InventoryConfigurationService;
import com.pkm.medicalinventory.repository.ConfigurationRepository;
import com.pkm.medicalinventory.repository.EntityRepository;

@Service
public class InventoryConfigServiceImpl implements InventoryConfigurationService {
	@Autowired
	private BindedValues bindedValues;
	@Autowired
	private ConfigurationRepository configurationRepository;
	@Autowired
	private EntityRepository entityRepository;
	
	private Configuration tempConfiguration;
	
	public ConfigurationModel getConfiguration() {
		return tempConfiguration.toModel();
	}

	public void setTempConfiguration(Configuration tempConfiguration) {
		this.tempConfiguration = tempConfiguration;
	}

	@PostConstruct
	public void init() {
		Configuration config = checkConfig();
		setTempConfiguration(config);
	}

	private Configuration checkConfig() {
		Configuration configuration = configurationRepository.findTop1ByCode(bindedValues.getConfigCode());
		if (null == configuration) {
			return createConfig();
		} 
		return configuration;
	}

	private Configuration createConfig() {
		Configuration configuration = 
				Configuration
				.builder()
				.code(bindedValues.getConfigCode())
				.expiredWarningDays(6)
				.leadTime(15)
				.cycleTime(30)
				.build();
		return entityRepository.save(configuration);
	}

	public ConfigurationModel updateConfig(WebRequest webRequest) {
		Configuration configuration = checkConfig();
		Configuration payload = webRequest.getInventoryConfiguration().toEntity();
		configuration.setCycleTime(payload.getCycleTime());
		configuration.setLeadTime(payload.getLeadTime());
		configuration.setExpiredWarningDays(payload.getExpiredWarningDays());
		Configuration saved = entityRepository.save(configuration);
		
		setTempConfiguration(saved);
		
		return saved.toModel();
	}
}
