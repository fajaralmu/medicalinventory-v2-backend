package com.pkm.medicalinventory.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.pkm.medicalinventory.annotation.CustomRequestInfo;
import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.service.config.DefaultApplicationProfileService;
import com.pkm.medicalinventory.service.config.InventoryConfigurationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/app/setting")
@Slf4j
public class RestSettingController {

	@Autowired
	private DefaultApplicationProfileService defaultAppProfileService;
	@Autowired
	private InventoryConfigurationService inventoryConfigurationService;
	public RestSettingController() {
		log.info("------------------RestSettingController-----------------");
	}
	 
	@PostMapping(value = "/updateprofile", produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse updateProfile(@RequestBody WebRequest webRequest, HttpServletRequest httpRequest) throws IOException {
		return defaultAppProfileService.updateApplicationProfile(httpRequest, webRequest);
	}
	@PostMapping(value = "/updateconfig", produces = MediaType.APPLICATION_JSON_VALUE) 
	public WebResponse updateconfig(@RequestBody WebRequest webRequest, HttpServletRequest httpRequest) throws IOException {
		return inventoryConfigurationService.updateConfig(httpRequest, webRequest);
	}
	 

}
