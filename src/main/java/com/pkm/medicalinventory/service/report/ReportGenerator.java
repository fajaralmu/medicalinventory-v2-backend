package com.pkm.medicalinventory.service.report;

import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.pkm.medicalinventory.exception.DataNotFoundException;
import com.pkm.medicalinventory.repository.HealthCenterRepository;
import com.pkm.medicalinventory.repository.ProductFlowRepository;
import com.pkm.medicalinventory.repository.ProductRepository;
import com.pkm.medicalinventory.repository.TransactionRepository;
import com.pkm.medicalinventory.service.ProgressService;
import com.pkm.medicalinventory.service.config.DefaultHealthCenterMasterService;
import com.pkm.medicalinventory.service.inventory.InventoryService;
import com.pkm.medicalinventory.service.inventory.ProductUsageService;
import com.pkm.medicalinventory.util.DateUtil;
import com.pkm.medicalinventory.util.MapUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportGenerator {

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
	private DefaultHealthCenterMasterService defaultHealthCenterMasterService;
	@Autowired
	private ProductUsageService productUsageService;

	public XSSFWorkbook getRecipeReport(Filter filter, HttpServletRequest httpServletRequest) throws Exception {
		List<Transaction> transactions = transactionRepository.findByMonthAndYearAndType(filter.getMonth(), filter.getYear(), TransactionType.TRANS_OUT);
		log.info("Transaction count: {}", transactions.size());
		ReportBuilder<XSSFWorkbook> builder = new RecipeSuitabilityReportBuilder(filter.getMonth(), filter.getYear(), fillProductFlows(transactions));
		return builder.build();
	}

	public XSSFWorkbook getMonthyReport(Filter filter, HttpServletRequest httpServletRequest) throws Exception {

		List<HealthCenter> locations = (List<HealthCenter>) healthCenterRepository.findAll();
		progressService.sendProgress(3, httpServletRequest);

		List<Product> products = productRepository.findByOrderByUtilityTool();
		progressService.sendProgress(3, httpServletRequest);

		List<Transaction> transactionsOneMonth = transactionRepository.findByMonthAndYear(filter.getMonth(), filter.getYear());
		transactionsOneMonth = fillProductFlows(transactionsOneMonth);
		progressService.sendProgress(4, httpServletRequest);

		HealthCenter master = defaultHealthCenterMasterService.getMasterHealthCenter();

		MonthlyReportGenerator generator = new MonthlyReportGenerator(filter,
																	  transactionsOneMonth,
																	  products,
																	  locations,
																	  master,
																	  notifier(httpServletRequest));
		return generator.build();
	}

	private ProgressNotifier notifier(final HttpServletRequest httpServletRequest) {

		return new ProgressNotifier() {

			@Override
			public void notify(int progress, int maxProgress, double percent) {
				progressService.sendProgress(progress, maxProgress, percent, httpServletRequest);

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

	public void generateTransactionReceipt(
		String code, 
		HttpServletRequest httpServletRequest, 
		OutputStream os
	) throws Exception {

		Transaction t = transactionRepository.findByCode(code);

		progressService.sendProgress(20, httpServletRequest);
		if (null == t) {
			throw new DataNotFoundException("Transaction record not found");
		}
		List<ProductFlow> productFlows = productFlowRepository.findByTransaction(t);
		t.setProductFlows(productFlows);
		HealthCenter location = defaultHealthCenterMasterService.getMasterHealthCenter();

		progressService.sendProgress(20, httpServletRequest);

		ReportBuilder<?> generator = new TransactionReceiptGenerator(t, location, os);
		generator.setProgressNotifier(notifier(httpServletRequest));
		generator.build();

	}

	public Document printLable(Transaction t, OutputStream os) throws Exception {
		if (!t.getType().equals(TransactionType.TRANS_IN))
			return null;
		System.out.println(t.toString());

		Document doc = new Document(PageSize.A4);
		doc.setMargins(10f, 10f, 10f, 10f);
		doc.open();

		PdfWriter.getInstance(doc, os);
		Font fontRincianIdStok = FontFactory.getFont(FontFactory.COURIER, 20f);
		fontRincianIdStok.setStyle("bold");
		Font fontUmum = FontFactory.getFont(FontFactory.TIMES, 12f);
		Font fontNamaObat = FontFactory.getFont(FontFactory.TIMES_BOLD, 12f);
		Font fontKecil = FontFactory.getFont(FontFactory.COURIER, 9f);
		Font fontKop = FontFactory.getFont(FontFactory.TIMES_BOLD, 13f);
		Paragraph pKop1 = new Paragraph("LABEL OBAT " + t.getCode() + " tgl: " + (t.getTransactionDate()), fontKop);
		pKop1.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
		doc.add(pKop1);
		doc.add(Chunk.NEWLINE);
		PdfPTable pt = new PdfPTable(5);
		pt.setTotalWidth(new float[] { 300, 300, 300, 300, 300 });

		for (ProductFlow ao : productFlowRepository.findByTransaction(t)) {
			PdfPTable pt_item = new PdfPTable(1);
			pt_item.setTotalWidth(new float[] { 300 });

			Product o = ao.getProduct();
			String namaObat = o.getName();
			String tglEd = "exp: \n" + (ao.getExpiredDate());

			String satuan = o.getUnit().getName();

			PdfPCell nama = new PdfPCell(new Phrase(namaObat, fontNamaObat));
			PdfPCell jml = new PdfPCell(new Phrase("qty : \n" + ao.getCount() + " " + satuan, fontUmum));
			PdfPCell ED = new PdfPCell(new Phrase(tglEd, fontUmum));
			PdfPCell ID_STOK = new PdfPCell(new Phrase(ao.getId().toString(), fontRincianIdStok));
			PdfPCell hargaItem = new PdfPCell(new Phrase("Harga: Rp." + ao.getPrice(), fontUmum));
			PdfPCell kode_tr = new PdfPCell(new Phrase(t.getCode(), fontKecil));
			nama.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
			nama.setBackgroundColor(BaseColor.YELLOW);
			jml.setBorder(Rectangle.NO_BORDER);
			// ED.setBackgroundColor(BaseColor.ORANGE);
			ED.setBorder(Rectangle.NO_BORDER);
			ID_STOK.setBorder(Rectangle.NO_BORDER);
			kode_tr.setBorder(Rectangle.NO_BORDER);
			hargaItem.setBorder(Rectangle.NO_BORDER);
			pt_item.addCell(ID_STOK);
			pt_item.addCell(jml);
			pt_item.addCell(ED);
			pt_item.addCell(kode_tr);
			pt_item.addCell(nama);
			pt.addCell(pt_item);

		}
		for (int i = 0; i < 5; i++)
			pt.addCell("");

		doc.add(pt);
		// doc.add(pt);

		doc.close();
		return doc;

	}

	/**
	 * generate LPLPO
	 * @param webRequest
	 * @param outputStream
	 * @param httpServletRequest
	 * @throws Exception
	 */
	public void generateLPLPO(
		WebRequest webRequest,
		OutputStream outputStream,
		HttpServletRequest httpServletRequest
	) throws Exception {
		
		HealthCenter location = defaultHealthCenterMasterService.getMasterHealthCenter();
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
		progressService.sendProgress(10, httpServletRequest);

		Map<Long, Integer> mappedStocks = inventoryService.getProductsStockAtDate(products, location, prevDate);
		progressService.sendProgress(25, httpServletRequest);
		for (Product product : products) {
			int startingStock = mappedStocks.get(product.getId());
			mappedProductIdAndStartingStock.put(product.getId(), startingStock);
			progressService.sendProgress(1, products.size(), 25, httpServletRequest);
		}

		LPLPOGenerator generator 
			= new LPLPOGenerator(webRequest, 
								products, 
								mappedProductIdAndStartingStock,
								transactionOneMonth,
								outputStream);
		generator.setProgressNotifier(notifier(httpServletRequest));
		generator.build();
	}
	
	public XSSFWorkbook getStockOpnameReport(
		WebRequest webRequest, 
		HttpServletRequest httpServletRequest
	) throws Exception {

		HealthCenter location = webRequest.getHealthcenter().toEntity();
		Date selectedDate = DateUtil.clock24Midnight(DateUtil.getDate(webRequest.getFilter()));
		// prev year date
		int prevYear = webRequest.getFilter().getYear() - 1;
		Date lastDayOfPrevYear = DateUtil.lastDayOfYear(prevYear);

		log.info("Loading products");
		List<Product> products = (List<Product>) productRepository.findAll();
		List<ProductStock> stockModels = new LinkedList<>();
		log.info("loading product prices at selected date");
		List<Object[]> mappedPricesAndIDs = productRepository.getMappedPriceAndProductIdsAt(selectedDate);
		log.info("loading product prices at lastDayOfYear={}", lastDayOfPrevYear);
		List<Object[]> mappedPricesAndIDsAtBeginningYear = productRepository.getMappedPriceAndProductIdsAt(lastDayOfPrevYear);
		Map<Long, Double> mappedPrice = parseProductPriceMap(mappedPricesAndIDs);
		Map<Long, Double> mappedPriceAtBeginningYear = parseProductPriceMap(mappedPricesAndIDsAtBeginningYear);

		log.info("products: {}", products.size());
		//log.info("mappedPricesAndIDs: {} ", mappedPrice);
		progressService.sendProgress(10, httpServletRequest);

		log.info("loading product stock at selected date");
		int taskProp = 16;
		Map<Long, Integer> productStocks = inventoryService.getProductsStockAtDate(products, location, selectedDate);
		progressService.sendProgress(taskProp, httpServletRequest);
		
		log.info("loading product stock at lastDayOfPrevYear={}", lastDayOfPrevYear);
		Map<Long, Integer> remainingStocksAtYear = inventoryService.getProductsStockAtDate(products, location, lastDayOfPrevYear);
		progressService.sendProgress(taskProp, httpServletRequest);
		
		log.info("loading incoming product from {} to {} ", lastDayOfPrevYear, selectedDate);
		Map<Long, List<ProductFlow>> incomingStocksBetweenDate = productUsageService.getIncomingProductsBetweenDatev2(products, location, lastDayOfPrevYear, selectedDate);
		progressService.sendProgress(taskProp, httpServletRequest);
		
		log.info("loading used product from {} to {} ", lastDayOfPrevYear, selectedDate);
		Map<Long, List<ProductFlow>> usedCountBetweenDate = productUsageService.getUsedProductsBetweenDatev2(products, location, lastDayOfPrevYear, selectedDate);
		progressService.sendProgress(taskProp, httpServletRequest);

		for (Product product : products) {
			Long productId = product.getId();
			Double price = mappedPrice.get(productId);

			int productStockInTheBeginningOfYear = remainingStocksAtYear.get(productId);
			
			int incomingCount = ProductFlow.sumQtyCount(incomingStocksBetweenDate.get(productId));
			double incomingPrice = ProductFlow.sumQtyAndPrice(incomingStocksBetweenDate.get(productId));
			
			int usedCount = ProductFlow.sumQtyCount(usedCountBetweenDate.get(productId));
			double usedPrice = ProductFlow.sumQtyAndPrice(usedCountBetweenDate.get(productId));
			
			int productStockAtSelectedDate = productStocks.get(productId);
			
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
		progressService.sendProgress(taskProp, httpServletRequest);
		// log.info("mappedPricesAndIDs: {} ", mappedPrice);
		try {
			ReportBuilder<XSSFWorkbook> generator
				= new StockOpnameGenerator(location, 
										   stockModels,
										   mappedPriceAtBeginningYear,
										   selectedDate,
										   webRequest.getFilter().getYear());
			XSSFWorkbook wb = generator.build();
			progressService.sendProgress(10, httpServletRequest);

			return wb;
		} catch (Exception e) {
			log.error("Error generating stock opname report: {}", e);
			e.printStackTrace();
			throw e;
		}
	}

	private Map<Long, Double> parseProductPriceMap(List<Object[]> list) {

		Map<Long, Double> map = new HashMap<>();
		for (Object[] object : list) {
			if (object[0] == null)
				continue;
			Long id = Long.valueOf(object[0].toString());
			Double price = object[1] == null ? 0L : Double.valueOf(object[1].toString());
			map.put(id, price);
		}
		return map;
	}

}
