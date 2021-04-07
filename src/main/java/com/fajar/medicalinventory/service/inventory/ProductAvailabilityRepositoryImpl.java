package com.fajar.medicalinventory.service.inventory;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.dto.Filter;
import com.fajar.medicalinventory.entity.Product;

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
	public List<Product> findNotEmptyProductAtBranchWarehouse(Date expiredDateWithin, PageRequest pageable,
			Long locationId) {
		
		return listRepository.findNotEmptyProductAtBranchWarehouse(expiredDateWithin, pageable, locationId);
	}

	@Override
	public List<Product> findNotEmptyProductAtMasterWarehouse(Date expDateBefore, PageRequest pageable) {
		
		return listRepository.findNotEmptyProductAtMasterWarehouse(expDateBefore, pageable);
	}

	@Override
	public List<Product> findNotEmptyProductAtAllLocation(Date expiredDateWithin, PageRequest pageable) {
		
		return listRepository.findNotEmptyProductAtAllLocation(expiredDateWithin, pageable);
	}

	@Override
	public List<Product> getAvailableProductsAllLocation(Filter filter) {
		
		return listRepository.getAvailableProductsAllLocation(filter);
	}

	@Override
	public BigInteger countNontEmptyProduct(boolean isMasterHealthCenter, Integer expDaysWithin, Long locationId) {
		
		return countRepository.countNontEmptyProduct(isMasterHealthCenter, expDaysWithin, locationId);
	}

	@Override
	public BigInteger countNontEmptyProductAllLocation(boolean isMasterHealthCenter, Integer expDaysWithin) {
		
		return countRepository.countNontEmptyProductAllLocation(isMasterHealthCenter, expDaysWithin);
	}

}
