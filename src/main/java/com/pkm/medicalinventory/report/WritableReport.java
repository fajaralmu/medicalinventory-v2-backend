package com.pkm.medicalinventory.report;

import java.io.OutputStream;

public interface WritableReport {

	String getFileName();
	void write(OutputStream os);
}
