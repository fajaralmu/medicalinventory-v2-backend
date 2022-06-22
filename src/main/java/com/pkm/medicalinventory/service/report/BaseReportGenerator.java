package com.pkm.medicalinventory.service.report;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class BaseReportGenerator {

	protected XSSFWorkbook xwb;
	protected ProgressNotifier progressNotifier;
	protected SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	public String dateToString(Date d) {
		return sdfDateTime.format(d);

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
}
