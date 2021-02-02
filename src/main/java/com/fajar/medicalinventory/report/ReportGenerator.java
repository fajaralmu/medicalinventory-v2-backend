package com.fajar.medicalinventory.report;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.dto.Filter;
import com.fajar.medicalinventory.entity.Configuration;
import com.fajar.medicalinventory.entity.HealthCenter;
import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.entity.Transaction;
import com.fajar.medicalinventory.repository.HealthCenterRepository;
import com.fajar.medicalinventory.repository.ProductFlowRepository;
import com.fajar.medicalinventory.repository.ProductRepository;
import com.fajar.medicalinventory.repository.TransactionRepository;
import com.fajar.medicalinventory.service.ProgressService;
import com.fajar.medicalinventory.service.config.BindedValues;
import com.fajar.medicalinventory.service.config.DefaultHealthCenterMasterService;
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

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
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
	private BindedValues bindedValues;
	@Autowired
	private DefaultHealthCenterMasterService defaultHealthCenterMasterService;

	public String dateToString(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		Integer bulan = cal.get(Calendar.MONTH) + 1;
		return cal.get(Calendar.DATE) + "/" + bulan.toString() + "/" + cal.get(Calendar.YEAR);

	}

	public XSSFWorkbook getMonthyReport(Filter filter, HttpServletRequest httpServletRequest) throws Exception {
 
		List<HealthCenter> locations = (List<HealthCenter>) healthCenterRepository.findAll();
		progressService.sendProgress(3, httpServletRequest);
		
		List<Product> products = productRepository.findByOrderByUtilityTool(); 
		progressService.sendProgress(3, httpServletRequest);
		
		List<Transaction> transactionsOneMonth = fillProductFlows(transactionRepository.findByMonthAndYear(filter.getMonth(), filter.getYear())); 
		progressService.sendProgress(4, httpServletRequest);
		 
		MonthlyReportGenerator generator = new MonthlyReportGenerator(filter, transactionsOneMonth, products, locations, notifier(httpServletRequest));
		return generator.generateReport(progressService, httpServletRequest);
	}

	
	private ProgressNotifier notifier(final HttpServletRequest httpServletRequest) {
		 
		return new ProgressNotifier() {
			
			@Override
			public void nofity(int progress, int maxProgress, double percent) {
				progressService.sendProgress(progress, maxProgress, percent, httpServletRequest);
				
			}
		};
	}

	private List<Transaction> fillProductFlows(List<Transaction> transactions) {

		List<ProductFlow> productFlows = aliranObatRepository.findByTransactionIn(transactions);
		List<Transaction> result = new ArrayList<>();
		Map<Long, Transaction> transactionMap = new HashMap<>();
		for (Transaction transaction : transactions) {
			transactionMap.put(transaction.getId(), transaction);
			
		}
		
		for (ProductFlow productFlow : productFlows) {
			Long trxId = productFlow.getTransaction().getId();
			if (null == transactionMap.get(trxId)) continue;
			transactionMap.get(trxId).addProductFlow(productFlow);
			 
		}
		for(Long id : transactionMap.keySet()) {
			result.add(transactionMap.get(id));
		}
		return result;
	}

	



//	public String transactionNote(Transaction t, boolean includeED, String path, AliranObatRepository aorepo,
//			DrugStockService stokObatData)
//			throws FileNotFoundException, DocumentException, SQLException, com.itextpdf.text.DocumentException {
//		System.out.println(t.toString());
//		Document doc = new Document(PageSize.A5);
//		PdfPTable pt = new PdfPTable(7);
//		// pt.setTotalWidth(800);
//		pt.setTotalWidth(new float[] { 40, 200, 100, 100, 100, 100, 100 });
//
//		String filename = path + "Bukti-Transaksi-" + t. getCode() + ".pdf";
//		PdfWriter.getInstance(doc, new FileOutputStream(filename));
//
//		doc.open();
//
//		Font fontRincianTransaksi = FontFactory.getFont(FontFactory.COURIER, 6f);
//		Font fontJudulTabel = FontFactory.getFont(FontFactory.TIMES, 7f);
//
//		Font fontAlamat = FontFactory.getFont(FontFactory.TIMES_ITALIC, 8f);
//		Font fontEmail = FontFactory.getFont(FontFactory.TIMES, 8f);
//		fontEmail.setStyle("underline");
//
//		Font fontKop = FontFactory.getFont(FontFactory.TIMES_BOLD, 10f);
//
//		Paragraph pKop1 = new Paragraph("DINAS KESEHATAN", fontKop);
//		Paragraph pKop2 = new Paragraph("UPTD UNIT PUSKESMAS SRUWENG", fontKop);
//		Paragraph pAlamat = new Paragraph("Alamat: Desa Village Kec.Mulicipality.Kab.Kebumen.Telp.(0123)334567Kebumen",
//				fontAlamat);
//		Paragraph pEmail = new Paragraph(
//				"                                      E-mail : sehat.puskesmas@yahoo.com                                      ",
//				fontEmail);
//		pAlamat.setAlignment(Element.ALIGN_CENTER);
//		pEmail.setAlignment(Element.ALIGN_CENTER);
//		pKop1.setAlignment(Element.ALIGN_CENTER);
//		pKop2.setAlignment(Element.ALIGN_CENTER);
//		// pJudul.setAlignment(Element.ALIGN_CENTER);
//		doc.add(pKop1);
//		doc.add(pKop2);
//		doc.add(pAlamat);
//		doc.add(pEmail);
//		// doc.add(pGaris);
//		// doc.add(pJudul);
//		doc.add(Chunk.NEWLINE);
//
//		Integer total = 0, totalHarga = 0;
//		Paragraph identitas_transaksi[] = new Paragraph[4];
//		identitas_transaksi[0] = new Paragraph("Kode transaksi: " + t. getCode(), fontJudulTabel);
//		identitas_transaksi[1] = new Paragraph("Tanggal: " + dateToString(t.getTransactionDate()), fontJudulTabel);
//
//		Customer p = null;
//
//		if (t.getType().equals(TransactionType.TRANS_OUT)) {
//			identitas_transaksi[2] = new Paragraph("Kode penerima: "
//					+ (t. getCustomer() != null ? t. getCustomer(). getCode() : t. getHealthCenterDestination(). getCode()),
//					fontJudulTabel);
//			identitas_transaksi[3] = new Paragraph("Nama penerima: "
//					+ (t. getCustomer() != null ? t. getCustomer(). getName() : t. getHealthCenterDestination(). getName()),
//					fontJudulTabel);
//		} else {
//			identitas_transaksi[2] = new Paragraph("Kode pemasok: " + t. getSupplier().getId(), fontJudulTabel);
//			identitas_transaksi[3] = new Paragraph("Nama pemasok: " + t. getSupplier().getName(), fontJudulTabel);
//		}
//
//		for (int i = 0; i < identitas_transaksi.length; i++) {
//			identitas_transaksi[i].setIndentationLeft(30);
//			doc.add(identitas_transaksi[i]);
//		}
//
//		if (t. getCustomer() != null) {
//			p = t. getCustomer();
//
//			Paragraph tgllahir_par = new Paragraph("Tgl Lahir: " + changeDateStringFormat(p.getTgllahir()),
//					fontJudulTabel);
//			Paragraph alamat_par = new Paragraph("Alamat: " + p.getAlamat(), fontJudulTabel);
//			tgllahir_par.setIndentationLeft(30);
//			alamat_par.setIndentationLeft(30);
//			doc.add(tgllahir_par);
//			doc.add(alamat_par);
//		}
//
//		Integer i = 1;
//		PdfPCell noHead = new PdfPCell(new Phrase("No", fontJudulTabel));
//		PdfPCell namaHead = new PdfPCell(new Phrase("Nama Obat", fontJudulTabel));
//		PdfPCell tglED = new PdfPCell(new Phrase("Kadaluarsa", fontJudulTabel));
//		PdfPCell idStok = new PdfPCell(new Phrase("Id Stok", fontJudulTabel));
//		PdfPCell jmlHead = new PdfPCell(new Phrase("Qty", fontJudulTabel));
//		PdfPCell hargaSatuanHead = new PdfPCell(new Phrase("Harga Satuan", fontJudulTabel));
//		PdfPCell totalHargaItemHead = new PdfPCell(new Phrase("Harga Total", fontJudulTabel));
//		noHead.setBorder(Rectangle.BOTTOM);
//		namaHead.setBorder(Rectangle.BOTTOM);
//		tglED.setBorder(Rectangle.BOTTOM);
//		idStok.setBorder(Rectangle.BOTTOM);
//		jmlHead.setBorder(Rectangle.BOTTOM);
//		hargaSatuanHead.setBorder(Rectangle.BOTTOM);
//		totalHargaItemHead.setBorder(Rectangle.BOTTOM);
//		pt.addCell(noHead);
//		pt.addCell(namaHead);
//		pt.addCell(tglED);
//		pt.addCell(idStok);
//		pt.addCell(jmlHead);
//		pt.addCell(hargaSatuanHead);
//		pt.addCell(totalHargaItemHead);
//
//		for (ProductFlow ao : t. getProductFlows()) {
//			String Namaobat = ao. getProduct().getName();
//			if (ao.getGenerik() == 1) {
//				Namaobat += " (generik)";
//			}
//
//			PdfPCell nama = new PdfPCell(new Phrase(Namaobat, fontRincianTransaksi));
//			PdfPCell jml = new PdfPCell(new Phrase(ao.getCount().toString(), fontRincianTransaksi));
//			String tgl_ed = "";
//			Integer id_stok = 0;
//
//			if (!includeED) {
//				tgl_ed = "-";
//			} else {
//				if (t.getJenistransaksi().getKodejenistransaksi() == 2) {
//					ProductFlow ao_asal = aorepo
//							.findOne(stokObatData.dapatkanStokObat(ao.getKodestokobat()).getAliranobat().getId());
//					Date kadaluarsa = ao_asal.getKadaluarsa();
//					if (kadaluarsa == null) {
//						ProductFlow ao_asal_2 = aorepo.findOne(ao_asal.getKodestokobat());
//						ao.setKadaluarsa(ao_asal_2.getKadaluarsa());
//					} else {
//						ao.setKadaluarsa(kadaluarsa);
//					}
//					tgl_ed = dateToString(ao.getKadaluarsa());
//					id_stok = ao_asal.getId();
//				} else {
//					ProductStock stok_baru = stokObatData.dapatkanStokObatDenganKodeAliran(ao.getId());
//					id_stok = stok_baru.getId();
//					tgl_ed = ao.getKadaluarsa() == null ? "-" : dateToString(ao.getKadaluarsa());
//				}
//			}
//			PdfPCell ED = new PdfPCell(new Phrase(tgl_ed, fontRincianTransaksi));
//			System.out.println("Bukti tr id stok: " + id_stok);
//			PdfPCell ID_STOK = new PdfPCell(new Phrase(id_stok.toString(), fontRincianTransaksi));
//			PdfPCell no = new PdfPCell(new Phrase(i.toString(), fontRincianTransaksi));
//			PdfPCell hargaItem = new PdfPCell(new Phrase(ao.getHarga().toString(), fontRincianTransaksi));
//			Integer totalHargaItem_int = ao.getHarga() * ao.getCount();
//			PdfPCell totalHargaItem = new PdfPCell(new Phrase(totalHargaItem_int.toString(), fontRincianTransaksi));
//			no.setBorder(Rectangle.NO_BORDER);
//			nama.setBorder(Rectangle.NO_BORDER);
//			jml.setBorder(Rectangle.NO_BORDER);
//			ED.setBorder(Rectangle.NO_BORDER);
//			ID_STOK.setBorder(Rectangle.NO_BORDER);
//			hargaItem.setBorder(Rectangle.NO_BORDER);
//			totalHargaItem.setBorder(Rectangle.NO_BORDER);
//			pt.addCell(no);
//			pt.addCell(nama);
//			pt.addCell(ED);
//			pt.addCell(ID_STOK);
//			pt.addCell(jml);
//			pt.addCell(hargaItem);
//			pt.addCell(totalHargaItem);
//			totalHarga = totalHarga + totalHargaItem_int;
//			total = total + ao.getCount();
//			// System.out.println("total " + total);
//			i++;
//		}
//		PdfPCell kosong = new PdfPCell(new Phrase("", fontJudulTabel));
//		PdfPCell labelTotal = new PdfPCell(new Phrase("total", fontJudulTabel));
//		PdfPCell jmlTotal = new PdfPCell(new Phrase(total.toString(), fontJudulTabel));
//		PdfPCell jmlHargaTotal = new PdfPCell(new Phrase("Rp " + numbToCurrencyString(totalHarga), fontJudulTabel));
//		kosong.setBorder(Rectangle.TOP);
//		labelTotal.setBorder(Rectangle.TOP);
//		jmlTotal.setBorder(Rectangle.TOP);
//		jmlHargaTotal.setBorder(Rectangle.TOP);
//		pt.addCell(kosong);
//		pt.addCell(labelTotal);
//		pt.addCell(kosong);
//		pt.addCell(kosong);
//		pt.addCell(jmlTotal);
//		pt.addCell(kosong);
//		pt.addCell(jmlHargaTotal);
//		doc.add(Chunk.NEWLINE);
//		doc.add(pt);
//		doc.close();
//
//		System.out.println("Done");
//
//		File f = null;
//		if (Desktop.isDesktopSupported()) {
//			f = new File(filename);
//			System.out.println(f.exists());
//			if (f.canRead()) {
//				System.out.println("DONE");
//				System.out.println(f.getName());
//				System.out.println(f.getAbsolutePath());
//				return f.getName();
//			}
//		}
//
//		return null;
//
//	}

	public String printLable(Transaction t, String path) throws Exception {
		if (!t.getType().equals(TransactionType.TRANS_IN))
			return null;
		System.out.println(t.toString());

		Document doc = new Document(PageSize.A4);
		doc.setMargins(10f, 10f, 10f, 10f);
		String filename = path + "Label-Obat-" + t.getCode() + ".pdf";
		PdfWriter.getInstance(doc, new FileOutputStream(filename));
		doc.open();
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

	public void printLPLPO(String kodeLokasiTr, int bulan, int tahun, String path, Configuration konfig,
			OutputStream os) throws Exception {

		WritableWorkbook wb;
		List<Product> daftarObatLpLpo = new ArrayList<>();
		List<Product> obatNonAlat = productRepository.findByUtilityTool(false);
		List<Product> obatAlat = productRepository.findByUtilityTool(true);

		daftarObatLpLpo.addAll(obatNonAlat);
		daftarObatLpLpo.addAll(obatAlat);
		WritableCellFormat formatNama = new WritableCellFormat();
		WritableCellFormat formatNamaobat = new WritableCellFormat();
		WritableCellFormat formatUmum = new WritableCellFormat();
		formatNamaobat.setBorder(Border.ALL, BorderLineStyle.THIN);
		formatNamaobat.setShrinkToFit(true);
		formatNama.setBorder(Border.ALL, BorderLineStyle.THIN);
		formatUmum.setBorder(Border.ALL, BorderLineStyle.THIN);
		formatUmum.setAlignment(Alignment.CENTRE);
		formatUmum.setVerticalAlignment(VerticalAlignment.CENTRE);

		wb = Workbook.createWorkbook(os);
		WritableSheet sheet = wb.createSheet("LPLPO " + bulan + "-" + tahun, 0);

		// JUDUL

		sheet.addCell(new Label(3, 3, healthCenterRepository.findTop1ByCode(kodeLokasiTr).getName()));
		sheet.addCell(new Label(6, 2, "Pelaporan pemakaian: " + strMonth(bulan, tahun)));
		sheet.addCell(new Label(6, 3, "Permintaan bulan: " + strMonth(bulan + 1, tahun)));

		Label[] labelJudul = new Label[18];
		labelJudul[0] = new Label(2, 5, "No", formatUmum);
		labelJudul[1] = new Label(3, 5, "Nama Obat/Alkes", formatUmum);
		labelJudul[2] = new Label(4, 5, "Satuan", formatUmum);
		labelJudul[3] = new Label(5, 5, "Stok Awal", formatUmum);
		labelJudul[4] = new Label(6, 5, "Penerimaan", formatUmum);
		labelJudul[5] = new Label(7, 5, "Persediaan", formatUmum);
		labelJudul[6] = new Label(8, 5, "Pemakaian", formatUmum);
		labelJudul[7] = new Label(9, 5, "Sisa Stok", formatUmum);
		labelJudul[8] = new Label(10, 5, "Stok OPT", formatUmum);
		labelJudul[9] = new Label(11, 5, "Permintaan", formatUmum);

		sheet.mergeCells(12, 5, 15, 5);
		labelJudul[10] = new Label(12, 5, "Pemberian", formatUmum);
		// labelJudul[13] = new Label(12, 6, "Obat", formatUmum);
		for (int i = 0; i < 4; i++) {
			sheet.addCell(new Label(12 + i, 6, "Obat", formatUmum));
		}
		labelJudul[11] = new Label(12, 7, "PKD", formatUmum);
		labelJudul[12] = new Label(13, 7, "Askes", formatUmum);
		labelJudul[13] = new Label(14, 7, "Program", formatUmum);
		labelJudul[14] = new Label(15, 7, "Lain2", formatUmum);

		labelJudul[15] = new Label(17, 5, "Ket", formatUmum);
		labelJudul[16] = new Label(16, 5, "Jumlah", formatUmum);

		int baris = 8;
		int koljudul = 2;

		for (int i = 0; i < labelJudul.length; i++) {
			if (i < 10 || i >= 14) {
				sheet.mergeCells(koljudul, 5, koljudul, 7);
			}
			if (labelJudul[i] != null) {
				sheet.addCell(labelJudul[i]);
			}
			koljudul++;
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, bulan - 1);
		cal.set(Calendar.YEAR, tahun);
		List<Transaction> trSblm = transactionRepository.findByTransactionDateBefore(cal.getTime());

		List<Transaction> trBulanIni = transactionRepository.findByMonthAndYear(bulan, tahun);
		List<ProductFlow> daftarAO = new ArrayList<>();
		int no = 1;
		System.out.println("ukuran tr: " + trSblm.size());
		for (Transaction t : trBulanIni) {
			t.setProductFlows(aliranObatRepository.findByTransaction(t));
		}

		for (Product o : daftarObatLpLpo) {
			System.out.print("*");
			// nomer
			sheet.addCell(new jxl.write.Number(2, baris, no, formatUmum));
			// nama obat
			sheet.addCell(new Label(3, baris, o.getName(), formatNamaobat));
			// satuan
			sheet.addCell(new Label(4, baris, o.getUnit().getName(), formatUmum));

			// STOK AWAL BULAN i
			// transaksi sblm tgl
			Integer stokAwalBln = 0;
			Integer penerimaan = 0;
			Integer pemakaian = 0;
			if (kodeLokasiTr.equals(bindedValues.getMasterHealthCenterCode())) {
				for (Transaction t : trSblm) {
					daftarAO = t.getProductFlows(); // daoao.listAliranObatDenganKodeTransaksi(t. getCode());
					// tr masuk
					if (t.getType().equals(TransactionType.TRANS_IN)) {
						for (ProductFlow ao : daftarAO) {
							if (o.getId().equals(ao.getProduct().getId())) {
								stokAwalBln += ao.getCount();
							}
						}
					} // tr keluar
					else if (t.getType().equals(TransactionType.TRANS_OUT) && t.getHealthCenterDestination() == null) {
						for (ProductFlow ao : daftarAO) {
							if (o.getId().equals(ao.getProduct().getId())) {
								stokAwalBln -= ao.getCount();
							}
						}
					}
				}
				// PENERIMAAN DAN PEMAKAIAN BULAN INI
				for (Transaction t : trBulanIni) {
					daftarAO = t.getProductFlows();
					// penerimaan
					if (t.getType().equals(TransactionType.TRANS_IN)) {
						for (ProductFlow ao : daftarAO) {
							if (o.getId().equals(ao.getProduct().getId())) {
								penerimaan += ao.getCount();
							}
						}
					} // Pemakaian
					else if (t.getType().equals(TransactionType.TRANS_OUT)
							&& t.getHealthCenterDestination().getCode() == null) {
						for (ProductFlow ao : daftarAO) {
							if (o.getId().equals(ao.getProduct().getId())) {
								pemakaian += ao.getCount();
							}
						}
					}
				}

			} else {
				for (Transaction t : trSblm) {
					daftarAO = t.getProductFlows();
					// tr masuk ke pustu
					if (t.getType().equals(TransactionType.TRANS_OUT_TO_WAREHOUSE)
							&& t.getHealthCenterDestination() != null
							&& t.getHealthCenterDestination().getCode().equals(kodeLokasiTr)) {
						for (ProductFlow ao : daftarAO) {
							if (o.getId().equals(ao.getProduct().getId())) {
								stokAwalBln += ao.getCount();
							}
						}
					} // tr keluar
					else if (t.getType().equals(TransactionType.TRANS_OUT) && t.getCustomer() != null
							&& (t.getHealthCenterLocation().getCode().equals(kodeLokasiTr)
									|| kodeLokasiTr.equals("01"))) {
						for (ProductFlow ao : daftarAO) {
							if (o.getId().equals(ao.getProduct().getId())) {
								stokAwalBln -= ao.getCount();
							}
						}
					}
				}
				// PENERIMAAN DAN PEMAKAIAN BULAN INI
				for (Transaction t : trBulanIni) {
					daftarAO = t.getProductFlows();
					// penerimaan
					if (t.getType().equals(TransactionType.TRANS_OUT_TO_WAREHOUSE)
							&& t.getHealthCenterDestination() != null
							&& t.getHealthCenterDestination().getCode().equals(kodeLokasiTr)) {
						for (ProductFlow ao : daftarAO) {
							if (o.getId().equals(ao.getProduct().getId())) {
								penerimaan += ao.getCount();
							}
						}
					} // Pemakaian
					else if (t.getType().equals(TransactionType.TRANS_OUT) && t.getCustomer() != null
							&& t.getHealthCenterLocation().getCode().equals(kodeLokasiTr)) {
						for (ProductFlow ao : daftarAO) {
							if (o.getId().equals(ao.getProduct().getId())) {
								pemakaian += ao.getCount();
							}
						}
					}
				}
			}

			sheet.addCell(new jxl.write.Number(5, baris, stokAwalBln, formatUmum));
			sheet.addCell(new jxl.write.Number(6, baris, penerimaan, formatUmum));
			sheet.addCell(new jxl.write.Number(7, baris, stokAwalBln + penerimaan, formatUmum));
			sheet.addCell(new jxl.write.Number(8, baris, pemakaian, formatUmum));
			sheet.addCell(new jxl.write.Number(9, baris, stokAwalBln + penerimaan - pemakaian, formatUmum));
			for (int i = 0; i < 8; i++) {
				sheet.addCell(new Label(10 + i, baris, "", formatUmum));
			}
			no++;
			baris++;
		}

		wb.write();
		wb.close();

	}

	public boolean transactionExist(String kodetransaksi, List<Transaction> list) {
		for (Transaction t : list) {
			if (t.getCode().equals(kodetransaksi)) {
				return true;
			}
		}
		return false;
	}

	public XSSFWorkbook getStockOpnameReport(Date d, HealthCenter location, HttpServletRequest httpServletRequest)
			throws Exception {

		List<Product> listObat = (List<Product>) productRepository.findAll();
		progressService.sendProgress(10, httpServletRequest);
		log.info("products: {}", listObat.size());
		boolean isMasterHealthCenter = defaultHealthCenterMasterService.isMasterHealthCenter(location);
		for (Product product : listObat) {
			log.info("get stock info for: {}", product.getName());
			List<ProductFlow> productFlows;
			if (isMasterHealthCenter) {
				productFlows = aliranObatRepository.findAvailabeProductsAtMainWareHouseAtDate(product.getId(), d);
			} else {
				productFlows = aliranObatRepository.findAvailabeProductsAtBranchWareHouseAtDate(location.getId(),
						product.getId(), d);
			}
			List<Long> prices = aliranObatRepository.getProductPriceAtDate(product.getId(), d, PageRequest.of(0, 1));
			int count = ProductFlow.sumCount(productFlows);
			product.setCount(count);
			product.setPrice(prices.size() == 0 ? 0 : prices.get(0).intValue());

			progressService.sendProgress(1, listObat.size(), 80, httpServletRequest);
		}

		try {
			StockOpnameGenerator generator = new StockOpnameGenerator(location, listObat, d);
			XSSFWorkbook wb = generator.generateReport();
			progressService.sendProgress(10, httpServletRequest);

			return wb;
		} catch (Exception e) {
			log.error("Error generating stock opname report: {}", e);
			e.printStackTrace();
			throw e;
		}
	}

	public String numbToCurrencyString(Integer Int) {
		String nominal = Int.toString();
		String hasil = "";
		if (nominal.length() > 3) {
			int nol = 0;
			for (int i = nominal.length() - 1; i > 0; i--) {
				nol++;
				hasil = nominal.charAt(i) + hasil;
				if (nol == 3) {
					hasil = "." + hasil;
					nol = 0;
				}

			}
			hasil = nominal.charAt(0) + hasil;
		} else {
			hasil = Int.toString();
		}
		return hasil;
	}

	public String strMonth(Integer bulan, Integer tahun) {
		String tgl = "";
		switch (bulan) {
		case 1:
			tgl = "Januari";
			break;
		case 2:
			tgl = "Februari";
			break;
		case 3:
			tgl = "Maret";
			break;
		case 4:
			tgl = "April";
			break;
		case 5:
			tgl = "Mei";
			break;
		case 6:
			tgl = "Juni";
			break;
		case 7:
			tgl = "Juli";
			break;
		case 8:
			tgl = "Agustus";
			break;
		case 9:
			tgl = "September";
			break;
		case 10:
			tgl = "Oktotber";
			break;
		case 11:
			tgl = "Nopember";
			break;
		case 12:
			tgl = "Desembar";
			break;
		case 13:
		default:
			tgl = "Januari";
			tahun++;
			break;

		}
		tgl += " " + tahun.toString();
		return tgl;
	}
}
