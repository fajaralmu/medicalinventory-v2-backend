package com.pkm.medicalinventory.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pkm.medicalinventory.annotation.CustomRequestInfo;
import com.pkm.medicalinventory.config.ApplicationProfileService;
import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.dto.model.ApplicationProfileModel;
import com.pkm.medicalinventory.dto.model.ConfigurationModel;
import com.pkm.medicalinventory.inventory.InventoryConfigurationService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/app/setting")
@Slf4j
public class SettingController {

	@Autowired
	private ApplicationProfileService appProfileService;
	@Autowired
	private InventoryConfigurationService inventoryConfigurationService;
	public SettingController() {
		log.info("------------------RestSettingController-----------------");
	}
	 
	@PostMapping(value = "/updateprofile", produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse updateProfile(@RequestBody WebRequest webRequest) {
		ApplicationProfileModel profile = appProfileService.updateApplicationProfile(webRequest);
		return new WebResponse().withAppProfile(profile);
	}
	@PostMapping(value = "/updateconfig", produces = MediaType.APPLICATION_JSON_VALUE) 
	public WebResponse updateconfig(@RequestBody WebRequest webRequest) {
		ConfigurationModel config = inventoryConfigurationService.updateConfig(webRequest);
		return new WebResponse().withConfiguration(config);
	}
	 

}
