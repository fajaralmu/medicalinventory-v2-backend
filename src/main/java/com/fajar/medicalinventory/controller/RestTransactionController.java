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
import com.fajar.medicalinventory.service.transaction.TransactionService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/app/transaction")
@Slf4j
public class RestTransactionController extends BaseController {

	@Autowired
	private TransactionService transactionService; 

	public RestTransactionController() {
		log.info("------------------RestTransactionControllerr-----------------");
	}

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	@PostMapping(value = "/transactionin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse transactionin(@RequestBody WebRequest request, HttpServletRequest httpRequest) {
		log.info("transactionin {}", request.getEntity());
		return transactionService.performTransactionIN(request, httpRequest);
	}
	@PostMapping(value = "/gettransaction/{code}", produces = MediaType.APPLICATION_JSON_VALUE) 
	public WebResponse gettransactionbycode(@PathVariable String code, HttpServletRequest httpRequest) {
		log.info("gettransactionbycode {}", code);
		return transactionService.getTransactionByCode(code);
	}

	 
}
