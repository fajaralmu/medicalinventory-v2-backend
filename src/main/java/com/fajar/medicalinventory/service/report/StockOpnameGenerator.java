package com.fajar.medicalinventory.service.report;

import java.util.Date;
import java.util.List;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fajar.medicalinventory.dto.ProductStock;
import com.fajar.medicalinventory.dto.model.ProductModel;
import com.fajar.medicalinventory.entity.HealthCenter;
import com.fajar.medicalinventory.util.DateUtil;

import jxl.write.Label;

public class StockOpnameGenerator extends BaseReportGenerator {

	private static final String DATE_PATTERN = "dd-MM-yyyy";
	private final HealthCenter location;
	private XSSFSheet sheet;
	private List<ProductStock> productStocks;
	private Date date;

	public StockOpnameGenerator(HealthCenter location, List<ProductStock> productStocks, Date date) {
		xwb = new XSSFWorkbook();
		this.location = location;
		this.date = date;
		this.productStocks = productStocks;
	}

	private void validateRow(int row) {
		if (sheet.getRow(row) == null) {
			sheet.createRow(row);
		}
	}

	private XSSFRow getRow(int row) {
		validateRow(row);
		return sheet.getRow(row);
	}

	public XSSFWorkbook generateReport() throws Exception {
		
		String dateString = DateUtil.formatDate(date, DATE_PATTERN);
		sheet = xwb.createSheet("Stock Opname " + dateString);
		Label labels[] = new Label[11];
		labels[0] = new Label(2, 3, "No");
		labels[1] = new Label(3, 3, "Nama");
		labels[2] = new Label(4, 3, "Satuan");
		labels[3] = new Label(5, 3, "Stok Awal");
		labels[4] = new Label(6, 3, "Pemasukan ");
		labels[5] = new Label(7, 3, "Penggunaan ");
		labels[6] = new Label(8, 3, "Sisa Stok");
		labels[7] = new Label(9, 3, "Harga Satuan per "+dateString);
		labels[8] = new Label(10, 3, "Harga Total");
		labels[9] = new Label(2, 1, "STOK OPNAME " + dateString);
		labels[10] = new Label(2, 2, location.getName().toUpperCase());

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 10));
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 2, 10));

		for (int i = 0; i < labels.length; i++) {
			Label label = labels[i];
			int row = label.getRow();

			XSSFRow xssfRow = getRow(row);
			xssfRow.createCell(label.getColumn()).setCellValue(label.getContents());

		}
		Double totalPrice = 0d;
		Integer totalCount = 0;
		Integer currentRow = 4, number = 1;

		for (ProductStock stock : productStocks) {
			XSSFRow xssfRow = getRow(currentRow);

			ProductModel ob = stock.getProduct();
			
			jxl.write.Number labelobat[] = new jxl.write.Number[7];
			labelobat[0] = new jxl.write.Number(2, currentRow, number);
			labelobat[1] = new jxl.write.Number(5, currentRow, stock.getPreviousStock());
			labelobat[2] = new jxl.write.Number(6, currentRow, stock.getTotalIncomingCount());
			labelobat[3] = new jxl.write.Number(7, currentRow, stock.getTotalUsedCount());
			labelobat[4] = new jxl.write.Number(8, currentRow, stock.getTotalStock());
			labelobat[5] = new jxl.write.Number(9, currentRow, ob.getPrice());
			Double total = ob.getPrice() * stock.getTotalStock();
			labelobat[6] = new jxl.write.Number(10, currentRow, total);
			totalPrice += total;
			totalCount += stock.getTotalStock();

			xssfRow.createCell(3).setCellValue(ob.getName()+"("+ob.getCode()+")");
			xssfRow.createCell(4).setCellValue(ob.getUnit().getName());

			for (jxl.write.Number labelobat1 : labelobat) {
				XSSFCell cell = xssfRow.createCell(labelobat1.getColumn());
				cell.setCellValue(Double.valueOf(labelobat1.getContents()));
			}
			currentRow++;
			number++;
		}

		XSSFRow xssfRow = getRow(currentRow);

		xssfRow.createCell(2).setCellValue("Total");
		xssfRow.createCell(5).setCellValue(Double.valueOf(totalCount));
		xssfRow.createCell(7).setCellValue(Double.valueOf(totalPrice));

		return xwb;

	}

}
