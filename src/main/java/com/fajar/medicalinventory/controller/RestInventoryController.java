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
 
	@PostMapping(value = "/availableproducts/{code}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE) 
	public WebResponse gettransactionbycode(@PathVariable String code, @RequestBody WebRequest webRequest, HttpServletRequest httpRequest) {
		log.info("availableproducts {} at {} ", code, webRequest.getHealthcenter().getName());
		return inventoryService.getAvailableProducts(code, webRequest);
	}
	@PostMapping(value = "/getproducts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE) 
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse getproducts(@RequestBody WebRequest webRequest, HttpServletRequest httpRequest) {
		log.info("getproducts   at {} ", webRequest.getHealthcenter().getName());
		return inventoryService.getProducts( webRequest, httpRequest);
	}
	
	@PostMapping("/recalculatestock") 
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse recalculateStock( HttpServletRequest httpRequest) {
		log.info("recalculatesting STOCK");
		return inventoryService.adjustStock(httpRequest);
	}
	@PostMapping("/getinventoriesdata") 
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse getInventoriesData( HttpServletRequest httpRequest) {
		log.info("getInventoriesData");
		return inventoryService.getInventoriesData(httpRequest);
	}
	 
}
