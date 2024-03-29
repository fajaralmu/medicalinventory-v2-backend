package com.pkm.medicalinventory.report.builder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pkm.medicalinventory.dto.ProductStock;
import com.pkm.medicalinventory.dto.model.ProductModel;
import com.pkm.medicalinventory.entity.HealthCenter;
import com.pkm.medicalinventory.report.WritableReport;
import com.pkm.medicalinventory.util.DateUtil;

import jxl.write.Label;

public class StockOpnameBuilder extends BaseReportBuilder {
	private static final String DATE_PATTERN = "dd-MM-yyyy";
	
	private final HealthCenter location;
	private final List<ProductStock> productStocks;
	private final Date date;
	private final Integer year;
	private final Map<Long, Double> mappedStartingStockPrice;
	
	private final XSSFWorkbook xwb;
	private final XSSFSheet sheet;

	public StockOpnameBuilder(
		HealthCenter location,
		List<ProductStock> productStocks,
		Map<Long, Double> mappedBeginningStockPrice,
		Date date,
		int year,
		String fileName
	) {
		super(fileName);
		this.xwb = new XSSFWorkbook();
		this.location = location;
		this.date = date;
		this.year = year;
		this.productStocks = productStocks;
		this.productStocks.sort((a, b) -> a.getProduct().getName().toLowerCase().compareTo(b.getProduct().getName().toLowerCase()));
		this.mappedStartingStockPrice = mappedBeginningStockPrice;

		
		String dateString = DateUtil.formatDate(date, DATE_PATTERN);
		sheet = xwb.createSheet("Stock Opname " + dateString);
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

	public WritableReport build() {
		
		String dateString = DateUtil.formatDate(date, DATE_PATTERN);
		List<Label> labels  = new ArrayList<>();
		labels.add(new Label(2, 3, "No"));
		labels.add(new Label(3, 3, "Nama"));
		labels.add(new Label(4, 3, "Satuan"));
		labels.add(new Label(5, 3, "Stok Awal Tahun "+year));
		labels.add(new Label(6, 3, "Harga @"));
		labels.add(new Label(7, 3, "Harga"));
		labels.add(new Label(8, 3, "Pemasukan "+year));
		labels.add(new Label(9, 3, "Harga"));
		labels.add(new Label(10, 3, "Penggunaan "+year));
		labels.add(new Label(11, 3, "Harga"));
		labels.add(new Label(12, 3, "Sisa Stok"));
		labels.add(new Label(13, 3, "Harga Satuan per "+dateString));
		labels.add(new Label(14, 3, "Harga Total"));
		labels.add(new Label(2, 1, "STOK OPNAME " + dateString));
		labels.add(new Label(2, 2, location.getName().toUpperCase()));

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 14));
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 2, 14));

		for (int i = 0; i < labels.size(); i++) {
			Label label = labels.get(i);
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
			
			List<jxl.write.Number> labelobat  = new ArrayList<>();
			labelobat.add( new jxl.write.Number(2, currentRow, number));
			//prev stock beginning of year
			Double prevPrice = mappedStartingStockPrice.get(ob.getId());
			labelobat.add(new jxl.write.Number(5, currentRow, stock.getPreviousStock()));
			labelobat.add(new jxl.write.Number(6, currentRow, prevPrice));
			labelobat.add(new jxl.write.Number(7, currentRow, (stock.getPreviousStock() * prevPrice)));
			
			//incoming
			labelobat.add( new jxl.write.Number(8, currentRow, stock.getTotalIncomingCount()));
			labelobat.add( new jxl.write.Number(9, currentRow, stock.getIncomingPrice()));

			//usage
			labelobat.add( new jxl.write.Number(10, currentRow, stock.getTotalUsedCount()));
			labelobat.add( new jxl.write.Number(11, currentRow, stock.getUsedPrice()));
			
			labelobat.add(new jxl.write.Number(12, currentRow, stock.getTotalStock()));
			labelobat.add(new jxl.write.Number(13, currentRow, ob.getPrice()));
			
			Double stockPrice = ob.getPrice() * stock.getTotalStock();
			labelobat.add( new jxl.write.Number(14, currentRow, stockPrice));
			totalPrice += stockPrice;
			totalCount += stock.getTotalStock();

			xssfRow.createCell(3).setCellValue(ob.getName());
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
		xssfRow.createCell(13).setCellValue(Double.valueOf(totalCount));
		xssfRow.createCell(14).setCellValue(totalPrice);

		return new XSSFPrintableReport(xwb, fileName);

	}
}
