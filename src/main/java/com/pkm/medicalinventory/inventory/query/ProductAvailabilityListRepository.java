package com.pkm.medicalinventory.inventory.query;

import java.util.Date;
import java.util.List;

import org.springframework.lang.Nullable;

import com.pkm.medicalinventory.dto.Filter;
import com.pkm.medicalinventory.entity.Product;

public interface ProductAvailabilityListRepository {
	List<Product> getAvailableProducts(boolean isMasterHealthCenter, Filter filter, Long locationId);

	List<Product> findNotEmptyProductAtBranchWarehouse(@Nullable Date expiredDateWithin, Filter filter, Long locationId);

	List<Product> findNotEmptyProductAtMasterWarehouse(@Nullable Date expDateBefore, Filter filter);

	List<Product> findNotEmptyProductAtAllLocation(@Nullable Date expiredDateWithin, Filter filter);

	List<Product> getAvailableProductsAllLocation(Filter filter);

}
