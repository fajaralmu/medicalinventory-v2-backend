package com.fajar.medicalinventory.repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.entity.Transaction;

@Repository
public interface ProductFlowRepository extends JpaRepository<ProductFlow, Integer> {

	List<ProductFlow> findByTransaction(Transaction transaction);

	List<ProductFlow> findByProduct_codeAndTransaction_type(String code, TransactionType transIn);

	@Query("select pf from ProductFlow pf left join pf.transaction tx "
			+ " left join pf.product p "
			+ " where tx.type='TRANS_IN' and p.code = ?1 and (pf.count- pf.usedCount) > 0 ") 
	List<ProductFlow> findAvailabeProductsAtMainWareHouse(String code);

	@Query( "select pf from ProductFlow pf left join pf.transaction tx  "
			+ " left join pf.product p "
			+ " left join tx.healthCenterDestination location "
			+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE'   "
			+ " and location.id = ?1 and p.code = ?2 and "
			+ " (pf.count- pf.usedCount)  > 0 ")
	List<ProductFlow> findAvailabeProductsAtBranchWareHouse(Long locationId, String productCode);

	@Query("select pf.price from ProductFlow pf " + 
			"left join pf.transaction tx " + 
			"left join pf.product p " + 
			"where tx.transactionDate <= ?2 and tx.type = 'TRANS_IN' " + 
			"and p.id = ?1")
	BigInteger getProductPriceAtDate(Long id, Date d);

}
