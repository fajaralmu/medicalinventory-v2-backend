package com.fajar.medicalinventory.controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.medicalinventory.annotation.CustomRequestInfo;
import com.fajar.medicalinventory.dto.WebRequest;
import com.fajar.medicalinventory.service.LogProxyFactory;
import com.fajar.medicalinventory.service.transaction.ReportService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/app/report")
@Slf4j
public class RestReportController extends BaseController {

	@Autowired
	private ReportService reportService;

	public RestReportController() {
		log.info("------------------RestReportController-----------------");
	}

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	@PostMapping(value = "/stockopname", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public void stockopname(@RequestBody WebRequest webRequest, HttpServletRequest httpRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		log.info("stockopname {} at {} ", webRequest.getHealthcenter().getName());
		httpServletResponse.setContentType("text/xls");
		httpServletResponse.setHeader("Access-Control-Expose-Headers", "Content-disposition,access-token");
		
		reportService.printStockOpname(webRequest, httpRequest, httpServletResponse);
	}
	@PostMapping(value = "/monthly", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public void monthly(@RequestBody WebRequest webRequest, HttpServletRequest httpRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		log.info("monthly report");
		httpServletResponse.setContentType("text/xls");
		httpServletResponse.setHeader("Access-Control-Expose-Headers", "Content-disposition,access-token");
		
		reportService.printMonthlyReport(webRequest, httpRequest, httpServletResponse);
	}
	
	@PostMapping(value = "/receiverequestsheet", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public void receiveRequestSheet(@RequestBody WebRequest webRequest, HttpServletRequest httpRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		log.info("receiverequestsheet");
		httpServletResponse.setContentType("text/xls");
		httpServletResponse.setHeader("Access-Control-Expose-Headers", "Content-disposition,access-token");
		
		reportService.receiveRequestSheet(webRequest, httpRequest, httpServletResponse);
	}

}
