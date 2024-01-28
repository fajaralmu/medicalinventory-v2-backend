package com.pkm.medicalinventory.inventory.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pkm.medicalinventory.entity.HealthCenter;
import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.entity.ProductFlow;
import com.pkm.medicalinventory.inventory.ProductUsageService;
import com.pkm.medicalinventory.inventory.WarehouseService;
import com.pkm.medicalinventory.repository.readonly.ProductFlowRepository;

@Service
public class ProductUsageServiceImpl implements ProductUsageService  {

	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private ProductFlowRepository productFlowRepository;
	
	public int getIncomingProductBetweenDate(
		Product product,
		HealthCenter location,
		Date date1,
		Date date2
	) {
		BigInteger result;
		if (warehouseService.isMasterHealthCenter(location)) {
			result= productFlowRepository.getTotalIncomingProductFromSupplierBetweenDate(product.getId(), date1, date2);
		} else {
			result = productFlowRepository.getTotalIncomingProductAtBranchWarehouseBetweenDate(product.getId(), date1, date2, location.getId()); 
		}
		return result == null ? 0 : result.intValue();
	}

	public int getUsedProductBetweenDate(
		Product product,
		HealthCenter location,
		Date date1,
		Date date2
	) {
		BigInteger result;
		if (warehouseService.isMasterHealthCenter(location)) {
			result= productFlowRepository.getTotalUsedProductToCustomerOrBranchWarehouseBetweenDate(product.getId(), date1, date2, location.getId());
		} else {
			result = productFlowRepository.getTotalUsedProductToCustomerBetweenDate(product.getId(), date1, date2, location.getId()); 
		}
		return result == null ? 0 : result.intValue();
	}
	public Map<Long, Integer> getUsedProductsBetweenDate(
		List<Product> products,
		HealthCenter location,
		Date date1,
		Date date2
	) {
		Map<Long, Integer> result = getMapPopulatedWithKey(products);
		
		final List<Object[]> quantities;
		if (warehouseService.isMasterHealthCenter(location)) {
			quantities= productFlowRepository.getTotalUsedProductsToCustomerOrBranchWarehouseBetweenDate(products, date1, date2, location.getId());
		} else {
			quantities = productFlowRepository.getTotalUsedProductsToCustomerBetweenDate(products, date1, date2, location.getId()); 
		}
		for (Object[] objects : quantities) {
			Long productId = Long.parseLong(objects[0].toString());
			Integer count = Integer.parseInt(objects[1].toString());
			result.put(productId, count);
		}
		return result;
	}
	public Map<Long, List<ProductFlow>> getUsedProductsBetweenDatev2(
		List<Product> products,
		HealthCenter location,
		Date date1,
		Date date2
	) {
		Map<Long, List<ProductFlow>> result = getMapPopulatedWithKeyv2(products);
		
		final List<ProductFlow> quantities;
		if (warehouseService.isMasterHealthCenter(location)) {
			quantities = productFlowRepository.getUsedProductsToCustomerOrBranchWarehouseBetweenDate(products, date1, date2, location.getId());
		} else {
			quantities = productFlowRepository.getUsedProductsToCustomerBetweenDate(products, date1, date2, location.getId()); 
		}
		for (ProductFlow productFlow : quantities) {
			Long productId = productFlow.getProduct().getId();
			result.get(productId).add(productFlow);
		}
		return result;
	}
	private static Map<Long, Integer> getMapPopulatedWithKey(List<Product> products) {
		Map<Long, Integer> result = new HashMap<>();
		if (null== products || products.isEmpty()) {
			result.put(-1L, 0);
			return result;
		}
		
		products.forEach(p->{
			result.put(p.getId(), 0);
		});
		return result;
	}
	private static Map<Long, List<ProductFlow>> getMapPopulatedWithKeyv2(List<Product> products) {
		Map<Long,  List<ProductFlow>> result = new HashMap<>();
		if (null== products || products.isEmpty()) {
			result.put(-1L, new ArrayList<>());
			return result;
		}
		
		products.forEach(p->{
			result.put(p.getId(), new ArrayList<>());
		});
		return result;
	}


	public Map<Long, Integer> getIncomingProductsBetweenDate(
		List<Product> products,
		HealthCenter location,
		Date date1,
		Date date2
	) {
		Map<Long, Integer> result = getMapPopulatedWithKey(products);
		final List<Object[]> quantities;
		if (warehouseService.isMasterHealthCenter(location)) {
			quantities = productFlowRepository.getTotalIncomingProductsFromSupplierBetweenDate(products, date1, date2);
		} else {
			quantities = productFlowRepository.getTotalIncomingProductsAtBranchWarehouseBetweenDate(products, date1, date2, location.getId()); 
		}
		for (Object[] objects : quantities) {
			Long productId = Long.parseLong(objects[0].toString());
			Integer count = Integer.parseInt(objects[1].toString());
			result.put(productId, count);
		}
		return result;
	}
	public Map<Long, List<ProductFlow>> getIncomingProductsBetweenDatev2(
		List<Product> products,
		HealthCenter location,
		Date date1,
		Date date2
	) {
		Map<Long, List<ProductFlow>> result = getMapPopulatedWithKeyv2(products);
		final List<ProductFlow> quantities;
		if (warehouseService.isMasterHealthCenter(location)) {
			quantities = productFlowRepository.getIncomingProductsFromSupplierBetweenDate(products, date1, date2);
		} else {
			quantities = productFlowRepository.getIncomingProductsAtBranchWarehouseBetweenDate(products, date1, date2, location.getId()); 
		}
		for (ProductFlow productFlow : quantities) {
			Long productId = productFlow.getProduct().getId();
			result.get(productId).add(productFlow);
		}
		return result;
	}
}

