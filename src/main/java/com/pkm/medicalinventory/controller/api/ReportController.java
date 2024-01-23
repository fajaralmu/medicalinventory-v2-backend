package com.pkm.medicalinventory.controller.api;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pkm.medicalinventory.annotation.CustomRequestInfo;
import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.report.ReportService;
import com.pkm.medicalinventory.report.WritableReport;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/app/report")
@Slf4j
public class ReportController {

	@Autowired
	private ReportService reportService;

	@PostMapping(
		value = "/stockopname",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@CustomRequestInfo(withRealtimeProgress = true)
	public void stockopname(
		@RequestBody WebRequest req,
		HttpServletRequest httpRq,
		HttpServletResponse httpRs
	) throws Exception {
		log.info("stockopname {} at {}={} ", req.getHealthcenter().getName(), req.getFilter().getMonth(), req.getFilter().getYear());
		WritableReport report = reportService.printStockOpname(req);
		writeReportFile(httpRs, report, "text/xls", "xlsx");
	}
	@PostMapping(
		value = "/monthly",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@CustomRequestInfo(withRealtimeProgress = true)
	public void monthly(
		@RequestBody WebRequest req,
		HttpServletResponse httpRs
	) throws Exception {
		log.info("monthly report");
		WritableReport report = reportService.printMonthlyReport(req);
		writeReportFile(httpRs, report, "text/xls", "xlsx");
	}
	@PostMapping(
		value = "/recipe",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@CustomRequestInfo(withRealtimeProgress = true)
	public void recipe(
		@RequestBody WebRequest req,
		HttpServletResponse httpRs
	) throws Exception {
		log.info("recipe report");
		WritableReport report = reportService.printRecipeReport(req);
		writeReportFile(httpRs, report, "text/xls", "xlsx");
	}
	
	@PostMapping(
		value = "/receiverequestsheet",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@CustomRequestInfo(withRealtimeProgress = true)
	public void receiveRequestSheet(
		@RequestBody WebRequest req,
		HttpServletResponse httpRs
	) throws Exception {
		log.info("LPLPO");
		WritableReport report = reportService.receiveRequestSheet(req);
		writeReportFile(httpRs, report, "text/xls", "xls");
	}
	@PostMapping(
		value = "/transactionreceipt/{code}",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@CustomRequestInfo(withRealtimeProgress = true)
	public void transactionReceipt(
		@PathVariable(name="code") String code,
		HttpServletRequest httpRq,
		HttpServletResponse httpRs
	) throws Exception {
		log.info("transactionreceipt with code: {}", code);
		
		WritableReport report = reportService.generateTransactionReceipt(code);
		writeReportFile(httpRs, report, "text/pdf", "pdf");
	}
	
	@PostMapping(
		value = "/records",
		consumes = MediaType.APPLICATION_JSON_VALUE
	)
	@CustomRequestInfo(withRealtimeProgress = true)
	public void recordsReport(
		@RequestBody WebRequest request,
		HttpServletResponse httpResponse
	) throws Exception {
		log.info("entityreport {}", request);
		WritableReport result = reportService.generateEntityReport(request);

		writeReportFile(httpResponse, result, "text/xls", "xlsx");
	}
	
	private static void writeReportFile(HttpServletResponse httpResponse, WritableReport report, String contentType, String ext) throws Exception {
		httpResponse.setContentType(contentType);
		httpResponse.setHeader("Access-Control-Expose-Headers", "Content-disposition,access-token");
		httpResponse.setHeader("Content-disposition", "attachment;filename=" + report.getFileName()+"."+ext);

		try (OutputStream outputStream = httpResponse.getOutputStream()) {
			report.write(outputStream);
		}
	}

}
