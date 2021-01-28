package com.fajar.medicalinventory.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.entity.ProductFlow;

@Repository
public interface ProductFlowRepository extends CrudRepository<ProductFlow, Integer> {

	 
	
}
