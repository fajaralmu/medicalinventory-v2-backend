package com.pkm.medicalinventory.report;

import com.pkm.medicalinventory.dto.Filter;
import com.pkm.medicalinventory.dto.WebRequest;

public interface ReportGenerator {

	WritableReport getStockOpnameReport(String fileName, WebRequest webRequest);

	WritableReport getMonthyReport(String fileName, Filter filter);

	WritableReport getRecipeReport(String fileName, Filter filter);

	WritableReport generateLPLPO(String fileName, WebRequest webRequest);

	WritableReport generateTransactionReceipt(String fileName, String code);

}
