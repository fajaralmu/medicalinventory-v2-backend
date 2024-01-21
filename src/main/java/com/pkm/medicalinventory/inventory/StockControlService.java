package com.pkm.medicalinventory.inventory;

import com.pkm.medicalinventory.dto.model.TransactionModel;

public interface StockControlService {

	TransactionModel getTransactionRelatedRecords(String code);
	void adjustStock();

}
