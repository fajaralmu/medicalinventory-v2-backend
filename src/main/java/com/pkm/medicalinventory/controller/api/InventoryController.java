package com.pkm.medicalinventory.controller.api;

import javax.servlet.http.HttpServletRequest;

import com.pkm.medicalinventory.annotation.CustomRequestInfo;
import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.inventory.InventoryService;
import com.pkm.medicalinventory.inventory.ProductStatisticService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/app/inventory")
@Slf4j
public class InventoryController {

	@Autowired
	private InventoryService inventoryService; 
	@Autowired
	private ProductStatisticService productStatisticService;

	public InventoryController() {
		log.info("------------------RestInventoryController-----------------");
	}
 
 
	@PostMapping(
		value = "/availableproducts",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	) 
	public WebResponse gettransactionbycode(
		@RequestParam("code") String code, @RequestBody
		WebRequest webRequest
	) {
		log.info("availableproducts {} at {} ", code, webRequest.getHealthcenter().getName());
		return inventoryService.getAvailableProductStocks(code, webRequest);
	}
	@PostMapping(
		value = "/getproducts",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	) 
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse getproductsAtLocation(
		@RequestBody WebRequest webRequest
	) {
		log.info("getproducts   at {} ", webRequest.getHealthcenter().getName());
		return inventoryService.getProductStocks(webRequest);
	}
	
	@PostMapping("/recalculatestock") 
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse recalculateStock() {
		log.info("recalculatesting STOCK");
		inventoryService.adjustStock();
		return new WebResponse();
	}
	@PostMapping("/getinventoriesdata") 
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse getInventoriesData() {
		log.info("getInventoriesData");
		return inventoryService.getInventoriesData();
	}
	@PostMapping(
		value = "/getproductusage",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	) 
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse getProductUsageByCode(
		@RequestBody WebRequest webRequest
	) {
		log.info("getProductUsage");
		String productCode = webRequest.getProduct().getCode();
		return productStatisticService.getProductUsageByCode(productCode, webRequest);
	}
	@PostMapping(
		value = "/getproductswithusage",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	) 
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse getProductsWithUsage(
		@RequestBody WebRequest webRequest
	) {
		log.info("getProductsWithUsage");
		return productStatisticService.getProductListWithUsage(webRequest);
	}
	@PostMapping(
		value = "/filter",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	) 
	public WebResponse filter(@RequestBody WebRequest webRequest) {
		log.info("filter stock");
		return inventoryService.filterStock(webRequest);
	}
	 
}
