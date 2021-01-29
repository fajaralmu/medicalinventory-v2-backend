package com.fajar.medicalinventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>  {

	Transaction findByCode(String code);
 
	  
}
