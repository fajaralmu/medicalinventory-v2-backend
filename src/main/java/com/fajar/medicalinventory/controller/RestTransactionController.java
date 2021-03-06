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
import com.fajar.medicalinventory.service.inventory.StockControlService;
import com.fajar.medicalinventory.service.transaction.TransactionService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/app/transaction")
@Slf4j
public class RestTransactionController extends BaseController {

	@Autowired
	private TransactionService transactionService; 
	@Autowired
	private StockControlService stockControlService;

	public RestTransactionController() {
		log.info("------------------RestTransactionControllerr-----------------");
	}

	@PostMapping(value = "/transactionin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse transactionin(@RequestBody WebRequest request, HttpServletRequest httpRequest) {
		log.info("transactionin ");
		return transactionService.performTransactionSupply(request, httpRequest);
	}
	@PostMapping(value = "/transactionout", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse transactionout(@RequestBody WebRequest request, HttpServletRequest httpRequest) {
		log.info("transactionout ");
		return transactionService.performDistribution(request, httpRequest);
	}
	@PostMapping(value = "/gettransaction/{code}", produces = MediaType.APPLICATION_JSON_VALUE) 
	public WebResponse gettransactionbycode(@PathVariable String code, HttpServletRequest httpRequest) {
		log.info("gettransactionbycode {}", code);
		return transactionService.getTransactionByCode(code);
	}
	@PostMapping(value = "/deleterecord/{code}", produces = MediaType.APPLICATION_JSON_VALUE) 
	public WebResponse deleteTransactionRecord(@PathVariable String code, HttpServletRequest httpRequest) {
		log.info("deletetransactionrecord {}", code);
		return transactionService.deleteRecordByCode(code);
	}
	@PostMapping(value = "/relatedrecord/{code}", produces = MediaType.APPLICATION_JSON_VALUE) 
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse getTransactionRelatedRecords(@PathVariable String code, HttpServletRequest httpRequest) {
		log.info("getTransactionRelatedRecords {}", code);
		return stockControlService.getTransactionRelatedRecords(code, httpRequest);
	}

	 
}
