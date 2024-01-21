package com.pkm.medicalinventory.report;

import com.pkm.medicalinventory.dto.WebRequest;

public interface ReportService {

	WritableReport printStockOpname(WebRequest req);

	WritableReport printMonthlyReport(WebRequest req);

	WritableReport printRecipeReport(WebRequest req);

	WritableReport receiveRequestSheet(WebRequest req);

	WritableReport generateTransactionReceipt(String code);

	WritableReport generateEntityReport(WebRequest request);

}
