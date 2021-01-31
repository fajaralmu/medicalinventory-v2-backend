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

	@Query(nativeQuery = true, value = "select pf.id, pf.created_date, pf.deleted, pf.modified_date,  pf.expired_date, pf.generic, pf.price, pf.suitable, pf.product_id, pf.reference_flow_id, pf.transaction_id, "
			+ "(pf.count-  coalesce((select sum(pf_used.count) from product_flow pf_used where pf_used.reference_flow_id = pf.id),0)  ) as count "
			+ "from product_flow pf left join  transaction  tx on transaction_id = tx.id "
			+ "left join product p on p.id = product_id where tx.type = 'TRANS_IN'   and p.code=?1 and "
			+ "(pf.count- coalesce((select sum(pf_used.count) from product_flow pf_used where pf_used.reference_flow_id = pf.id), 0) ) > 0")
	List<ProductFlow> findAvailabeProductsAtMainWareHouse(String code);

	@Query(nativeQuery = true, value = "select pf.id, pf.created_date, pf.deleted, pf.modified_date,  pf.expired_date, pf.generic, pf.price, pf.suitable, pf.product_id, pf.reference_flow_id, pf.transaction_id, "
			+ "(pf.count-  coalesce((select sum(pf_used.count) from product_flow pf_used where pf_used.reference_flow_id = pf.id),0)  ) as count "
			+ "from product_flow pf left join  transaction  tx on transaction_id = tx.id "
			+ "left join product p on p.id = product_id where tx.type = 'TRANS_OUT_TO_WAREHOUSE'   "
			+ "and tx.health_center_destination_id = ?1 and p.code = ?2 and "
			+ " (pf.count- coalesce((select sum(pf_used.count) from product_flow pf_used where pf_used.reference_flow_id = pf.id), 0) ) > 0 ")
	List<ProductFlow> findAvailabeProductsAtBranchWareHouse(Long locationId, String productCode);

}
