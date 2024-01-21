package com.pkm.medicalinventory.report.builder;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pkm.medicalinventory.exception.ApplicationException;
import com.pkm.medicalinventory.report.WritableReport;

public class XSSFPrintableReport implements WritableReport {

	private final XSSFWorkbook workbook;
	private final String fileName;
	public XSSFPrintableReport(XSSFWorkbook wb, String fileName) {
		this.workbook = wb;
		this.fileName = fileName;
	}
	@Override
	public String getFileName() {
		return fileName;
	}
	@Override
	public void write(OutputStream os) {
		try {
			this.workbook.write(os);
		} catch (IOException e) {
			throw new ApplicationException(e);
		}
	}
}
