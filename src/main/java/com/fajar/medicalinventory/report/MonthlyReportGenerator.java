package com.fajar.medicalinventory.report;

import static com.fajar.medicalinventory.constants.TransactionType.TRANS_OUT;
import static com.fajar.medicalinventory.constants.TransactionType.TRANS_OUT_TO_WAREHOUSE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.dto.Filter;
import com.fajar.medicalinventory.entity.HealthCenter;
import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.entity.Transaction;
import com.fajar.medicalinventory.service.ProgressService;
import com.fajar.medicalinventory.util.DateUtil;
import com.fajar.medicalinventory.util.EntityUtil;

public class MonthlyReportGenerator {
	private final XSSFWorkbook xwb;
	private final Map<Integer, List<Transaction>> transactionMapped;
	private final List<Transaction> transactionsOneMonth;
	private final int month, year;
	private final List<Product> allProducts;
	private final List<HealthCenter> locations;
	private List<DailyConsumption> dailyConsumptionPerProduct = new ArrayList<>();
	private List<Product> productListPerDay = new ArrayList<>();
	private final XSSFCellStyle productNameStyle;
	private final XSSFCellStyle regularStyle;
	final ProgressNotifier progressNotifier;

	public MonthlyReportGenerator(Filter filter, List<Transaction> transactionsOneMonth, List<Product> products,
			List<HealthCenter> locations, ProgressNotifier progressNotifier) {
		this.transactionsOneMonth = transactionsOneMonth;
		this.xwb = new XSSFWorkbook();
		this.month = filter.getMonth();
		this.year = filter.getYear();
		this.transactionMapped = mapTransactionByDay(transactionsOneMonth);
		this.allProducts = products;
		this.locations = locations;
		this.productNameStyle = createProductNameStyle(xwb);
		this.regularStyle = createRegularStyle(xwb);
		this.progressNotifier = progressNotifier;

	}

	private Map<Integer, List<Transaction>> mapTransactionByDay(List<Transaction> transactions) {
		Map<Integer, List<Transaction>> map = new HashMap<>();
		for (Transaction transaction : transactions) {
			int day = DateUtil.getCalendarDayOfMonth(transaction.getTransactionDate());
			if (map.get(day) == null) {
				map.put(day, new ArrayList<>());
			}
			map.get(day).add(transaction);
		}
		return map;
	}

	public XSSFWorkbook generateReport(ProgressService progressService, HttpServletRequest httpServletRequest) {

		allProducts.forEach(p -> {
			p.setCount(0);
			productListPerDay.add(p);
		});
		

		/**************** BEGIN DAILY CONSUMPTION ***********************/
		mainLoop: for (Integer day = 1; day <= 31; day++) {
			List<Product> productsPerDay = (List<Product>) EntityUtil.cloneSerializable((Serializable)allProducts);
			int allProductCountDaily = 0;
			// JUDUL TABEL//
			XSSFSheet sheet = xwb.createSheet(day.toString());
			createProductNameCells(sheet.createRow(3), sheet);

			boolean lanjut = false;

			// KONTEN TABEL//
			Integer no = 1;
			int row = 4;
			List<Transaction> transactions = transactionMapped.get(day) == null ? new ArrayList<>()
					: transactionMapped.get(day);
			for (Transaction transaction : transactions) {

				if (!transaction.getType().equals(TransactionType.TRANS_OUT)
						|| transaction.getCustomer() == null) {
					continue;
				}
				lanjut = true;

				XSSFRow cunsumptionRow = sheet.createRow(row);
				XSSFCell[] xkolomRincian = new XSSFCell[4 + productCount()];

				xkolomRincian[0] = createCellWithString(cunsumptionRow, 2, String.valueOf(no));
				xkolomRincian[1] = createCellWithString(cunsumptionRow, 3, transaction.getCustomer().getName());
				xkolomRincian[2] = createCellWithString(cunsumptionRow, 4,
						transaction.getHealthCenterLocation().getName());
				sheet.autoSizeColumn(3);

				Integer totalProductsPerCustomer = 0;
				for (ProductFlow ao : transaction.getProductFlows()) {
					totalProductsPerCustomer += ao.getCount();
				}
				allProductCountDaily += totalProductsPerCustomer;

				xkolomRincian[3] = cunsumptionRow.createCell(5);
				xkolomRincian[3].setCellValue(totalProductsPerCustomer);

				// rincian obat per orang
				int kolomObatPerOrang = 5;
				int idxObat = 3;
				for (Product product : productsPerDay) {
					idxObat++;
					kolomObatPerOrang++;
					for (ProductFlow productFlow : transaction.getProductFlows()) {
						if (productFlow.getProduct().idEquals(product)) {
							product.addCount(productFlow.getCount()); 
							
							xkolomRincian[idxObat] = cunsumptionRow.createCell(kolomObatPerOrang);
							xkolomRincian[idxObat].setCellValue(productFlow.getCount());
						} else {
						}
					}
				}
				no++;
				row++;
				setRegularStyle(xkolomRincian);
			}
//			// Jumlah obat
//			if (!lanjut) {
//				progressNotifier.nofity(1, 31, 50);
//				continue mainLoop;
//			}
			sheet.addMergedRegion(new CellRangeAddress(row, row, 2, 4));
			XSSFRow summaryRow = sheet.createRow(row);
			XSSFCell[] summaryCells = new XSSFCell[6 + productCount()];
			summaryCells[0] = createCellWithString(summaryRow, 2, "Jumlah");
			summaryCells[1] = summaryRow.createCell(3);
			summaryCells[2] = summaryRow.createCell(4);
			summaryCells[3] = summaryRow.createCell(5);

			DailyConsumption dailyConsumption = new DailyConsumption(allProductCountDaily, day);
			dailyConsumptionPerProduct.add(dailyConsumption);

			int columnProductPerDay = 6;
			int idxTotal = 5;
			for (Product o : productsPerDay) {
				idxTotal++;
				for (DailyConsumption totalConsumption : dailyConsumptionPerProduct) {
					if (totalConsumption.getDay().equals(day)) {
						DrugConsumption consumption = new DrugConsumption(day, o.getCount(), o.getCode());
						totalConsumption.addConsumption(consumption);
					}
					summaryCells[idxTotal] = summaryRow.createCell(columnProductPerDay);
					summaryCells[idxTotal].setCellValue(o.getCount());
				}
				columnProductPerDay++;
			}

			summaryCells[4] = createCellWithNumber(summaryRow, 5, (double) allProductCountDaily);

			setRegularStyle(summaryCells);

			progressNotifier.nofity(1, 31, 50);
		}
		/**************** END DAILY CONSUMPTION ***********************/

		writeSummary();

		return xwb;
	}

	private List<Transaction> filterTransactionByLocation(HealthCenter healthCenter) {

		List<Transaction> result = new ArrayList<>();
		for (Transaction transaction : transactionsOneMonth) {
			TransactionType type = transaction.getType();
			boolean isTransOut = (type.equals(TRANS_OUT) || type.equals(TRANS_OUT_TO_WAREHOUSE));
			if (isTransOut && transaction.getHealthCenterLocation().idEquals(healthCenter)) {
				result.add(transaction);
			}
		}
		return result;
	}

	private void writeSummary( ) {

		/**************** BEGIN SUMMARY ***********************/
		XSSFSheet xsheetpkm = xwb.createSheet("Rincian Per Puskesmas Bulan " + month);
		XSSFRow titleRow = xsheetpkm.createRow(3);
		createSummaryTableHeader(xsheetpkm, titleRow);
		writeProductNames(titleRow);

		int totalProductsAllLocation = 0;
		int column = 5, locationRowNum = 4;
		progressNotifier.nofity(10,10,10);

		// ************************SHEET TERAKHIR***********************//
		List<Product> productsTotal = (List<Product>) SerializationUtils.clone((Serializable) allProducts);
		productsTotal.forEach(p -> p.setCount(0));

		for (HealthCenter location : locations) {
			int totalProductsPerLocation = 0;

			XSSFRow locationRow = xsheetpkm.createRow(locationRowNum);
			XSSFCell[] rowDataCells = new XSSFCell[4 + productCount()];

			rowDataCells[0] = createCellWithString(locationRow, 2, String.valueOf(locationRowNum - 3));
			rowDataCells[1] = createCellWithString(locationRow, 3, location.getName());

			List<Product> productsPerLocation = (List<Product>) SerializationUtils.clone((Serializable) allProducts);
			List<Transaction> transactions = filterTransactionByLocation(location);

			productsPerLocation.forEach(p -> p.setCount(0));
			int locationRowItem = 0;

			for (Product product : productsPerLocation) {

				int count = getCountProduct(product, transactions);
				product.addCount(count);
				rowDataCells[locationRowItem + 4] = createCellWithNumber(locationRow, column,
						(double) product.getCount());
				locationRowItem++;
				column++;
				totalProductsPerLocation += product.getCount();

				// *********************KOLOM TOTAL OBAT*************************//
				for (Product productTotal : productsTotal) {
					if (productTotal.idEquals(product)) {
						productTotal.addCount(product.getCount());
					}
				}
			}
			rowDataCells[2] = createCellWithNumber(locationRow, 4, (double) totalProductsPerLocation);

			totalProductsAllLocation += totalProductsPerLocation;
			column = 5;
			locationRowNum++;
			setRegularStyle(rowDataCells);

			progressNotifier.nofity(1, locations.size(), 30);
		}

		XSSFRow summaryRow = xsheetpkm.createRow(locationRowNum);
		XSSFCell[] summaryCells = new XSSFCell[3 + productCount()];
		summaryCells[0] = createCellWithString(summaryRow, 3, "Total");
		summaryCells[1] = createCellWithNumber(summaryRow, 4, (double) totalProductsAllLocation);

		for (int i = 0; i < productsTotal.size(); i++) {
			double value = (double) productsTotal.get(i).getCount();
			summaryCells[i + 2] = createCellWithNumber(summaryRow, column, value);
			column++;
		}
		setRegularStyle(summaryCells);
	}

	private int getCountProduct(Product product, List<Transaction> transactions) {
		int count = 0;
		for (Transaction transaction : transactions) {
			int countCurrentTrx = getProductCount(product, transaction.getProductFlows());
			count += countCurrentTrx;
		}
		return count;
	}

	private int getProductCount(Product product, List<ProductFlow> productFlows) {
		int count = 0;
		for (ProductFlow productFlow : productFlows) {
			if (product.idEquals(productFlow.getProduct())) {
				count += (productFlow.getCount());
			}
		}
		return count;
	}

	private void writeProductNames(XSSFRow titleRow) {

		for (int i = 0; i < productCount(); i++) {
			Product o = allProducts.get(i);
			XSSFCell xcellNamaobat = createCellWithString(titleRow, i + 5, o.getName());
			xcellNamaobat.setCellStyle(productNameStyle);
		}
	}

	private int productCount() { 
		return allProducts.size();
	}

	private XSSFCell createCellWithString(XSSFRow row, int column, String valueOf) {
		XSSFCell cell = row.createCell(column);
		cell.setCellValue(valueOf);
		return cell;
	}

	private XSSFCell createCellWithNumber(XSSFRow row, int column, Double valueOf) {
		XSSFCell cell = row.createCell(column);
		cell.setCellValue(valueOf);
		return cell;
	}

	private void createSummaryTableHeader(XSSFSheet xsheetpkm, XSSFRow xbarisJudulTabel) {
		XSSFCell[] xkolomAtas = new XSSFCell[3];
		xkolomAtas[0] = xbarisJudulTabel.createCell(2);
		xkolomAtas[0].setCellValue("No");
		xkolomAtas[1] = xbarisJudulTabel.createCell(3);
		xkolomAtas[1].setCellValue("Lokasi Transaksi");
		xkolomAtas[2] = xbarisJudulTabel.createCell(4);
		xkolomAtas[2].setCellValue("Jumlah Obat");
		for (int c = 0; c < xkolomAtas.length; c++) {
			xkolomAtas[c].setCellStyle(regularStyle);
			xsheetpkm.autoSizeColumn(c);
		}
	}

	private void setRegularStyle(XSSFCell[] cells) {
		for (XSSFCell cell : cells) {
			if (cell != null) {
				cell.setCellStyle(regularStyle);
			}
		}
	}

	private void createProductNameCells(XSSFRow titleRow, XSSFSheet sheet) {
		// TODO Auto-generated method stub

		XSSFCell[] cells = new XSSFCell[4];
		cells[0] = titleRow.createCell(2);
		cells[0].setCellValue("No");
		cells[1] = titleRow.createCell(3);
		cells[1].setCellValue("Nama");
		cells[2] = titleRow.createCell(4);
		cells[2].setCellValue("Lokasi Transaksi");
		cells[3] = titleRow.createCell(5);
		cells[3].setCellValue("Jumlah Obat");

		for (int c = 0; c < cells.length; c++) {
			cells[c].setCellStyle(regularStyle);
			sheet.autoSizeColumn(c);
		}

		int column = 5;
		// Membuat daftar obat di atas tabel//
		for (Product o : allProducts) {
			column++;

			XSSFCell xcellNamaobat = titleRow.createCell(column);
			xcellNamaobat.setCellValue(o.getName());
			xcellNamaobat.setCellStyle(productNameStyle);
			o.setCount(0);

		}
	}

	private XSSFCellStyle createProductNameStyle(XSSFWorkbook xwb) {
		XSSFCellStyle style = xwb.createCellStyle();
		style.setRotation((short) 90);
		style.setWrapText(true);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		return style;
	}

	private XSSFCellStyle createRegularStyle(XSSFWorkbook xwb) {
		XSSFCellStyle regularStyle = xwb.createCellStyle();
		;

		regularStyle.setBorderBottom(BorderStyle.THIN);
		regularStyle.setBorderTop(BorderStyle.THIN);
		regularStyle.setBorderRight(BorderStyle.THIN);
		regularStyle.setBorderLeft(BorderStyle.THIN);
		return regularStyle;
	}
}
