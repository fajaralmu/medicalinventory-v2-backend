package com.pkm.medicalinventory.report.builder;

import static com.pkm.medicalinventory.constants.TransactionType.TRANS_OUT;
import static com.pkm.medicalinventory.constants.TransactionType.TRANS_OUT_TO_WAREHOUSE;
import static com.pkm.medicalinventory.util.DateUtil.MONTH_NAMES;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.pkm.medicalinventory.constants.TransactionType;
import com.pkm.medicalinventory.dto.Filter;
import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.entity.HealthCenter;
import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.entity.ProductFlow;
import com.pkm.medicalinventory.entity.Transaction;
import com.pkm.medicalinventory.exception.ApplicationException;
import com.pkm.medicalinventory.report.WritableReport;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class LplpoReportBuilder extends BaseReportBuilder {
	private final HealthCenter location;
	private final Filter filter;
	private List<Product> products;

	private Map<Long, Integer> mappedProductIdAndStartingStock;
	private List<Transaction> transactionOneMonth;
	private WritableCellFormat productNameCellFormat, regularStyle;
	private int month, year;

	public LplpoReportBuilder(
		WebRequest webRequest,
		List<Product> products,
		Map<Long, Integer> mappedProductIdAndStartingStock,
		List<Transaction> transactionOneMonth,
		String fileName
	) {
		super(fileName);
		this.filter = webRequest.getFilter();
		this.location = webRequest.getHealthcenter().toEntity();
		this.products = products;
		this.mappedProductIdAndStartingStock = mappedProductIdAndStartingStock;
		this.transactionOneMonth = transactionOneMonth;
		this.month = filter.getMonth();
		this.year = filter.getYear();
		this.productNameCellFormat = getProductNameFormat();
		this.regularStyle = getRegularCellFormat();
		this.products.sort((a, b) -> a.getName().toLowerCase().compareTo(b.getName().toLowerCase()));
	}

	public WritableReport build() {
		Consumer<OutputStream> onWriteAction = (os) -> {
			try {
				WritableWorkbook xwb = Workbook.createWorkbook(os);
				WritableSheet sheet = xwb.createSheet("LPLPO " + filter.getMonth() + "-" + filter.getYear(), 0);

				buildTitleCells(sheet);
				int row = 8, no = 1;

				for (Product product : products) {

					sheet.addCell(new jxl.write.Number(2, row, no, regularStyle));
					sheet.addCell(new Label(3, row, product.getName(), productNameCellFormat));
					sheet.addCell(new Label(4, row, product.getUnit().getName(), regularStyle));

					int startingStock = mappedProductIdAndStartingStock.get(product.getId());
					int suppliedCount = 0, distributedCount = 0;
					for (Transaction t : transactionOneMonth) {
						List<ProductFlow> productFlows = t.getProductFlows();

						if (t.getType().equals(TransactionType.TRANS_IN)) {
							suppliedCount += sumCountProduct(productFlows, product);
						} else if (t.getType().equals(TRANS_OUT) || t.getType().equals(TRANS_OUT_TO_WAREHOUSE)) {
							distributedCount += sumCountProduct(productFlows, product);
						}
					}

					sheet.addCell(new jxl.write.Number(5, row, startingStock, regularStyle));
					sheet.addCell(new jxl.write.Number(6, row, suppliedCount, regularStyle));
					sheet.addCell(new jxl.write.Number(7, row, startingStock + suppliedCount, regularStyle));
					sheet.addCell(new jxl.write.Number(8, row, distributedCount, regularStyle));
					sheet.addCell(new jxl.write.Number(9, row, startingStock + suppliedCount - distributedCount,
							regularStyle));
					for (int i = 0; i < 8; i++) {
						sheet.addCell(new Label(10 + i, row, "", regularStyle));
					}
					no++;
					row++;

					if (null != progressNotifier) {
						notifyProgress(1, products.size(), 40);
					}
				}

				xwb.write();
				xwb.close();
			} catch (Exception e) {
				throw new ApplicationException(e);
			}
		};
		return new LplpoReport(fileName, onWriteAction);
	}

	private void buildTitleCells(WritableSheet sheet) throws Exception {
		// JUDUL
		int nextMonth = month + 1 > 11 ? 1 : month + 1;
		int nextYear = month + 1 > 11 ? year + 1 : year;
		sheet.addCell(new Label(3, 3, location.getName()));
		sheet.addCell(new Label(6, 2, "Pelaporan pemakaian: " + MONTH_NAMES[month - 1] + " " + filter.getYear()));
		sheet.addCell(new Label(6, 3, "Permintaan bulan: " + MONTH_NAMES[nextMonth - 1] + " " + nextYear));

		Label[] labelJudul = new Label[18];
		labelJudul[0] = new Label(2, 5, "No", regularStyle);
		labelJudul[1] = new Label(3, 5, "Nama Obat/Alkes", regularStyle);
		labelJudul[2] = new Label(4, 5, "Satuan", regularStyle);
		labelJudul[3] = new Label(5, 5, "Stok Awal", regularStyle);
		labelJudul[4] = new Label(6, 5, "Penerimaan", regularStyle);
		labelJudul[5] = new Label(7, 5, "Persediaan", regularStyle);
		labelJudul[6] = new Label(8, 5, "Pemakaian", regularStyle);
		labelJudul[7] = new Label(9, 5, "Sisa Stok", regularStyle);
		labelJudul[8] = new Label(10, 5, "Stok OPT", regularStyle);
		labelJudul[9] = new Label(11, 5, "Permintaan", regularStyle);

		sheet.mergeCells(12, 5, 15, 5);
		labelJudul[10] = new Label(12, 5, "Pemberian", regularStyle);
		// labelJudul[13] = new Label(12, 6, "Obat", formatUmum);
		for (int i = 0; i < 4; i++) {
			sheet.addCell(new Label(12 + i, 6, "Obat", regularStyle));
		}
		labelJudul[11] = new Label(12, 7, "PKD", regularStyle);
		labelJudul[12] = new Label(13, 7, "Askes", regularStyle);
		labelJudul[13] = new Label(14, 7, "Program", regularStyle);
		labelJudul[14] = new Label(15, 7, "Lain2", regularStyle);

		labelJudul[15] = new Label(17, 5, "Ket", regularStyle);
		labelJudul[16] = new Label(16, 5, "Jumlah", regularStyle);

		int titleColumn = 2;
		for (int i = 0; i < labelJudul.length; i++) {
			if (i < 10 || i >= 14) {
				sheet.mergeCells(titleColumn, 5, titleColumn, 7);
			}
			if (labelJudul[i] != null) {
				sheet.addCell(labelJudul[i]);
			}
			titleColumn++;
		}
	}

	private WritableCellFormat getRegularCellFormat() {
		WritableCellFormat regularStyle = new WritableCellFormat();
		try {
			regularStyle.setBorder(Border.ALL, BorderLineStyle.THIN);
			regularStyle.setAlignment(Alignment.CENTRE);
			regularStyle.setVerticalAlignment(VerticalAlignment.CENTRE);
		} catch (WriteException e) {
			e.printStackTrace();
		}

		return regularStyle;
	}

	private WritableCellFormat getProductNameFormat() {
		WritableCellFormat productNameCellFormat = new WritableCellFormat();

		try {
			productNameCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
			productNameCellFormat.setShrinkToFit(true);
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return productNameCellFormat;
	}

	private int sumCountProduct(List<ProductFlow> productFlows, Product product) {
		int count = 0;
		for (ProductFlow ao : productFlows) {
			if (ao.productsEquals(product)) {
				count += ao.getCount();
			}
		}
		return count;
	}
}
