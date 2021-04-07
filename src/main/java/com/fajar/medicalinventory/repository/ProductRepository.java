package com.fajar.medicalinventory.repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.Unit;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByUnit(Unit unit);

	List<Product> findByName(String name);

	List<Product> findByUtilityTool(boolean isUtility);

	List<Product> findByOrderByName(Pageable of);
	 
	@Query("select p from Product p order by p.utilityTool, p.name")
	List<Product> findByOrderByUtilityTool();

	/**
	 * 
	 * @param date
	 * @return List<Object[] {id, price}>
	 */
	@Query(nativeQuery = true, value = "select id, (select  pf.price  from product_flow pf  "
			+ "left join  transaction tx on pf.transaction_id = tx.id "
			+ "left join  product p on p.id = pf.product_id  "
			+ "where tx.type = 'TRANS_IN' and p.id = p1.id and tx.transaction_date<=?1 "
			+ "group by tx.transaction_date, pf.id, p.name "
			+ "order by  tx.transaction_date desc limit 1) as price from product p1")
	List<Object[]> getMappedPriceAndProductIdsAt(Date date);

	Product findTop1ByCode(String code);

	
	@Query("select count(p) from Product p")
	BigInteger countAll(); 

}
