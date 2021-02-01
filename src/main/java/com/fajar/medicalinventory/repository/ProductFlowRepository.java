package com.fajar.medicalinventory.repository;

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
//	@Query(nativeQuery = true, value = "select  * from product_flow pf left join  transaction  tx on transaction_id = tx.id "
//			+ "left join product p on p.id = product_id where tx.type = 'TRANS_IN'   and p.code=?1 and "
//			+ "(pf.count- coalesce(pf.used_count, 0) ) > 0")
	List<ProductFlow> findAvailabeProductsAtMainWareHouse(String code);

	@Query(nativeQuery = true, value = "select * from product_flow pf left join  transaction  tx on transaction_id = tx.id "
			+ "left join product p on p.id = product_id where tx.type = 'TRANS_OUT_TO_WAREHOUSE'   "
			+ "and tx.health_center_destination_id = ?1 and p.code = ?2 and "
			+ " (pf.count- coalesce(pf.used_count, 0) ) > 0 ")
	List<ProductFlow> findAvailabeProductsAtBranchWareHouse(Long locationId, String productCode);

}
