package com.pkm.medicalinventory.controller.api;

import javax.servlet.http.HttpServletRequest;

import com.pkm.medicalinventory.annotation.CustomRequestInfo;
import com.pkm.medicalinventory.inventory.TransactionService;
import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.dto.model.TransactionModel;
import com.pkm.medicalinventory.inventory.StockControlService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/app/transaction")
@Slf4j
public class TransactionController {

	@Autowired
	private TransactionService transactionService; 
	@Autowired
	private StockControlService stockControlService;

	public TransactionController() {
		log.info("------------------RestTransactionControllerr-----------------");
	}

	@PostMapping(
		value = "/transactionin",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse transactionin(@RequestBody WebRequest request) {
		log.info("transactionin ");
		return transactionService.performTransactionSupply(request);
	}
	@PostMapping(
		value = "/transactionout",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse transactionout(@RequestBody WebRequest request) {
		log.info("transactionout ");
		return transactionService.performDistribution(request);
	}
	@PostMapping(
		value = "/gettransaction/{code}",
		produces = MediaType.APPLICATION_JSON_VALUE
	) 
	public WebResponse gettransactionbycode(@PathVariable String code, HttpServletRequest httpRequest) {
		log.info("gettransactionbycode {}", code);
		return transactionService.getTransactionByCode(code);
	}
	@PostMapping(
		value = "/deleterecord/{code}",
		produces = MediaType.APPLICATION_JSON_VALUE
	) 
	public WebResponse deleteTransactionRecord(@PathVariable String code, HttpServletRequest httpRequest) {
		log.info("deletetransactionrecord {}", code);
		return transactionService.deleteRecordByCode(code);
	}
	@PostMapping(
		value = "/relatedrecord/{code}",
		produces = MediaType.APPLICATION_JSON_VALUE
	) 
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse getTransactionRelatedRecords(@PathVariable String code) {
		log.info("getTransactionRelatedRecords {}", code);
		TransactionModel rec = stockControlService.getTransactionRelatedRecords(code);
		return new WebResponse().withTransaction(rec);
	}

	 
}
