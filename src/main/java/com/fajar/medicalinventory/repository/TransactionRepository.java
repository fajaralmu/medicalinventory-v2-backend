package com.fajar.medicalinventory.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.entity.Transaction;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long>  {
 
	  
}
