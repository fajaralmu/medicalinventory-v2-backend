package com.pkm.medicalinventory.report.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pkm.medicalinventory.dto.Filter;
import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.management.MasterDataService;
import com.pkm.medicalinventory.report.EntityReportService;
import com.pkm.medicalinventory.report.WritableReport;
import com.pkm.medicalinventory.report.ReportGenerator;
import com.pkm.medicalinventory.report.ReportService;
import com.pkm.medicalinventory.service.ProgressService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

	@Autowired
	private MasterDataService masterDataService;
	@Autowired
	private ReportGenerator reportGenerator;
	@Autowired
	private ProgressService progressService;
	@Autowired
	private EntityReportService entityReportService;

	public WritableReport printStockOpname(WebRequest webRequest) {
		Filter filter = webRequest.getFilter(); 
		String locationName = webRequest.getHealthcenter().getName();
		String fileName = "StockOpname_"+locationName+"_"+filter.getYear()+"-"+ filter.getMonth()+"-"+ filter.getDay();
//		httpServletResponse.setHeader("Content-disposition", "attachment;filename="+fileName+".xlsx");

		return reportGenerator.getStockOpnameReport(fileName, webRequest);
	}

	public WritableReport printMonthlyReport(WebRequest webRequest){
		Filter filter = webRequest.getFilter();
		String fileName = "Laporan-Bulanan-" + filter.getYear() + "-" + filter.getMonth();
//		httpServletResponse.setHeader("Content-disposition", "attachment;filename="+fileName+".xlsx");

		return reportGenerator.getMonthyReport(fileName, filter);
		
	}

	public WritableReport printRecipeReport(WebRequest webRequest) {

		Filter filter = webRequest.getFilter();
		String fileName = "Kesesuaian-Resep-Bulan-" + filter.getYear() + "-" + filter.getMonth();
//		httpServletResponse.setHeader("Content-disposition", "attachment;filename="+fileName+".xlsx");

		return reportGenerator.getRecipeReport(fileName, filter);
		
	}

	public WritableReport receiveRequestSheet(WebRequest webRequest) {

		Filter filter = webRequest.getFilter();
		String fileName = "LPLPO_"+webRequest.getHealthcenter().getName()+"_"+filter.getYear()+"-"+ filter.getMonth();
//		httpServletResponse.setHeader("Content-disposition", "attachment;filename="+fileName+".xls");
		
		return reportGenerator.generateLPLPO(fileName, webRequest);
	}

	public WritableReport generateTransactionReceipt(String code) {
		String fileName = "Struk-Transaksi_"+code;
//		httpServletResponse.setHeader("Content-disposition", "attachment;filename="+fileName+".pdf");
		return reportGenerator.generateTransactionReceipt(fileName, code);
	}

	public WritableReport generateEntityReport(WebRequest request) {
		Objects.requireNonNull(request);
		log.info("generateEntityReport, request: {}", request); 

		WebResponse response = masterDataService.filter(request); 
		progressService.sendProgress(1, 1, 20, true);
		return entityReportService.getEntityReport(response.getEntities(), response.getEntityClass());
	}

}
