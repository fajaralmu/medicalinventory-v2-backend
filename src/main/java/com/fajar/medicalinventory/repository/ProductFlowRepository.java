package com.fajar.medicalinventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.entity.ProductFlow;

@Repository
public interface ProductFlowRepository extends JpaRepository<ProductFlow, Integer> {

	 
	
}
