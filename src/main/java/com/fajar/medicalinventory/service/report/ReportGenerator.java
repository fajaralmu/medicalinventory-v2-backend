package com.fajar.medicalinventory.service.report;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
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

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.dto.Filter;
import com.fajar.medicalinventory.dto.ProductStock;
import com.fajar.medicalinventory.dto.WebRequest;
import com.fajar.medicalinventory.entity.HealthCenter;
import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.entity.Transaction;
import com.fajar.medicalinventory.exception.DataNotFoundException;
import com.fajar.medicalinventory.repository.HealthCenterRepository;
import com.fajar.medicalinventory.repository.ProductFlowRepository;
import com.fajar.medicalinventory.repository.ProductRepository;
import com.fajar.medicalinventory.repository.TransactionRepository;
import com.fajar.medicalinventory.service.ProgressService;
import com.fajar.medicalinventory.service.config.DefaultHealthCenterMasterService;
import com.fajar.medicalinventory.service.inventory.InventoryService;
import com.fajar.medicalinventory.util.DateUtil;
import com.fajar.medicalinventory.util.MapUtil;
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

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportGenerator {

	@Autowired
	private ProgressService progressService;
	@Autowired
	private ProductFlowRepository aliranObatRepository;
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
	 
	public XSSFWorkbook getMonthyReport(Filter filter, HttpServletRequest httpServletRequest) throws Exception {

		List<HealthCenter> locations = (List<HealthCenter>) healthCenterRepository.findAll();
		progressService.sendProgress(3, httpServletRequest);

		List<Product> products = productRepository.findByOrderByUtilityTool();
		progressService.sendProgress(3, httpServletRequest);

		List<Transaction> transactionsOneMonth = fillProductFlows(
				transactionRepository.findByMonthAndYear(filter.getMonth(), filter.getYear()));
		progressService.sendProgress(4, httpServletRequest);

		MonthlyReportGenerator generator = new MonthlyReportGenerator(filter, transactionsOneMonth, products, locations,
				notifier(httpServletRequest));
		return generator.generateReport();
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
		List<ProductFlow> productFlows = aliranObatRepository.findByTransactionIn(transactions);
		 
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

	public void transactionReceipt(String code, HttpServletRequest httpServletRequest, OutputStream os)
			throws  Exception {
		 
		Transaction t = transactionRepository.findByCode(code);
		
		progressService.sendProgress(20, httpServletRequest);
		if (null == t) {
			throw new DataNotFoundException("Transaction record not found");
		}
		List<ProductFlow> productFlows = aliranObatRepository.findByTransaction(t);
		t.setProductFlows(productFlows);
		HealthCenter location = defaultHealthCenterMasterService.getMasterHealthCenter();
		
		progressService.sendProgress(20, httpServletRequest);
		
		TransactionReceiptGenerator generator = new TransactionReceiptGenerator(t, location, notifier(httpServletRequest));
		generator.generateReport(os);

	}

	public String printLable(Transaction t, String path) throws Exception {
		if (!t.getType().equals(TransactionType.TRANS_IN))
			return null;
		System.out.println(t.toString());

		Document doc = new Document(PageSize.A4);
		doc.setMargins(10f, 10f, 10f, 10f);
		doc.open();
		
		String filename = path + "Label-Obat-" + t.getCode() + ".pdf";
		PdfWriter.getInstance(doc, new FileOutputStream(filename));
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

		for (ProductFlow ao : aliranObatRepository.findByTransaction(t)) {
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

		System.out.println("Done");

		File f = null;
		if (Desktop.isDesktopSupported()) {
			f = new File(filename);
			System.out.println(f.exists());
			if (f.canRead()) {
				System.out.println("DONE");
				System.out.println(f.getName());
				System.out.println(f.getAbsolutePath());
				/*
				 * try { Desktop.getDesktop().open(f); } catch (IOException e) { // TODO
				 * Auto-generated catch block e.printStackTrace(); }
				 */
				return f.getName();
			}

		}

		return null;

	}

	
	public void generateProductRequestSheet(WebRequest webRequest, OutputStream os, HttpServletRequest httpServletRequest)
			throws Exception {
		HealthCenter location = webRequest.getHealthcenter().toEntity();
		Filter filter = webRequest.getFilter();
		Boolean isMasterHealthCenter = defaultHealthCenterMasterService.isMasterHealthCenter(location);

		int month = filter.getMonth(), year = filter.getYear();
		Date date = DateUtil.getDate(year, month - 1, 1);
		Date prevDate = DateUtil.getPrevDateLastDay(date);

		List<Product> products = productRepository.findByOrderByUtilityTool();

		List<Transaction> transactionOneMonth = transactionRepository.findByMonthAndYear(filter.getMonth(),
				filter.getYear());

		fillProductFlows(transactionOneMonth);
		Map<Long, Integer> mappedProductIdAndStartingStock = new HashMap<>();
		progressService.sendProgress(10, httpServletRequest);
		 
		for (Product product : products) {
			int startingStock = inventoryService.getProductStockAtDate(product, location, prevDate);
			mappedProductIdAndStartingStock.put(product.getId(), startingStock);
			progressService.sendProgress(1, products.size(), 50, httpServletRequest);
		}
		
		
		 ProductRequestSheetGenerator generator = new ProductRequestSheetGenerator(webRequest, products, mappedProductIdAndStartingStock, transactionOneMonth);
		 generator.setMasterHealthCenter(isMasterHealthCenter);
		 generator.setProgressNotifier(notifier(httpServletRequest));
		 generator.generateReport(os);

	}

	public boolean transactionExist(String kodetransaksi, List<Transaction> list) {
		for (Transaction t : list) {
			if (t.getCode().equals(kodetransaksi)) {
				return true;
			}
		}
		return false;
	}

	public XSSFWorkbook getStockOpnameReport(WebRequest webRequest, HttpServletRequest httpServletRequest)
			throws Exception {
		HealthCenter location = webRequest.getHealthcenter().toEntity();
		Date d = DateUtil.getDate(webRequest.getFilter());
		List<Product> products = (List<Product>) productRepository.findAll();
		List<ProductStock> stockModels = new LinkedList<>();
		List<Object[]> mappedPricesAndIDs = productRepository.getMappedPriceAndProductIdsAt(d);
		Map<Long, Double> mappedPrice = parseProductPriceMap(mappedPricesAndIDs);

		log.info("products: {}", products.size());
		log.info("mappedPricesAndIDs: {} ", mappedPrice);
		progressService.sendProgress(10, httpServletRequest);
		
		//prev year date
		int prevYear = webRequest.getFilter().getYear() - 1;
		Date lastDayOfYear = DateUtil.lastDayOfYear(prevYear);
		
		for (Product product : products) {
			log.info("get stock info for: {}", product.getName());
			Double price = mappedPrice.get(product.getId());
			
			int productStockInTheBeginningOfYear = inventoryService.getProductStockAtDate(product, location, lastDayOfYear);
			int incomingCount = inventoryService.getIncomingProductBetweenDate(product, location, lastDayOfYear, d);
			int usedCount = inventoryService.getUsedProductBetweenDate(product, location, lastDayOfYear, d);
			int productStockAtSelectedDate = inventoryService.getProductStockAtDate(product, location, d);
			
			product.setPrice(price);
			
			ProductStock stockModel = new ProductStock(product.toModel(), incomingCount, usedCount, productStockAtSelectedDate, productStockInTheBeginningOfYear);
			stockModels.add(stockModel);
			progressService.sendProgress(1, products.size(), 80, httpServletRequest);
		}
		log.info("mappedPricesAndIDs: {} ", mappedPrice);
		try {
			StockOpnameGenerator generator = new StockOpnameGenerator(location, stockModels, d);
			XSSFWorkbook wb = generator.generateReport();
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
