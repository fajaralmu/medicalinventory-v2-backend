package com.pkm.medicalinventory.inventory;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.entity.HealthCenter;
import com.pkm.medicalinventory.entity.Product;

public interface InventoryService {

	WebResponse getAvailableProductStocks(String code, WebRequest webRequest);
	WebResponse getProductStocks(WebRequest webRequest);
	void adjustStock();
	WebResponse getInventoriesData();
	WebResponse filterStock(WebRequest webRequest);
	
	Map<Long, Integer> getProductsStockAtDate(
		List<Product> products,
		HealthCenter location,
		Date selectedDate
	);

}
