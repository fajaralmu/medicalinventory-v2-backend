package com.pkm.medicalinventory.controller;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pkm.medicalinventory.annotation.CustomRequestInfo;
import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.exception.ApplicationException;
import com.pkm.medicalinventory.service.report.CustomWorkbook;
import com.pkm.medicalinventory.service.transaction.ReportService;

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
@RequestMapping("/api/app/report")
@Slf4j
public class RestReportController {

	@Autowired
	private ReportService reportService;

	public RestReportController() {
		log.info("------------------RestReportController-----------------");
	}

	@PostMapping(value = "/stockopname", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public void stockopname(@RequestBody WebRequest req, HttpServletRequest httpRq, HttpServletResponse httpRs) throws Exception {
		log.info("stockopname {} at {}={} ", req.getHealthcenter().getName(), req.getFilter().getMonth(), req.getFilter().getYear());
		httpRs.setContentType("text/xls");
		httpRs.setHeader("Access-Control-Expose-Headers", "Content-disposition,access-token");
		
		reportService.printStockOpname(req, httpRq, httpRs);
	}
	@PostMapping(value = "/monthly", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public void monthly(@RequestBody WebRequest req, HttpServletRequest httpRq, HttpServletResponse httpRs) throws Exception {
		log.info("monthly report");
		httpRs.setContentType("text/xls");
		httpRs.setHeader("Access-Control-Expose-Headers", "Content-disposition,access-token");
		
		reportService.printMonthlyReport(req, httpRq, httpRs);
	}
	@PostMapping(value = "/recipe", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public void recipe(@RequestBody WebRequest req, HttpServletRequest httpRq, HttpServletResponse httpRs) throws Exception {
		log.info("recipe report");
		httpRs.setContentType("text/xls");
		httpRs.setHeader("Access-Control-Expose-Headers", "Content-disposition,access-token");
		
		reportService.printRecipeReport(req, httpRq, httpRs);
	}
	
	@PostMapping(value = "/receiverequestsheet", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public void receiveRequestSheet(@RequestBody WebRequest req, HttpServletRequest httpRq, HttpServletResponse httpRs) throws Exception {
		log.info("receiverequestsheet");
		httpRs.setContentType("text/xls");
		httpRs.setHeader("Access-Control-Expose-Headers", "Content-disposition,access-token");
		
		reportService.receiveRequestSheet(req, httpRq, httpRs);
	}
	@PostMapping(value = "/transactionreceipt/{code}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public void transactionReceipt(@PathVariable(name="code") String code, HttpServletRequest httpRq, HttpServletResponse httpRs) throws Exception {
		log.info("transactionreceipt with code: {}", code);
		httpRs.setContentType("text/pdf");
		httpRs.setHeader("Access-Control-Expose-Headers", "Content-disposition,access-token");
		
		reportService.generateTransactionReceipt(code, httpRq, httpRs);
	}
	
	@PostMapping(value = "/records", consumes = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public void recordsReport(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		try {
			 
			log.info("entityreport {}", request);

			CustomWorkbook result = reportService.generateEntityReport(request, httpRequest);

			writeXSSFWorkbook(httpResponse, result);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new ApplicationException(e);
		}
	}
	
	public static void writeXSSFWorkbook(HttpServletResponse httpResponse, CustomWorkbook xwb) throws Exception {
		httpResponse.setContentType("text/xls");
		httpResponse.setHeader("Access-Control-Expose-Headers", "Content-disposition,access-token");
		httpResponse.setHeader("Content-disposition", "attachment;filename=" + xwb.getFileName());

		try (OutputStream outputStream = httpResponse.getOutputStream()) {
			xwb.write(outputStream);
		}
	}

}
