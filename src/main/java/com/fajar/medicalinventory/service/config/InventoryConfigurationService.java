package com.fajar.medicalinventory.service.config;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.dto.WebRequest;
import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.dto.model.ConfigurationModel;
import com.fajar.medicalinventory.entity.Configuration;
import com.fajar.medicalinventory.repository.ConfigurationRepository;
import com.fajar.medicalinventory.repository.EntityRepository;

@Service
public class InventoryConfigurationService {

	@Autowired
	private BindedValues bindedValues;
	@Autowired
	private ConfigurationRepository configurationRepository;
	@Autowired
	private EntityRepository entityRepository;
	
	private Configuration tempConfiguration;
	
	public Configuration getTempConfiguration() {
		return tempConfiguration;
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
		if (null ==configuration) {
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

	public WebResponse updateConfig(HttpServletRequest httpRequest, WebRequest webRequest) {
		Configuration configuration = checkConfig();
		Configuration payload = webRequest.getInventoryConfiguration().toEntity();
		configuration.setCycleTime(payload.getCycleTime());
		configuration.setLeadTime(payload.getLeadTime());
		configuration.setExpiredWarningDays(payload.getExpiredWarningDays());
		Configuration saved = entityRepository.save(configuration);
		
		setTempConfiguration(saved);
		
		return WebResponse.builder().entity(saved.toModel()).build();
	}

	 
}
