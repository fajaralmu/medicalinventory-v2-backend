package com.fajar.medicalinventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.entity.Transaction;

@Repository
public interface ProductFlowRepository extends JpaRepository<ProductFlow, Integer> {

	List<ProductFlow> findByTransaction(Transaction transaction);

	List<ProductFlow> findByProduct_codeAndTransaction_type(String code, TransactionType transIn);

	 
	
}
