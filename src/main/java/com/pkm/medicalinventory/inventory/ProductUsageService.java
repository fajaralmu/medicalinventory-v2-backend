package com.pkm.medicalinventory.inventory;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pkm.medicalinventory.entity.HealthCenter;
import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.entity.ProductFlow;

public interface ProductUsageService {

	public int getIncomingProductBetweenDate(
		Product product,
		HealthCenter location,
		Date date1,
		Date date2);

	public int getUsedProductBetweenDate(
		Product product,
		HealthCenter location,
		Date date1,
		Date date2);

	public Map<Long, Integer> getUsedProductsBetweenDate(
		List<Product> products,
		HealthCenter location,
		Date date1,
		Date date2);

	public Map<Long, List<ProductFlow>> getUsedProductsBetweenDatev2(
		List<Product> products,
		HealthCenter location,
		Date date1,
		Date date2);

	public Map<Long, Integer> getIncomingProductsBetweenDate(
		List<Product> products,
		HealthCenter location,
		Date date1,
		Date date2);

	public Map<Long, List<ProductFlow>> getIncomingProductsBetweenDatev2(
		List<Product> products,
		HealthCenter location,
		Date date1,
		Date date2);
}
