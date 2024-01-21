package com.pkm.medicalinventory.controller.api;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pkm.medicalinventory.auth.UserSessionService;
import com.pkm.medicalinventory.dto.WebResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/public")
public class PublicController {
  
	@Autowired
	private UserSessionService userSessionService;

	public PublicController() {
		log.info("----------------------Rest Public Controller-------------------");
	}

	 
	@PostMapping(value = "/requestid", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse getRequestId() throws IOException {
		log.info("generate or update requestId }");
		WebResponse response = userSessionService.generateRequestId();
		return response;
	}
	 
	
}
