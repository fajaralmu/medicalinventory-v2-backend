package com.fajar.medicalinventory.controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.medicalinventory.annotation.CustomRequestInfo;
import com.fajar.medicalinventory.dto.WebRequest;
import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.service.LogProxyFactory;
import com.fajar.medicalinventory.service.transaction.InventoryService;
import com.fajar.medicalinventory.service.transaction.TransactionService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/app/inventory")
@Slf4j
public class RestInventoryController extends BaseController {

	@Autowired
	private InventoryService inventoryService; 

	public RestInventoryController() {
		log.info("------------------RestInventoryController-----------------");
	}

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
 
	@PostMapping(value = "/availableproducts/{code}", produces = MediaType.APPLICATION_JSON_VALUE) 
	public WebResponse gettransactionbycode(@PathVariable String code, HttpServletRequest httpRequest) {
		log.info("availableproducts {}", code);
		return inventoryService.getAvailableProducts(code);
	}

	 
}
