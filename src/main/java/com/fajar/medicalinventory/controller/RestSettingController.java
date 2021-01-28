package com.fajar.medicalinventory.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.medicalinventory.annotation.CustomRequestInfo;
import com.fajar.medicalinventory.dto.WebRequest;
import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.service.LogProxyFactory;
import com.fajar.medicalinventory.service.config.DefaultApplicationProfileService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/app/setting")
@Slf4j
public class RestSettingController extends BaseController {

	@Autowired
	private DefaultApplicationProfileService defaultAppProfileService;
	public RestSettingController() {
		log.info("------------------RestSettingController-----------------");
	}

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	 
	@PostMapping(value = "/updateprofile", produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse updateProfile(@RequestBody WebRequest webRequest, HttpServletRequest httpRequest) throws IOException {
		return defaultAppProfileService.updateApplicationProfile(httpRequest, webRequest);
	}
	 

}
