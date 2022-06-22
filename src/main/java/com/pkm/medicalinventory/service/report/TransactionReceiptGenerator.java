package com.pkm.medicalinventory.service.report;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.TabSettings;
import com.itextpdf.text.Font.FontStyle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.pkm.medicalinventory.constants.TransactionType;
import com.pkm.medicalinventory.entity.Customer;
import com.pkm.medicalinventory.entity.HealthCenter;
import com.pkm.medicalinventory.entity.ProductFlow;
import com.pkm.medicalinventory.entity.Supplier;
import com.pkm.medicalinventory.entity.Transaction;

public class TransactionReceiptGenerator extends BaseReportGenerator{
	
	private final Transaction transaction;
	private final HealthCenter mainLocation;
	
	static final DecimalFormat df 		= new DecimalFormat("#,###.##");
	static final String DATE_PATTERN 	= "dd-MM-yyyy";
	static final float FONT_MULTIPLIER 	= 1.2f;

	public TransactionReceiptGenerator(Transaction transaction, HealthCenter mainLocation ) {
		this.transaction = transaction;
		this.mainLocation = mainLocation;
	}
	
	private static String df(double value) {
		return df.format(value);
	}

	public void generateReport(OutputStream os) throws  Exception {
		
		HealthCenter location 	= this.mainLocation;
		
		Document doc = new Document(PageSize.A5);
		PdfPTable pt = new PdfPTable(8);
		// pt.setTotalWidth(800);
		pt.setTotalWidth(new float[] { 
			50,  // no
			200, // name
			170, // exp date
			170, // batch
			120, // record id
			100, // qty
			200, // price @
			250  // total price
		});
		PdfWriter.getInstance(doc, os);

		doc.open();
		SimpleDateFormat dateFmt = new SimpleDateFormat(DATE_PATTERN);
		
		Font fontDetail 	= FontFactory.getFont(FontFactory.COURIER, 4f * FONT_MULTIPLIER);
		Font fontTitle 		= FontFactory.getFont(FontFactory.HELVETICA, 5f * FONT_MULTIPLIER);
		Font fontAddress 	= FontFactory.getFont(FontFactory.HELVETICA, 4f * FONT_MULTIPLIER);
		Font fontEmail 		= FontFactory.getFont(FontFactory.HELVETICA, 4f * FONT_MULTIPLIER);
		Font fontHeadline 	= FontFactory.getFont(FontFactory.HELVETICA_BOLD, 6f * FONT_MULTIPLIER);

		fontEmail.setStyle(FontStyle.UNDERLINE.ordinal());

//		Desa Village Kec.Mulicipality.Kab.Kebumen.Telp.(0123)334567Kebumen
		Paragraph pKop1 	= new Paragraph("Struk Transaksi", fontHeadline);
		Paragraph pKop2 	= new Paragraph(location.getName(), fontHeadline);
		Paragraph pAlamat 	= new Paragraph("Alamat: "+location.getAddress(), fontAddress);
		Paragraph pContact 	= new Paragraph(location.getContact(), fontEmail);

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

		PdfPCell emptyCell = new PdfPCell(new Phrase(""));
		emptyCell.setBorder(Rectangle.TOP);

		Double totalItem = 0d;
		Double totalPrice = 0d;
		Paragraph transInfo[] = new Paragraph[4];
		transInfo[0] = labelValue("Kode transaksi", transaction. getCode(), fontTitle);
		transInfo[1] = labelValue("Tanggal", dateToString(transaction.getTransactionDate()), fontTitle);

		Customer p = null;

		if (transaction.getType().equals(TransactionType.TRANS_OUT) || transaction.getType().equals(TransactionType.TRANS_OUT_TO_WAREHOUSE)) {
			Customer customer 			= transaction.getCustomer();
			HealthCenter destination 	= transaction.getHealthCenterDestination();

			String code = customer != null ? customer.getCode() : destination.getCode();
			String name = customer != null ? customer.getName() : destination.getName();
			
			transInfo[2] = labelValue("Kode penerima", code, fontTitle);
			transInfo[3] = labelValue("Nama penerima", name, fontTitle);
		} else {
			Supplier supplier = transaction.getSupplier();
			transInfo[2] = labelValue("Kode pemasok", supplier.getId(), fontTitle);
			transInfo[3] = labelValue("Nama pemasok", supplier.getName(), fontTitle);
		}

		for (int i = 0; i < transInfo.length; i++) {
			transInfo[i].setIndentationLeft(30);
			doc.add(transInfo[i]);
		}

		if (transaction. getCustomer() != null) {
			p = transaction. getCustomer();

			Paragraph pharDOB 		= labelValue("Tgl Lahir", dateFmt.format(p.getBirthDate()), fontTitle);
			Paragraph pharAge 		= labelValue("Umur", p.toModel().getAge(), fontTitle);
			Paragraph pharAddress 	= labelValue("Alamat", p.getAddress(), fontTitle);

			pharDOB.setIndentationLeft(30);
			pharAge.setIndentationLeft(30);
			pharAddress.setIndentationLeft(30);
			doc.add(pharDOB);
			doc.add(pharAge);
			doc.add(pharAddress);
		}

		Integer i = 1;
		PdfPCell noHead 			= new PdfPCell(new Phrase("No", fontTitle));
		PdfPCell namaHead 			= new PdfPCell(new Phrase("Nama Obat", fontTitle));
		PdfPCell expDate 			= new PdfPCell(new Phrase("Kadaluarsa", fontTitle));
		PdfPCell batch 				= new PdfPCell(new Phrase("Batch", fontTitle));
		PdfPCell idStok 			= new PdfPCell(new Phrase("Record Id", fontTitle));
		PdfPCell jmlHead 			= new PdfPCell(new Phrase("Qty", fontTitle));
		PdfPCell hargaSatuanHead 	= new PdfPCell(new Phrase("Harga Satuan", fontTitle));
		PdfPCell totalHargaItemHead = new PdfPCell(new Phrase("Harga Total", fontTitle));

		noHead.setBorder(Rectangle.BOTTOM);
		namaHead.setBorder(Rectangle.BOTTOM);
		expDate.setBorder(Rectangle.BOTTOM);
		batch.setBorder(Rectangle.BOTTOM);
		idStok.setBorder(Rectangle.BOTTOM);
		jmlHead.setBorder(Rectangle.BOTTOM);
		hargaSatuanHead.setBorder(Rectangle.BOTTOM);
		totalHargaItemHead.setBorder(Rectangle.BOTTOM);

		pt.addCell(noHead);
		pt.addCell(namaHead);
		pt.addCell(expDate);
		pt.addCell(batch);
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

			PdfPCell nama 	= new PdfPCell(new Phrase(Namaobat, fontDetail));
			PdfPCell jml 	= new PdfPCell(new Phrase(String.valueOf(ao.getCount()), fontDetail));

			long id_stok 			= ao.getId();
			boolean includeExpDate 	= true;
			
			final String expDateString;
						
			if (!includeExpDate) {
				expDateString = "-";
			} else {
				expDateString = dateFmt.format(ao.getExpiredDate());
				 
			}
			PdfPCell exp 				= new PdfPCell(new Phrase(expDateString, fontDetail));
			PdfPCell itemBatch			= new PdfPCell(new Phrase(ao.getBatchNum(), fontDetail));
			PdfPCell ID_STOK 			= new PdfPCell(new Phrase(String.valueOf(id_stok), fontDetail));
			PdfPCell no 				= new PdfPCell(new Phrase(i.toString(), fontDetail));
			PdfPCell hargaItem 			= new PdfPCell(new Phrase(df(ao.getPrice()), fontDetail));
			Double totalHargaItem_int 	= (double) (ao.getPrice() * ao.getCount());
			PdfPCell totalHargaItem 	= new PdfPCell(new Phrase(df(totalHargaItem_int), fontDetail));
			
			no.setBorder(Rectangle.NO_BORDER);
			nama.setBorder(Rectangle.NO_BORDER);
			jml.setBorder(Rectangle.NO_BORDER);
			exp.setBorder(Rectangle.NO_BORDER);
			itemBatch.setBorder(Rectangle.NO_BORDER);
			ID_STOK.setBorder(Rectangle.NO_BORDER);
			hargaItem.setBorder(Rectangle.NO_BORDER);
			totalHargaItem.setBorder(Rectangle.NO_BORDER);
			
			pt.addCell(no);
			pt.addCell(nama);
			pt.addCell(exp);
			pt.addCell(itemBatch);
			pt.addCell(ID_STOK);
			pt.addCell(jml);
			pt.addCell(hargaItem);
			pt.addCell(totalHargaItem);
			
			totalPrice = totalPrice + totalHargaItem_int;
			totalItem = totalItem + ao.getCount();
			// System.out.println("total " + total);
			i++;
			notifyProgress(1, transaction. getProductFlows().size(), 50);
		}
		PdfPCell labelTotal 	= new PdfPCell(new Phrase("Total", fontTitle));
		PdfPCell jmlTotal 		= new PdfPCell(new Phrase(totalItem.toString(), fontTitle));
		PdfPCell jmlHargaTotal 	= new PdfPCell(new Phrase("Rp " + df(totalPrice), fontTitle));

		labelTotal.setBorder(Rectangle.TOP);
		jmlTotal.setBorder(Rectangle.TOP);
		jmlHargaTotal.setBorder(Rectangle.TOP);
		
		pt.addCell(emptyCell);
		pt.addCell(labelTotal);
		pt.addCell(emptyCell);
		pt.addCell(emptyCell);
		pt.addCell(emptyCell);
		pt.addCell(jmlTotal);
		pt.addCell(emptyCell);
		pt.addCell(jmlHargaTotal);
		
		doc.add(Chunk.NEWLINE);
		doc.add(pt);
		doc.close();
	}

	public String numbToCurrencyString(Object Int) {
		return String.valueOf(Int);
	}

	static Paragraph labelValue(String label, Object value, Font font) {
		if (value == null) {
			value = "";
		}
		final Paragraph p = new Paragraph();
		p.setFont(font);
		p.setTabSettings(new TabSettings(55f));
		p.setSpacingAfter(-5);
		p.setSpacingBefore(0);
		p.setExtraParagraphSpace(0);
		p.setPaddingTop(0);
		
		p.add(new Chunk(label));
		p.add(Chunk.TABBING);
		p.add(new Chunk(": " + value));
		return p;
	}
}
