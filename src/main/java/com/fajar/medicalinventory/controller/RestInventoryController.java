package com.fajar.medicalinventory.controller;

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
import com.fajar.medicalinventory.service.inventory.InventoryService;
import com.fajar.medicalinventory.service.transaction.ProductStatisticService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/app/inventory")
@Slf4j
public class RestInventoryController extends BaseController {

	@Autowired
	private InventoryService inventoryService; 
	@Autowired
	private ProductStatisticService productStatisticService;

	public RestInventoryController() {
		log.info("------------------RestInventoryController-----------------");
	}
 
 
	@PostMapping(value = "/availableproducts/{code}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE) 
	public WebResponse gettransactionbycode(@PathVariable String code, @RequestBody WebRequest webRequest, HttpServletRequest httpRequest) {
		log.info("availableproducts {} at {} ", code, webRequest.getHealthcenter().getName());
		return inventoryService.getAvailableProductStocks(code, webRequest);
	}
	@PostMapping(value = "/getproducts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE) 
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse getproductsAtLocation(@RequestBody WebRequest webRequest, HttpServletRequest httpRequest) {
		log.info("getproducts   at {} ", webRequest.getHealthcenter().getName());
		return inventoryService.getProductStocks( webRequest, httpRequest);
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
	@PostMapping(value = "/getproductusage", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE) 
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse getProductUsageByCode( @RequestBody WebRequest webRequest, HttpServletRequest httpRequest) {
		log.info("getProductUsage");
		String productCode = webRequest.getProduct().getCode();
		return productStatisticService.getProductUsageByCode(productCode, webRequest, httpRequest);
	}
	@PostMapping(value = "/getproductswithusage", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE) 
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse getProductsWithUsage( @RequestBody WebRequest webRequest, HttpServletRequest httpRequest) {
		log.info("getProductsWithUsage");
		 
		return productStatisticService.getProductListWithUsage( webRequest, httpRequest);
	}
	@PostMapping(value = "/filter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE) 
	public WebResponse filter( @RequestBody WebRequest webRequest, HttpServletRequest httpRequest) {
		log.info("filter stock");
		
		return inventoryService.filterStock(webRequest);
	}
	 
}
