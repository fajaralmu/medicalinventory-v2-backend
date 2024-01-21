package com.pkm.medicalinventory.report.builder;

import java.io.OutputStream;
import java.util.function.Consumer;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.pkm.medicalinventory.exception.ApplicationException;
import com.pkm.medicalinventory.report.WritableReport;

public class TransRecepitReport implements WritableReport {

	private final String fileName;
	private final java.util.function.Consumer<Document> action;
	private final Document doc;

	public TransRecepitReport(Document doc, String fileName, Consumer<Document>action) {
		this.fileName = fileName;
		this.action = action;
		this.doc = doc;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public void write(OutputStream os) {
		try {
			PdfWriter.getInstance(doc, os);
			action.accept(doc);
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new ApplicationException(e);
		}

	}

}
