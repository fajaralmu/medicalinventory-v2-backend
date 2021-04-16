package com.fajar.medicalinventory.service.inventory;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.lang.Nullable;

import com.fajar.medicalinventory.dto.Filter;
import com.fajar.medicalinventory.entity.Product;

public interface ProductAvailabilityRepository {
	public List<Product> getAvailableProducts(boolean isMasterHealthCenter, Filter filter, Long locationId);

	public List<Product> findNotEmptyProductAtBranchWarehouse(@Nullable Date expiredDateWithin, Filter filter, Long locationId);

	public List<Product> findNotEmptyProductAtMasterWarehouse(@Nullable Date expDateBefore, Filter filter);
	public List<Product> findNotEmptyProductAtAllLocation(@Nullable Date expiredDateWithin, Filter filter) ;

	public List<Product> getAvailableProductsAllLocation(Filter filter);
	
	public BigInteger countNontEmptyProduct(boolean isMasterHealthCenter, @Nullable Integer expDaysWithin, Filter filter, Long locationId);
	public BigInteger countNontEmptyProductAllLocation(boolean isMasterHealthCenter, Integer expDaysWithin, Filter filter );
}
