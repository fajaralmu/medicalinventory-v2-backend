package com.pkm.medicalinventory.report.impl;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ThreadUtils;
import org.hibernate.internal.util.ValueHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.pkm.medicalinventory.constants.TransactionType;
import com.pkm.medicalinventory.dto.Filter;
import com.pkm.medicalinventory.dto.ProductStock;
import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.entity.HealthCenter;
import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.entity.ProductFlow;
import com.pkm.medicalinventory.entity.Transaction;
import com.pkm.medicalinventory.exception.ApplicationException;
import com.pkm.medicalinventory.exception.DataNotFoundException;
import com.pkm.medicalinventory.inventory.InventoryService;
import com.pkm.medicalinventory.inventory.ProductUsageService;
import com.pkm.medicalinventory.inventory.WarehouseService;
import com.pkm.medicalinventory.report.WritableReport;
import com.pkm.medicalinventory.report.ReportGenerator;
import com.pkm.medicalinventory.report.builder.LplpoReportBuilder;
import com.pkm.medicalinventory.report.builder.MonthlyReportBuilder;
import com.pkm.medicalinventory.report.builder.RecipeSuitabilityReportBuilder;
import com.pkm.medicalinventory.report.builder.ReportBuilder;
import com.pkm.medicalinventory.report.builder.StockOpnameBuilder;
import com.pkm.medicalinventory.report.builder.TransactionReceiptBuilder;
import com.pkm.medicalinventory.repository.main.HealthCenterRepository;
import com.pkm.medicalinventory.repository.readonly.ProductFlowRepository;
import com.pkm.medicalinventory.repository.readonly.ProductRepository;
import com.pkm.medicalinventory.repository.readonly.TransactionRepository;
import com.pkm.medicalinventory.service.ProgressNotifier;
import com.pkm.medicalinventory.service.ProgressService;
import com.pkm.medicalinventory.util.DateUtil;
import com.pkm.medicalinventory.util.HttpRequestUtil;
import com.pkm.medicalinventory.util.MapUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportGeneratorImpl implements ReportGenerator{

	@Autowired
	private ProgressService progressService;
	@Autowired
	private ProductFlowRepository productFlowRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private HealthCenterRepository healthCenterRepository;
	@Autowired
	private InventoryService inventoryService;
	@Autowired
	private WarehouseService wareHouseService;
	@Autowired
	private ProductUsageService productUsageService;

	public WritableReport getRecipeReport(String fileName,Filter filter) {
		List<Transaction> transactions = transactionRepository.findByMonthAndYearAndType(filter.getMonth(), filter.getYear(), TransactionType.TRANS_OUT);
		log.info("Transaction count: {}", transactions.size());
		ReportBuilder builder = new RecipeSuitabilityReportBuilder(filter.getMonth(), filter.getYear(), fillProductFlows(transactions), fileName);
		return builder.build();
	}

	public WritableReport getMonthyReport(String fileName,Filter filter) {
		List<HealthCenter> locations = (List<HealthCenter>) healthCenterRepository.findAll();
		progressService.sendProgress(3);

		List<Product> products = productRepository.findByOrderByUtilityTool();
		progressService.sendProgress(3);

		List<Transaction> transactionsOneMonth = transactionRepository.findByMonthAndYear(filter.getMonth(), filter.getYear());
		transactionsOneMonth = fillProductFlows(transactionsOneMonth);
		progressService.sendProgress(4);

		HealthCenter master = wareHouseService.getMasterHealthCenter();

		ReportBuilder generator = new MonthlyReportBuilder(filter,
														   transactionsOneMonth,
														   products,
														   locations,
														   master,
														   notifier(),
														   fileName);
		return generator.build();
	}

	private ProgressNotifier notifier() {
		return new ProgressNotifier() {
			@Override
			public void notify(int progress, int maxProgress, double percent) {
				progressService.sendProgress(progress, maxProgress, percent);
			}
		};
	}

	private List<Transaction> fillProductFlows(List<Transaction> transactions) {
		if (transactions.size() == 0)
			return transactions;
		List<ProductFlow> productFlows = productFlowRepository.findByTransactionIn(transactions);

		Map<Long, Transaction> transactionMap = new HashMap<>();
		for (Transaction transaction : transactions) {
			transactionMap.put(transaction.getId(), transaction);
		}

		for (ProductFlow productFlow : productFlows) {
			Long trxId = productFlow.getTransaction().getId();
			if (null == transactionMap.get(trxId))
				continue;
			transactionMap.get(trxId).addProductFlow(productFlow);
		}
		return MapUtil.mapValuesToList(transactionMap);
	}

	public WritableReport generateTransactionReceipt(String fileName,String code) {
		Transaction t = transactionRepository.findByCode(code);

		progressService.sendProgress(20);
		if (null == t) {
			throw new DataNotFoundException("Transaction record not found");
		}
		List<ProductFlow> productFlows = productFlowRepository.findByTransaction(t);
		t.setProductFlows(productFlows);
		HealthCenter location = wareHouseService.getMasterHealthCenter();

		progressService.sendProgress(20);

		ReportBuilder generator = new TransactionReceiptBuilder(t, location, fileName);
		return generator.build();

	}

//	public Document printLable(Transaction t, OutputStream os) throws Exception {
//		if (!t.getType().equals(TransactionType.TRANS_IN))
//			return null;
//		System.out.println(t.toString());
//
//		Document doc = new Document(PageSize.A4);
//		doc.setMargins(10f, 10f, 10f, 10f);
//		doc.open();
//
//		PdfWriter.getInstance(doc, os);
//		Font fontRincianIdStok = FontFactory.getFont(FontFactory.COURIER, 20f);
//		fontRincianIdStok.setStyle("bold");
//		Font fontUmum = FontFactory.getFont(FontFactory.TIMES, 12f);
//		Font fontNamaObat = FontFactory.getFont(FontFactory.TIMES_BOLD, 12f);
//		Font fontKecil = FontFactory.getFont(FontFactory.COURIER, 9f);
//		Font fontKop = FontFactory.getFont(FontFactory.TIMES_BOLD, 13f);
//		Paragraph pKop1 = new Paragraph("LABEL OBAT " + t.getCode() + " tgl: " + (t.getTransactionDate()), fontKop);
//		pKop1.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
//		doc.add(pKop1);
//		doc.add(Chunk.NEWLINE);
//		PdfPTable pt = new PdfPTable(5);
//		pt.setTotalWidth(new float[] { 300, 300, 300, 300, 300 });
//
//		for (ProductFlow ao : productFlowRepository.findByTransaction(t)) {
//			PdfPTable pt_item = new PdfPTable(1);
//			pt_item.setTotalWidth(new float[] { 300 });
//
//			Product o = ao.getProduct();
//			String namaObat = o.getName();
//			String tglEd = "exp: \n" + (ao.getExpiredDate());
//
//			String satuan = o.getUnit().getName();
//
//			PdfPCell nama = new PdfPCell(new Phrase(namaObat, fontNamaObat));
//			PdfPCell jml = new PdfPCell(new Phrase("qty : \n" + ao.getCount() + " " + satuan, fontUmum));
//			PdfPCell ED = new PdfPCell(new Phrase(tglEd, fontUmum));
//			PdfPCell ID_STOK = new PdfPCell(new Phrase(ao.getId().toString(), fontRincianIdStok));
//			PdfPCell hargaItem = new PdfPCell(new Phrase("Harga: Rp." + ao.getPrice(), fontUmum));
//			PdfPCell kode_tr = new PdfPCell(new Phrase(t.getCode(), fontKecil));
//			nama.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
//			nama.setBackgroundColor(BaseColor.YELLOW);
//			jml.setBorder(Rectangle.NO_BORDER);
//			// ED.setBackgroundColor(BaseColor.ORANGE);
//			ED.setBorder(Rectangle.NO_BORDER);
//			ID_STOK.setBorder(Rectangle.NO_BORDER);
//			kode_tr.setBorder(Rectangle.NO_BORDER);
//			hargaItem.setBorder(Rectangle.NO_BORDER);
//			pt_item.addCell(ID_STOK);
//			pt_item.addCell(jml);
//			pt_item.addCell(ED);
//			pt_item.addCell(kode_tr);
//			pt_item.addCell(nama);
//			pt.addCell(pt_item);
//
//		}
//		for (int i = 0; i < 5; i++)
//			pt.addCell("");
//
//		doc.add(pt);
//		// doc.add(pt);
//
//		doc.close();
//		return doc;
//
//	}
	
	public WritableReport generateLPLPO(String fileName, WebRequest webRequest) {
		HealthCenter location = wareHouseService.getMasterHealthCenter();
		Filter filter = webRequest.getFilter();

		int month = filter.getMonth(), year = filter.getYear();
		Date date = DateUtil.getDate(year, month - 1, 1);
		Date prevDate = DateUtil.getPrevDateLastDay(date);
		prevDate = DateUtil.clock24Midnight(prevDate);

		List<Product> products = productRepository.findByOrderByUtilityTool();

		List<Transaction> transactionOneMonth = transactionRepository
			.findByMonthAndYearAndHealthCenterLocation(filter.getMonth(), filter.getYear(), location);

		fillProductFlows(transactionOneMonth);
		Map<Long, Integer> mappedProductIdAndStartingStock = new HashMap<>();
		progressService.sendProgress(10);

		Map<Long, Integer> mappedStocks = inventoryService.getProductsStockAtDate(products, location, prevDate);
		progressService.sendProgress(25);
		for (Product product : products) {
			int startingStock = mappedStocks.get(product.getId());
			mappedProductIdAndStartingStock.put(product.getId(), startingStock);
			progressService.sendProgress(1, products.size(), 25);
		}

		ReportBuilder generator 
			= new LplpoReportBuilder( webRequest, 
								products, 
								mappedProductIdAndStartingStock,
								transactionOneMonth,
								fileName);
		
		return generator.build();
	}
	
	public WritableReport getStockOpnameReport(String fileName,WebRequest webRequest) {
		List<ProductStock> stockModels = new LinkedList<>();

		HealthCenter location = webRequest.getHealthcenter().toEntity();
		Date selectedDate = DateUtil.clock24Midnight(DateUtil.getDate(webRequest.getFilter()));
		// prev year date
		int prevYear = webRequest.getFilter().getYear() - 1;
		Date lastDayOfPrevYear = DateUtil.lastDayOfYear(prevYear);

		log.info("Loading products");
		List<Product> products = (List<Product>) productRepository.findAll();
		log.info("products: {}", products.size());

		final Map<Long, Double> mappedPrice = new HashMap<>();
		final Map<Long, Double> mappedPriceAtBeginningYear = new HashMap<>();
		final StockOpnameInternal model = new StockOpnameInternal();
		final String reqId = HttpRequestUtil.getPageRequestId();
		
		Thread mappedPriceTask = new Thread(() -> {
			Date begin = new Date();
			log.info("loading product prices at selected date");

			List<Object[]> mapPriceAndId = productRepository.getMappedPriceAndProductIdsAt(selectedDate);
			parseProductPriceMap(mappedPrice, mapPriceAndId);

			long dur = new Date().getTime() - begin.getTime();
			log.info("loading product prices finished. Duration: {} ms", dur);
			
			progressService.sendProgress(5d, reqId);
		});
		mappedPriceTask.start();
		
		Thread mappedPriceEarlyYearTask = new Thread(() -> {
			Date begin = new Date();
			log.info("loading product prices at lastDayOfYear={}", lastDayOfPrevYear);

			List<Object[]> mappedPricesAndIDsAtBeginningYear = productRepository.getMappedPriceAndProductIdsAt(lastDayOfPrevYear);
			parseProductPriceMap(mappedPriceAtBeginningYear, mappedPricesAndIDsAtBeginningYear);

			long dur = new Date().getTime() - begin.getTime();
			log.info("loading product prices at lastDayOfYear finished. Duration: {} ms", dur);

			progressService.sendProgress(5d, reqId);
		});
		mappedPriceEarlyYearTask.start();
		
		final double taskProp = 16;

		//log.info("mappedPricesAndIDs: {} ", mappedPrice);
		Thread stockTask = new Thread(() -> {
			log.info("loading product stock at selected date");
			model.productStocks = inventoryService.getProductsStockAtDate(products, location, selectedDate);
			progressService.sendProgress(taskProp, reqId);
			
			log.info("loading product stock at lastDayOfPrevYear={}", lastDayOfPrevYear);
			model.remainingStocksAtYear = inventoryService.getProductsStockAtDate(products, location, lastDayOfPrevYear);
			progressService.sendProgress(taskProp, reqId);
			
			log.info("loading incoming product from {} to {} ", lastDayOfPrevYear, selectedDate);
			model.incomingStocksBetweenDate = productUsageService.getIncomingProductsBetweenDatev2(products, location, lastDayOfPrevYear, selectedDate);
			progressService.sendProgress(taskProp, reqId);
			
			log.info("loading used product from {} to {} ", lastDayOfPrevYear, selectedDate);
			model.usedCountBetweenDate = productUsageService.getUsedProductsBetweenDatev2(products, location, lastDayOfPrevYear, selectedDate);
			progressService.sendProgress(taskProp, reqId);
		});
		stockTask.start();

		try {
			mappedPriceTask.join();
			mappedPriceEarlyYearTask.join();
			stockTask.join();
		} catch (Exception ex) {
			throw new ApplicationException(ex);
		}

		for (Product product : products) {
			Long productId = product.getId();
			Double price = mappedPrice.get(productId);

			int productStockInTheBeginningOfYear = model.remainingStocksAtYear.get(productId);
			
			int incomingCount = ProductFlow.sumQtyCount( model.incomingStocksBetweenDate.get(productId));
			double incomingPrice = ProductFlow.sumQtyAndPrice( model.incomingStocksBetweenDate.get(productId));
			
			int usedCount = ProductFlow.sumQtyCount( model.usedCountBetweenDate.get(productId));
			double usedPrice = ProductFlow.sumQtyAndPrice( model.usedCountBetweenDate.get(productId));
			
			int productStockAtSelectedDate =  model.productStocks.get(productId);
			
			log.info("Stock info for: {}. early year stock: {}. incomingCount: {}. used stock: {}, remaining: {} | {}",
				product.getName(),
				productStockInTheBeginningOfYear,
				incomingCount,
				usedCount,
				productStockInTheBeginningOfYear + incomingCount - usedCount,
				productStockAtSelectedDate
			);

			product.setPrice(price);

			ProductStock stockModel = new ProductStock(
				product.toModel(),
				productStockInTheBeginningOfYear,
				incomingCount,
				usedCount,
				productStockAtSelectedDate
			);
			stockModel.setIncomingPrice(incomingPrice);
			stockModel.setUsedPrice(usedPrice);

			// log.info("")
			
			stockModels.add(stockModel);

		}
		progressService.sendProgress(taskProp);
		// log.info("mappedPricesAndIDs: {} ", mappedPrice);
		try {
			ReportBuilder generator
				= new StockOpnameBuilder(location, 
										   stockModels,
										   mappedPriceAtBeginningYear,
										   selectedDate,
										   webRequest.getFilter().getYear(),
										   fileName);
			WritableReport wb = generator.build();
			progressService.sendProgress(10);

			return wb;
		} catch (Exception e) {
			log.error("Error generating stock opname report: {}", e);
			e.printStackTrace();
			throw e;
		}
	}

	private Map<Long, Double> parseProductPriceMap(final Map<Long, Double> map, List<Object[]> list) {
		for (Object[] object : list) {
			if (object[0] == null)
				continue;
			Long id = Long.valueOf(object[0].toString());
			Double price = object[1] == null ? 0L : Double.valueOf(object[1].toString());
			map.put(id, price);
		}
		return map;
	}

	static class StockOpnameInternal {
		public Map<Long, Integer> productStocks = new HashMap<>();
		public Map<Long, Integer> remainingStocksAtYear = new HashMap<>();
		public Map<Long, List<ProductFlow>> incomingStocksBetweenDate = new HashMap<>();
		public Map<Long, List<ProductFlow>> usedCountBetweenDate = new HashMap<>();
	}
}
