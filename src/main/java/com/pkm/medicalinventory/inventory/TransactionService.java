package com.pkm.medicalinventory.inventory;

import javax.servlet.http.HttpServletRequest;

import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.WebResponse;

public interface TransactionService {

	WebResponse performTransactionSupply(WebRequest request);

	WebResponse performDistribution(WebRequest request);

	WebResponse getTransactionByCode(String code);

	WebResponse deleteRecordByCode(String code);

}
