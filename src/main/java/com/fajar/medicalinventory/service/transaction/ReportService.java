package com.fajar.medicalinventory.service.transaction;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.dto.Filter;
import com.fajar.medicalinventory.dto.WebRequest;
import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.service.ProgressService;
import com.fajar.medicalinventory.service.entity.EntityReportService;
import com.fajar.medicalinventory.service.entity.MasterDataService;
import com.fajar.medicalinventory.service.report.CustomWorkbook;
import com.fajar.medicalinventory.service.report.ReportGenerator;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportService {

	@Autowired
	private MasterDataService masterDataService;
	@Autowired
	private ReportGenerator reportGenerator;
	@Autowired
	private ProgressService progressService;
	@Autowired
	private EntityReportService entityReportService;

	public void printStockOpname(WebRequest webRequest, HttpServletRequest httpRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		Filter filter = webRequest.getFilter(); 
		String locationName = webRequest.getHealthcenter().getName();
		String fileName = "StockOpname_"+locationName+"_"+filter.getYear()+"-"+ filter.getMonth()+"-"+ filter.getDay();
		httpServletResponse.setHeader("Content-disposition", "attachment;filename="+fileName+".xlsx");

		XSSFWorkbook wb = reportGenerator.getStockOpnameReport(webRequest , httpRequest);
		wb.write(httpServletResponse.getOutputStream()); 
		
	}

	public void printMonthlyReport(WebRequest webRequest, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		Filter filter = webRequest.getFilter();
		String fileName = "MONTHLY_"+filter.getYear()+"-"+ filter.getMonth();
		httpServletResponse.setHeader("Content-disposition", "attachment;filename="+fileName+".xlsx");

		XSSFWorkbook wb = reportGenerator.getMonthyReport(filter, httpServletRequest);
		wb.write(httpServletResponse.getOutputStream());
		
	}

	public void receiveRequestSheet(WebRequest webRequest, HttpServletRequest httpRequest,
			HttpServletResponse httpServletResponse) throws   Exception {
		Filter filter = webRequest.getFilter();
		String fileName = "LPLPO_"+webRequest.getHealthcenter().getName()+"_"+filter.getYear()+"-"+ filter.getMonth();
		httpServletResponse.setHeader("Content-disposition", "attachment;filename="+fileName+".xls");
		
		reportGenerator.generateProductRequestSheet(webRequest , httpServletResponse.getOutputStream(), httpRequest);
	}

	public void generateTransactionReceipt(String code, HttpServletRequest httpRequest,
			HttpServletResponse httpServletResponse) throws IOException, Exception {
		String fileName = "Receipt_"+code;
		httpServletResponse.setHeader("Content-disposition", "attachment;filename="+fileName+".pdf");
		reportGenerator.transactionReceipt(code, httpRequest, httpServletResponse.getOutputStream());
	}

	public CustomWorkbook generateEntityReport(WebRequest request, HttpServletRequest httpRequest) throws Exception {
		Objects.requireNonNull(request);
		log.info("generateEntityReport, request: {}", request); 

		WebResponse response = masterDataService.filter(request, null); 
		progressService.sendProgress(1, 1, 20, true, httpRequest);
		CustomWorkbook file = entityReportService.getEntityReport(response.getEntities(), response.getEntityClass(), httpRequest);
		return file;
	}

}
