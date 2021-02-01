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
		Date d = DateUtil.getDate(filter.getYear(), filter.getMonth()-1, filter.getDay());
		String fileName = "StockOpname-"+webRequest.getHealthcenter().getName()+filter.getYear()+ filter.getMonth()+ filter.getDay();
		httpServletResponse.setHeader("Content-disposition", "attachment;filename="+fileName+".xlsx");

		XSSFWorkbook wb = reportGenerator.getStockOpnameReport(d, webRequest.getHealthcenter(), httpRequest);
		wb.write(httpServletResponse.getOutputStream());
		
		
	}

}
