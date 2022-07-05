package com.pkm.medicalinventory.service.report;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class ReportBuilder<WorkBook>{

	protected WorkBook xwb;
	protected ProgressNotifier progressNotifier;
	protected SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	protected SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
	protected String dateToTimeString(Date d) {
		return sdfDateTime.format(d);
	}
	protected String dateToString(Date d) {
		return sdfDate.format(d);
	}
	public void setProgressNotifier(ProgressNotifier progressNotifier) {
		this.progressNotifier = progressNotifier;
	}
	protected ProgressNotifier getProgressNotifier() {
		if (null == progressNotifier) {
			return ProgressNotifier.empty();
		}
		return progressNotifier;
	}
	protected void notifyProgress(int progress, int maxProgress, double percent) {
		getProgressNotifier().notify(progress, maxProgress, percent);
	}
	public abstract WorkBook build() throws Exception;
}
