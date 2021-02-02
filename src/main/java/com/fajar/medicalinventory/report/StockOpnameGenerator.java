package com.fajar.medicalinventory.report;

import java.util.Date;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fajar.medicalinventory.entity.HealthCenter;
import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.util.DateUtil;

import jxl.write.Label;

public class StockOpnameGenerator {

	private static final String DATE_PATTERN = "dd-MM-yyyy";
	private final XSSFWorkbook wb;
	private final HealthCenter location;
	private XSSFSheet sheet;
	private List<Product> productsAndItsPrice;
	private Date date;

	public StockOpnameGenerator(HealthCenter location, List<Product> productsAndItsPrice, Date date) {
		wb = new XSSFWorkbook();
		this.location = location;
		this.date = date;
		this.productsAndItsPrice = productsAndItsPrice;
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
		sheet = wb.createSheet("Stock Opname " + DateUtil.formatDate(date, DATE_PATTERN));
		Label labels[] = new Label[8];
		labels[0] = new Label(2, 3, "No");
		labels[1] = new Label(3, 3, "Nama");
		labels[2] = new Label(4, 3, "Satuan");
		labels[3] = new Label(5, 3, "Sisa Stok");
		labels[4] = new Label(6, 3, "Harga Satuan");
		labels[5] = new Label(7, 3, "Harga Total");
		labels[6] = new Label(2, 1, "STOK OPNAME " + DateUtil.formatDate(date, DATE_PATTERN));
		labels[7] = new Label(2, 2, location.getName().toUpperCase());

		sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, 2, 7));
		sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(2, 2, 2, 7));

		for (int i = 0; i < labels.length; i++) {
			Label label = labels[i];
			int row = label.getRow();

			XSSFRow xssfRow = getRow(row);
			xssfRow.createCell(label.getColumn()).setCellValue(label.getContents());

		}
		Integer totalPrice = 0, totalCount = 0;
		Integer currentRow = 4, number = 1;

		for (Product ob : productsAndItsPrice) {
			XSSFRow xssfRow = getRow(currentRow);

			jxl.write.Number labelobat[] = new jxl.write.Number[4];
			labelobat[0] = new jxl.write.Number(2, currentRow, number);
			labelobat[1] = new jxl.write.Number(5, currentRow, ob.getCount());
			labelobat[2] = new jxl.write.Number(6, currentRow, ob.getPrice());
			Integer total = ob.getPrice() * ob.getCount();
			labelobat[3] = new jxl.write.Number(7, currentRow, total);
			totalPrice += total;
			totalCount += ob.getCount();

			xssfRow.createCell(3).setCellValue(ob.getName());
			xssfRow.createCell(4).setCellValue(ob.getUnit().getName());

			for (jxl.write.Number labelobat1 : labelobat) {
				XSSFCell cell = xssfRow.createCell(labelobat1.getColumn());
				cell.setCellValue(labelobat1.getContents());
			}
			currentRow++;
			number++;
		}

		XSSFRow xssfRow = getRow(currentRow);

		xssfRow.createCell(2).setCellValue("Total");
		xssfRow.createCell(5).setCellValue(totalCount);
		xssfRow.createCell(7).setCellValue(totalPrice);

		return wb;

	}

}
