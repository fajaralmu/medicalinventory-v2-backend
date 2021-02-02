package com.fajar.medicalinventory.service.transaction;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.dto.Filter;
import com.fajar.medicalinventory.dto.WebRequest;
import com.fajar.medicalinventory.report.ReportGenerator;
import com.fajar.medicalinventory.util.DateUtil;

@Service
public class ReportService {

	@Autowired
	private ReportGenerator reportGenerator;

	public void printStockOpname(WebRequest webRequest, HttpServletRequest httpRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		Filter filter = webRequest.getFilter();
		Date date = DateUtil.getDate(filter);
		String locationName = webRequest.getHealthcenter().getName();
		String fileName = "StockOpname_"+locationName+"_"+filter.getYear()+ filter.getMonth()+ filter.getDay();
		httpServletResponse.setHeader("Content-disposition", "attachment;filename="+fileName+".xlsx");

		XSSFWorkbook wb = reportGenerator.getStockOpnameReport(date, webRequest.getHealthcenter(), httpRequest);
		wb.write(httpServletResponse.getOutputStream());
		
		
	}

	public void printMonthlyReport(WebRequest webRequest, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		Filter filter = webRequest.getFilter();
		 
		String fileName = "MONTHLY_"+filter.getYear()+"-"+ filter.getMonth();
		httpServletResponse.setHeader("Content-disposition", "attachment;filename="+fileName+".xlsx");

		XSSFWorkbook wb = reportGenerator.getMonthyReport(filter.getMonth(), filter.getYear(), httpServletRequest);
		wb.write(httpServletResponse.getOutputStream());
		
	}

}
