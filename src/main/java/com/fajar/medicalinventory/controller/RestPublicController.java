package com.fajar.medicalinventory.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.medicalinventory.dto.WebResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/public")
public class RestPublicController extends BaseController {
  
	public RestPublicController() {
		log.info("----------------------Rest Public Controller-------------------");
	}

	 
	@PostMapping(value = "/requestid", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse getRequestId(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		
		log.info("generate or update requestId }");
		WebResponse response = userSessionService.generateRequestId(httpRequest, httpResponse);
		return response;
	}
	 
	
}
