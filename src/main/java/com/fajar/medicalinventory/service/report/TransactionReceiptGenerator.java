package com.fajar.medicalinventory.service.report;

import java.io.OutputStream;
import java.text.SimpleDateFormat;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.entity.Customer;
import com.fajar.medicalinventory.entity.HealthCenter;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.entity.Transaction;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class TransactionReceiptGenerator extends BaseReportGenerator{
	
	private final Transaction transaction;
	private final HealthCenter mainLocation;
	
	public TransactionReceiptGenerator(Transaction transaction, HealthCenter mainLocation ) {
		this.transaction = transaction;
		this.mainLocation = mainLocation;
	}
	
	
	
	public void generateReport(OutputStream os) throws  Exception {
		HealthCenter location = this.mainLocation;
		
		Document doc = new Document(PageSize.A5);
		PdfPTable pt = new PdfPTable(7);
		// pt.setTotalWidth(800);
		pt.setTotalWidth(new float[] { 40, 200, 100, 100, 100, 100, 100 });
		PdfWriter.getInstance(doc, os);

		doc.open();
		final String DATE_PATTERN = "dd-MM-yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
		Font fontRincianTransaksi = FontFactory.getFont(FontFactory.COURIER, 6f);
		Font fontJudulTabel = FontFactory.getFont(FontFactory.TIMES, 7f);

		Font fontAlamat = FontFactory.getFont(FontFactory.TIMES_ITALIC, 8f);
		Font fontEmail = FontFactory.getFont(FontFactory.TIMES, 8f);
		fontEmail.setStyle("underline");

		Font fontKop = FontFactory.getFont(FontFactory.TIMES_BOLD, 10f);
//		Desa Village Kec.Mulicipality.Kab.Kebumen.Telp.(0123)334567Kebumen
		Paragraph pKop1 = new Paragraph("DINAS KESEHATAN", fontKop);
		Paragraph pKop2 = new Paragraph(location.getName(), fontKop);
		Paragraph pAlamat = new Paragraph("Alamat: "+location.getAddress(),
				fontAlamat);
		Paragraph pContact = new Paragraph(location.getContact(), fontEmail);
		pAlamat.setAlignment(Element.ALIGN_CENTER);
		pContact.setAlignment(Element.ALIGN_CENTER);
		pKop1.setAlignment(Element.ALIGN_CENTER);
		pKop2.setAlignment(Element.ALIGN_CENTER);
//		pContact.setAlignment(Element.ALIGN_CENTER);
		// pJudul.setAlignment(Element.ALIGN_CENTER);
		doc.add(pKop1);
		doc.add(pKop2);
		doc.add(pAlamat);
		doc.add(pContact);
		// doc.add(pGaris);
		// doc.add(pJudul);
		doc.add(Chunk.NEWLINE);

		Double total = 0d;
		Double totalHarga = 0d;
		Paragraph identitas_transaksi[] = new Paragraph[4];
		identitas_transaksi[0] = new Paragraph("Kode transaksi: " + transaction. getCode(), fontJudulTabel);
		identitas_transaksi[1] = new Paragraph("Tanggal: " + dateToString(transaction.getTransactionDate()), fontJudulTabel);

		Customer p = null;

		if (transaction.getType().equals(TransactionType.TRANS_OUT) || transaction.getType().equals(TransactionType.TRANS_OUT_TO_WAREHOUSE)) {
			identitas_transaksi[2] = new Paragraph("Kode penerima: "
					+ (transaction. getCustomer() != null ? transaction. getCustomer(). getCode() : transaction. getHealthCenterDestination(). getCode()),
					fontJudulTabel);
			identitas_transaksi[3] = new Paragraph("Nama penerima: "
					+ (transaction. getCustomer() != null ? transaction. getCustomer(). getName() : transaction. getHealthCenterDestination(). getName()),
					fontJudulTabel);
		} else {
			identitas_transaksi[2] = new Paragraph("Kode pemasok: " + transaction. getSupplier().getId(), fontJudulTabel);
			identitas_transaksi[3] = new Paragraph("Nama pemasok: " + transaction. getSupplier().getName(), fontJudulTabel);
		}

		for (int i = 0; i < identitas_transaksi.length; i++) {
			identitas_transaksi[i].setIndentationLeft(30);
			doc.add(identitas_transaksi[i]);
		}

		if (transaction. getCustomer() != null) {
			p = transaction. getCustomer();

			Paragraph tgllahir_par = new Paragraph("Tgl Lahir: " + simpleDateFormat.format(p.getBirthDate()),
					fontJudulTabel);
			Paragraph age_par = new Paragraph("Umur: " +p.toModel().getAge(),
					fontJudulTabel);
			Paragraph alamat_par = new Paragraph("Alamat: " + p.getAddress(), fontJudulTabel);
			tgllahir_par.setIndentationLeft(30);
			age_par.setIndentationLeft(30);
			alamat_par.setIndentationLeft(30);
			doc.add(tgllahir_par);
			doc.add(age_par);
			doc.add(alamat_par);
		}

		Integer i = 1;
		PdfPCell noHead = new PdfPCell(new Phrase("No", fontJudulTabel));
		PdfPCell namaHead = new PdfPCell(new Phrase("Nama Obat", fontJudulTabel));
		PdfPCell tglED = new PdfPCell(new Phrase("Kadaluarsa", fontJudulTabel));
		PdfPCell idStok = new PdfPCell(new Phrase("Record Id", fontJudulTabel));
		PdfPCell jmlHead = new PdfPCell(new Phrase("Qty", fontJudulTabel));
		PdfPCell hargaSatuanHead = new PdfPCell(new Phrase("Harga Satuan", fontJudulTabel));
		PdfPCell totalHargaItemHead = new PdfPCell(new Phrase("Harga Total", fontJudulTabel));
		noHead.setBorder(Rectangle.BOTTOM);
		namaHead.setBorder(Rectangle.BOTTOM);
		tglED.setBorder(Rectangle.BOTTOM);
		idStok.setBorder(Rectangle.BOTTOM);
		jmlHead.setBorder(Rectangle.BOTTOM);
		hargaSatuanHead.setBorder(Rectangle.BOTTOM);
		totalHargaItemHead.setBorder(Rectangle.BOTTOM);
		pt.addCell(noHead);
		pt.addCell(namaHead);
		pt.addCell(tglED);
		pt.addCell(idStok);
		pt.addCell(jmlHead);
		pt.addCell(hargaSatuanHead);
		pt.addCell(totalHargaItemHead);
		
		notifyProgress(10, 10, 10);
		
		for (ProductFlow ao : transaction. getProductFlows()) {
			String Namaobat = ao. getProduct().getName();
			if (ao.isGeneric()) {
				Namaobat += " (generik)";
			}

			PdfPCell nama = new PdfPCell(new Phrase(Namaobat, fontRincianTransaksi));
			PdfPCell jml = new PdfPCell(new Phrase(String.valueOf(ao.getCount()), fontRincianTransaksi));
			String tgl_ed = "";
			long id_stok = ao.getId();
			boolean includeED = true;
			if (!includeED) {
				tgl_ed = "-";
			} else {
				tgl_ed = simpleDateFormat.format(ao.getExpiredDate());
				 
			}
			PdfPCell ED = new PdfPCell(new Phrase(tgl_ed, fontRincianTransaksi));
			System.out.println("Bukti tr id stok: " + id_stok);
			PdfPCell ID_STOK = new PdfPCell(new Phrase(String.valueOf(id_stok), fontRincianTransaksi));
			PdfPCell no = new PdfPCell(new Phrase(i.toString(), fontRincianTransaksi));
			PdfPCell hargaItem = new PdfPCell(new Phrase(String.valueOf(ao.getPrice()), fontRincianTransaksi));
			Double totalHargaItem_int = (double) (ao.getPrice() * ao.getCount());
			PdfPCell totalHargaItem = new PdfPCell(new Phrase(totalHargaItem_int.toString(), fontRincianTransaksi));
			no.setBorder(Rectangle.NO_BORDER);
			nama.setBorder(Rectangle.NO_BORDER);
			jml.setBorder(Rectangle.NO_BORDER);
			ED.setBorder(Rectangle.NO_BORDER);
			ID_STOK.setBorder(Rectangle.NO_BORDER);
			hargaItem.setBorder(Rectangle.NO_BORDER);
			totalHargaItem.setBorder(Rectangle.NO_BORDER);
			pt.addCell(no);
			pt.addCell(nama);
			pt.addCell(ED);
			pt.addCell(ID_STOK);
			pt.addCell(jml);
			pt.addCell(hargaItem);
			pt.addCell(totalHargaItem);
			totalHarga = totalHarga + totalHargaItem_int;
			total = total + ao.getCount();
			// System.out.println("total " + total);
			i++;
			notifyProgress(1, transaction. getProductFlows().size(), 50);
		}
		PdfPCell kosong = new PdfPCell(new Phrase("", fontJudulTabel));
		PdfPCell labelTotal = new PdfPCell(new Phrase("total", fontJudulTabel));
		PdfPCell jmlTotal = new PdfPCell(new Phrase(total.toString(), fontJudulTabel));
		PdfPCell jmlHargaTotal = new PdfPCell(new Phrase("Rp " + numbToCurrencyString(totalHarga), fontJudulTabel));
		kosong.setBorder(Rectangle.TOP);
		labelTotal.setBorder(Rectangle.TOP);
		jmlTotal.setBorder(Rectangle.TOP);
		jmlHargaTotal.setBorder(Rectangle.TOP);
		pt.addCell(kosong);
		pt.addCell(labelTotal);
		pt.addCell(kosong);
		pt.addCell(kosong);
		pt.addCell(jmlTotal);
		pt.addCell(kosong);
		pt.addCell(jmlHargaTotal);
		doc.add(Chunk.NEWLINE);
		doc.add(pt);
		doc.close();
	}

	public String numbToCurrencyString(Object Int) {
		return String.valueOf(Int);
	}
}
