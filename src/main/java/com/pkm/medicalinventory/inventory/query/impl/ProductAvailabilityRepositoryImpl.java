package com.pkm.medicalinventory.inventory.query.impl;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pkm.medicalinventory.dto.Filter;
import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.inventory.query.ProductAvailabilityListRepository;
import com.pkm.medicalinventory.inventory.query.ProductAvailabilityRepository; 

@Service
public class ProductAvailabilityRepositoryImpl implements ProductAvailabilityRepository{

	@Autowired
	private ProductAvailabilityCountRepository countRepository;
	@Autowired
	private ProductAvailabilityListRepository listRepository;
	
	@Override
	public List<Product> getAvailableProducts(boolean isMasterHealthCenter, Filter filter, Long locationId) {
		return listRepository.getAvailableProducts(isMasterHealthCenter, filter, locationId);
	}

	@Override
	public List<Product> findNotEmptyProductAtBranchWarehouse(Date expiredDateWithin, Filter filter, Long locationId) {
		return listRepository.findNotEmptyProductAtBranchWarehouse(expiredDateWithin, filter, locationId);
	}

	@Override
	public List<Product> findNotEmptyProductAtMasterWarehouse(Date expDateBefore, Filter filter) {
		return listRepository.findNotEmptyProductAtMasterWarehouse(expDateBefore, filter);
	}

	@Override
	public List<Product> findNotEmptyProductAtAllLocation(Date expiredDateWithin, Filter filter) {		
		return listRepository.findNotEmptyProductAtAllLocation(expiredDateWithin, filter);
	}

	@Override
	public List<Product> getAvailableProductsAllLocation(Filter filter) {		
		return listRepository.getAvailableProductsAllLocation(filter);
	}

	@Override
	public BigInteger countNontEmptyProduct(boolean isMasterHealthCenter, Integer expDaysWithin, Filter filter, Long locationId) {		
		return countRepository.countNontEmptyProduct(isMasterHealthCenter, expDaysWithin, filter, locationId);
	}

	@Override
	public BigInteger countNontEmptyProductAllLocation(boolean isMasterHealthCenter, Integer expDaysWithin, Filter filter) {		
		return countRepository.countNontEmptyProductAllLocation(isMasterHealthCenter, expDaysWithin, filter);
	}

}
