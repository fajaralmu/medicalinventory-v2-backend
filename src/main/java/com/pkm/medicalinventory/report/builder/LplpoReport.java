package com.pkm.medicalinventory.report.builder;

import java.io.OutputStream;
import java.util.function.Consumer;

import com.pkm.medicalinventory.report.WritableReport;

public class LplpoReport implements WritableReport {

	private final String fileName;
	private final Consumer<OutputStream> onWrite;

	public LplpoReport(String fileName, Consumer<OutputStream> onWrite) {
		this.fileName = fileName;
		this.onWrite = onWrite;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public void write(OutputStream os) {
		onWrite.accept(os);
	}

}
