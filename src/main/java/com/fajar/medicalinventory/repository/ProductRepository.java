package com.fajar.medicalinventory.repository;
 
import java.math.BigInteger;
import java.util.Date;
import java.util.List;import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.entity.Transaction;
import com.fajar.medicalinventory.entity.Unit;
 

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	 
	List<Product> findByUnit(Unit unit);
	List<Product> findByName(String name);
	List<Product> findByUtilityTool(boolean isUtility);
	List<Product> findByOrderByName(Pageable of);
	@Query("select count(p) from Product p")
	BigInteger countAll();
	
	@Query("select distinct(p) from " + 
			"ProductFlow pf " + 
			"left join pf.product p " + 
			"left join pf.transaction tx " + 
			"where tx.type = 'TRANS_IN' and (pf.count- pf.usedCount) > 0 order by p.name")
	List<Product> findNotEmptyProductInMasterWarehouse(Pageable of);
	
	@Query("select distinct(p) from " + 
			"ProductFlow pf " + 
			"left join pf.product p " + 
			"left join pf.transaction tx " + 
			" left join tx.healthCenterDestination location "+
			"where tx.type = 'TRANS_OUT_TO_WAREHOUSE' and location.id = ?1 and (pf.count- pf.usedCount) > 0 order by p.name")
	List<Product> findNotEmptyProductInSpecifiedWarehouse(Long location, Pageable of);
	Product findTop1ByCode(String code);
	List<Product> findByOrderByUtilityTool();
	
	/**
	 * 
	 * @param date
	 * @return List<Object[] {id, price}>
	 */
	@Query(nativeQuery = true, value ="select id, (select  pf.price  from product_flow pf  " + 
			"left join  transaction tx on pf.transaction_id = tx.id " + 
			"left join  product p on p.id = pf.product_id  " + 
			"where tx.type = 'TRANS_IN' and p.id = p1.id and tx.transactiondate<=?1 " + 
			"group by tx.transactiondate, pf.id, p.name " + 
			"order by  tx.transactiondate desc limit 1) as price from product p1")
	List<Object[]> getMappedPriceAndProductIdsAt(Date date);
	
}
