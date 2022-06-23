package com.pkm.medicalinventory.service.report;

import static com.pkm.medicalinventory.constants.TransactionType.TRANS_OUT;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.pkm.medicalinventory.constants.TransactionType;
import com.pkm.medicalinventory.dto.Filter;
import com.pkm.medicalinventory.entity.HealthCenter;
import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.entity.ProductFlow;
import com.pkm.medicalinventory.entity.Transaction;
import com.pkm.medicalinventory.util.DateUtil;
import com.pkm.medicalinventory.util.EntityUtil;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * ONLY Reports consumptions from master to customer and master to branch
 */
public class MonthlyReportGenerator extends BaseReportGenerator {

	private final Map<Integer, List<Transaction>> transactionMapped;
	private final List<Transaction> transactionsOneMonth;
	private final int month, year;
	private final List<Product> allProducts;
	private final List<HealthCenter> locations;
	private final XSSFCellStyle productNameStyle;
	private final XSSFCellStyle regularStyle;
	private final HealthCenter masterLocation;

	public MonthlyReportGenerator(
    Filter filter, 
    List<Transaction> transactionsOneMonth, 
    List<Product> products,
    List<HealthCenter> locations,
    HealthCenter masterLocation,
    ProgressNotifier progressNotifier
  ) {
		this.transactionsOneMonth = transactionsOneMonth;
		this.xwb = new XSSFWorkbook();
		this.month = filter.getMonth();
		this.year = filter.getYear();
		this.transactionMapped = mapTransactionByDay(transactionsOneMonth);
		this.allProducts = products;
		this.locations = locations;
		this.productNameStyle = createProductNameStyle();
		this.regularStyle = createRegularStyle();
		this.progressNotifier = progressNotifier;
		this.masterLocation = masterLocation;

		allProducts.forEach(p -> {
			p.setCount(0);
		});

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

	public XSSFWorkbook generateReport() {

		/**************** BEGIN DAILY CONSUMPTION ***********************/
		mainLoop: for (Integer day = 1; day <= 31; day++) {
			List<Product> productsPerDay = EntityUtil.cloneSerializable(allProducts);
			int productQtySum = 0, productKindCountSum = 0;
			// JUDUL TABEL//
			XSSFSheet sheet = xwb.createSheet(day.toString());
			createProductNameCells(sheet.createRow(3), sheet);

			Integer number = 1;
			int row = 4;
			List<Transaction> transactions = transactionMapped.get(day) == null ? new ArrayList<>()
					: transactionMapped.get(day);
			/**
			 * list of customer transactions
			 */
			loop: for (Transaction transaction : transactions) {

				if (!transactionMatch(transaction)) {
					continue loop;
				}

				XSSFRow dailyRow = sheet.createRow(row);
				XSSFCell[] dailyUsageCell = new XSSFCell[5 + productCount()];
				String destinationName = transaction.getCustomer() != null ? transaction.getCustomer().getName() : transaction.getHealthCenterDestination().getName();

				dailyUsageCell[0] = createCellWithString(dailyRow, 2, String.valueOf(number));
				dailyUsageCell[1] = createCellWithString(dailyRow, 3, destinationName);
				dailyUsageCell[2] = createCellWithString(dailyRow, 4, transaction.getHealthCenterLocation().getName());
				sheet.autoSizeColumn(3);

				int totalProductPerCustomer = transaction.getTotalProductFlowCount();
				
				productQtySum += totalProductPerCustomer;
				productKindCountSum += transaction.getProductKindCount();;

				dailyUsageCell[3] = createCellWithNumber(dailyRow, 5, (double) transaction.getProductFlows().size());
				dailyUsageCell[4] = createCellWithNumber(dailyRow, 6, (double) totalProductPerCustomer);

				// rincian obat per orang
				int dailyCol = 6;
				int cellIndex = 4;
				/**
				 * consumptions per product for each products (transaction details)
				 */
				for (Product product : productsPerDay) {
					cellIndex++;
					dailyCol++;
					int productCount = transaction.getProductCount(product);
					product.addCount(productCount);
					if (productCount > 0) {
						dailyUsageCell[cellIndex] = createCellWithNumber(dailyRow, dailyCol, (double) productCount);
					}
				}
				number++;
				row++;
				setRegularStyle(dailyUsageCell);
			}

			/**
			 * total consumption
			 */
			sheet.addMergedRegion(new CellRangeAddress(row, row, 2, 4));
			XSSFRow summaryRow = sheet.createRow(row);
			createCellWithStringRegularStyle(summaryRow, 2, "Jumlah");
			createCellWithStringRegularStyle(summaryRow, 3, "");
			createCellWithStringRegularStyle(summaryRow, 4, "");
			createCellWithStringRegularStyle(summaryRow, 5, "");

			int totalDailyCol = 6;
			for (Product o : productsPerDay) {
				totalDailyCol++;
				createCellWithNumberRegularStyle(summaryRow, totalDailyCol, (double) o.getCount());
			}

			createCellWithNumberRegularStyle(summaryRow, 5, (double) productKindCountSum);
			createCellWithNumberRegularStyle(summaryRow, 6, (double) productQtySum);

			notifyProgress(1, 31, 50);
		}
		/**************** END DAILY CONSUMPTION ***********************/

		writeConsumtionSummary();

		return xwb;
	}

	private static boolean transactionMatch(Transaction transaction) {
		if (TransactionType.TRANS_OUT_TO_WAREHOUSE.equals(transaction.getType()) &&
			transaction.getHealthCenterDestination() != null) {
			return true;
		}
		if (TransactionType.TRANS_OUT.equals(transaction.getType()) &&
			transaction.getCustomer() != null) {
			return true;
		}
		return false;
	}

	private List<Transaction> getTransactionsToDestination(HealthCenter location) {

		return transactionsOneMonth.stream()
				.filter(t -> {
					return TransactionType.TRANS_OUT_TO_WAREHOUSE.equals(t.getType()) &&
					location.idEquals(t.getHealthCenterDestination());
				})
				.collect(Collectors.toList());
	}

	private void writeConsumtionSummary() {

		/**************** BEGIN SUMMARY ***********************/
		XSSFSheet xsheetpkm = xwb.createSheet("Rincian Per Puskesmas Bulan " + month);
		XSSFRow titleRow = xsheetpkm.createRow(3);
		createSummaryTableHeader(xsheetpkm, titleRow);
		writeProductNames(titleRow);

		int column = 5, locationRowNum = 4;
		notifyProgress(10, 10, 10);

		// ************************SHEET TERAKHIR***********************//
		Map<Long, Integer> productsTotal = new HashMap<>();
		allProducts.forEach(p -> productsTotal.put(p.getId(), 0));
		List<Transaction> toCustomers = transactionsOneMonth.stream()
			.filter(t -> TransactionType.TRANS_OUT.equals(t.getType()))
			.collect(Collectors.toList());

		for (HealthCenter location : locations) {
			int totalProductsPerLocation = 0;
			boolean isMaster = location.idEquals(masterLocation);

			XSSFRow locationRow = xsheetpkm.createRow(locationRowNum);
			XSSFCell[] rowDataCells = new XSSFCell[4 + productCount()];

			String name = isMaster ? "Pasien" : location.getName();

			rowDataCells[0] = createCellWithString(locationRow, 2, String.valueOf(locationRowNum - 3));
			rowDataCells[1] = createCellWithString(locationRow, 3, name);

			List<Product> productsPerLocation = EntityUtil.cloneSerializable(allProducts);
			List<Transaction> transactions;
			if (isMaster) {
				transactions = toCustomers;
			} else {
				transactions = getTransactionsToDestination(location);
			}

			productsPerLocation.forEach(p -> p.setCount(0));
			int locationRowItem = 0;

			for (Product product : productsPerLocation) {

				int count = getCountProduct(product, transactions);
				product.addCount(count);
				if (product.getCount() > 0) {
					rowDataCells[locationRowItem + 4] = createCellWithNumber(locationRow, column, (double) product.getCount());
				}
				locationRowItem++;
				column++;
				totalProductsPerLocation += product.getCount();

				// *********************KOLOM TOTAL OBAT*************************//
				for (Long id : productsTotal.keySet()) {
					if (id.equals(product.getId())) {
            			productsTotal.put(id, productsTotal.get(id) + product.getCount());
					}
				}
			}
			rowDataCells[2] = createCellWithNumber(locationRow, 4, (double) totalProductsPerLocation);
			column = 5;
			locationRowNum++;
			setRegularStyle(rowDataCells);

			notifyProgress(1, locations.size(), 30);
		}

		/**
		 * summary
		 */
		writeAccumulationForSummarySheet(xsheetpkm, locationRowNum, productsTotal);

	}

	private void writeAccumulationForSummarySheet(
		XSSFSheet xsheetpkm,
		int locationRowNum,
		Map<Long, Integer> productsTotal
	) {
		int column = 5;
		int totalProductsAllLocation = 0;
		XSSFRow summaryRow = xsheetpkm.createRow(locationRowNum);
		XSSFCell[] summaryCells = new XSSFCell[3 + productCount()];
		summaryCells[0] = createCellWithString(summaryRow, 3, "Total");
    
    int i = 0;
    for (Long key : productsTotal.keySet()) {
			double value = (double) productsTotal.get(key);
			summaryCells[i + 2] = createCellWithNumber(summaryRow, column, value);
			column++;
      		i++;
			totalProductsAllLocation += value;
		}

		summaryCells[1] = createCellWithNumber(summaryRow, 4, (double) totalProductsAllLocation);

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
	private XSSFCell createCellWithStringRegularStyle(XSSFRow row, int column, String valueOf) {
		XSSFCell cell = row.createCell(column);
		cell.setCellValue(valueOf);
		cell.setCellStyle(regularStyle);
		return cell;
	}

	private XSSFCell createCellWithNumber(XSSFRow row, int column, Double valueOf) {
		XSSFCell cell = row.createCell(column);
		cell.setCellValue(valueOf);
		return cell;
	}
	private XSSFCell createCellWithNumberRegularStyle(XSSFRow row, int column, Double valueOf) {
		XSSFCell cell = row.createCell(column);
		cell.setCellValue(valueOf);
		cell.setCellStyle(regularStyle);
		return cell;
	}

	private void createSummaryTableHeader(XSSFSheet xsheetpkm, XSSFRow xbarisJudulTabel) {
		XSSFCell[] xkolomAtas = new XSSFCell[3];
		xkolomAtas[0] = xbarisJudulTabel.createCell(2);
		xkolomAtas[0].setCellValue("No");
		xkolomAtas[1] = xbarisJudulTabel.createCell(3);
		xkolomAtas[1].setCellValue("Tujuan");
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

		XSSFCell[] cells = new XSSFCell[5];
		cells[0] = titleRow.createCell(2);
		cells[0].setCellValue("No");
		cells[1] = titleRow.createCell(3);
		cells[1].setCellValue("Nama");
		cells[2] = titleRow.createCell(4);
		cells[2].setCellValue("Lokasi Transaksi");
		cells[3] = titleRow.createCell(5);
		cells[3].setCellValue("Jml Obat (Jenis)");
		cells[4] = titleRow.createCell(6);
		cells[4].setCellValue("Jml Obat (Qty)");

		for (int c = 0; c < cells.length; c++) {
			cells[c].setCellStyle(regularStyle);
			sheet.autoSizeColumn(c);
		}

		int column = 6;
		// Membuat daftar obat di atas tabel//
		for (Product o : allProducts) {
			column++;

			XSSFCell xcellNamaobat = titleRow.createCell(column);
			xcellNamaobat.setCellValue(o.getName());
			xcellNamaobat.setCellStyle(productNameStyle);
			o.setCount(0);

		}
	}

	private XSSFCellStyle createProductNameStyle() {
		XSSFCellStyle style = xwb.createCellStyle();
		style.setRotation((short) 90);
		style.setWrapText(true);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		return style;
	}

	private XSSFCellStyle createRegularStyle() {
		XSSFCellStyle regularStyle = xwb.createCellStyle();

		regularStyle.setBorderBottom(BorderStyle.THIN);
		regularStyle.setBorderTop(BorderStyle.THIN);
		regularStyle.setBorderRight(BorderStyle.THIN);
		regularStyle.setBorderLeft(BorderStyle.THIN);
		return regularStyle;
	}
}
