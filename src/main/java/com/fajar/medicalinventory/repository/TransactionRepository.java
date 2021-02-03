package com.fajar.medicalinventory.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.entity.HealthCenter;
import com.fajar.medicalinventory.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>  {

	Transaction findByCode(String code);

	List<Transaction> findByTransactionDateBefore(Date time);

	List<Transaction> findByTransactionDate(Date d);

	List<Transaction> findByTypeAndTransactionDateBefore(TransactionType transIn, Date d);

	@Query("select t from Transaction t where MONTH(t.transactionDate) = ?1 and YEAR(t.transactionDate) = ?2")
	List<Transaction> findByMonthAndYear(int bulan, int tahun);
	@Query("select t from Transaction t where MONTH(t.transactionDate) = ?1 and YEAR(t.transactionDate) = ?2 and t.type = ?3")
	List<Transaction> findByMonthAndYearAndType(int bulan, int tahun, TransactionType type);
	@Query("select t from Transaction t where"
			+ " MONTH(t.transactionDate) = ?1 and YEAR(t.transactionDate) = ?2"
			+ " and t.type = ?3 and t.healthCenterDestination = ?4")
	List<Transaction> findByMonthAndYearAndTypeAndDestination(int month, int year, TransactionType type,
			HealthCenter destination);
 
	  
}
