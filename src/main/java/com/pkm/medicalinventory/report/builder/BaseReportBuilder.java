package com.pkm.medicalinventory.report.builder;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.pkm.medicalinventory.report.WritableReport;
import com.pkm.medicalinventory.service.ProgressNotifier;

abstract class BaseReportBuilder implements ReportBuilder {

	protected ProgressNotifier progressNotifier;
	protected SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	protected SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
	protected final String fileName;

	public BaseReportBuilder(String fileName) {
		this.fileName = fileName;
	}

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

	public abstract WritableReport build();

}
