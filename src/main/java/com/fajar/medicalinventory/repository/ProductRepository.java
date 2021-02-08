package com.fajar.medicalinventory.repository;
 
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.Unit;
import com.fajar.medicalinventory.util.DateUtil;
 

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	 
	List<Product> findByUnit(Unit unit);
	List<Product> findByName(String name);
	List<Product> findByUtilityTool(boolean isUtility);
	List<Product> findByOrderByName(Pageable of);
	
	//LIST AT Master warehouse
	@Query("select distinct(p) from " + 
			" ProductFlow pf " + 
			" left join pf.product p left join pf.transaction tx " + 
			" where tx.type = 'TRANS_IN' and (pf.count- pf.usedCount) > 0 order by p.name")
	List<Product> findNotEmptyProductInMasterWarehouse(Pageable of);
	@Query("select distinct(p) from " + 
			" ProductFlow pf " + 
			" left join pf.product p left join pf.transaction tx " + 
			" where tx.type = 'TRANS_IN' and (pf.count- pf.usedCount) > 0 "
			+ " and pf.expiredDate <  ?1 "
			+ " order by p.name")
	List<Product> findNotEmptyProductInMasterWarehouseWithExpDateBefore(Date  expiredDateWithin, Pageable pageable);
	
	//LIST AT Branch warehouse
	@Query("select distinct(p) from " + 
			" ProductFlow pf  left join pf.product p " + 
			" left join pf.transaction tx left join tx.healthCenterDestination location "+
			" where tx.type = 'TRANS_OUT_TO_WAREHOUSE' and location.id = ?1 and (pf.count- pf.usedCount) > 0 order by p.name")
	List<Product> findNotEmptyProductInSpecifiedWarehouse(Long location, Pageable of);
	@Query("select distinct(p) from " + 
			" ProductFlow pf  left join pf.product p " + 
			" left join pf.transaction tx left join tx.healthCenterDestination location "+
			" where tx.type = 'TRANS_OUT_TO_WAREHOUSE' and location.id = ?1 and (pf.count- pf.usedCount) > 0 "
			+ " and pf.expiredDate <  ?1 order by p.name")
	List<Product> findNotEmptyProductInSpecifiedWarehouseWithExpDateBefore(Long location, Date expiredDateWithin, Pageable of);
	
	
	
	@Query("select p from Product p order by p.utilityTool, p.name")
	List<Product> findByOrderByUtilityTool();
	
	/**
	 * 
	 * @param date
	 * @return List<Object[] {id, price}>
	 */
	@Query(nativeQuery = true, value ="select id, (select  pf.price  from product_flow pf  " + 
			"left join  transaction tx on pf.transaction_id = tx.id " + 
			"left join  product p on p.id = pf.product_id  " + 
			"where tx.type = 'TRANS_IN' and p.id = p1.id and tx.transaction_date<=?1 " + 
			"group by tx.transaction_date, pf.id, p.name " + 
			"order by  tx.transaction_date desc limit 1) as price from product p1")
	List<Object[]> getMappedPriceAndProductIdsAt(Date date);
	
	
	Product findTop1ByCode(String code);
	
	/**
	 *  ========================== product count ========================= 
	 */
	
	@Query("select count(p) from Product p")
	BigInteger countAll();
	
	//COUNT AT Main warehouse
	@Query(nativeQuery = true, value="select count(c) from (select distinct(p.id) from "
			+ " product p " 
			+ " left join product_flow pf on pf.product_id =  p.id "
			+ " left join transaction tx on pf.transaction_id = tx.id" + 
			" where tx.type = 'TRANS_IN' and (pf.count- pf.used_count) > 0)  c")
	BigInteger countNotEmptyProductInMasterWareHouse();
	@Query(nativeQuery = true, value="select count(c) from (select distinct(p.id) from "
			+ " product p " 
			+ " left join product_flow pf on pf.product_id =  p.id "
			+ " left join transaction tx on pf.transaction_id = tx.id" + 
			" where tx.type = 'TRANS_IN' "
			+ " and DATE_PART('day', AGE(pf.expired_date, current_timestamp)) < ?1"
			+ " and  (pf.count- pf.used_count) > 0)  c  "
			 )
	BigInteger countNotEmptyProductInMasterWareHouseWithExpDaysBefore(Integer expiredDaysWithin);
	
	//COUNT AT Branch warehouse
	@Query(nativeQuery = true, value="select count(c) from (select distinct(p.id) from " + 
			" product p  left join product_flow pf on p.id = pf.product_id " + 
			" left join  transaction tx on tx.id = pf.transaction_id " +
			" where tx.type = 'TRANS_OUT_TO_WAREHOUSE' "
			+ " and tx.health_center_destination_id = ?1 and (pf.count- pf.used_count) > 0)  c")
	BigInteger countNotEmptyProductInSpecifiedWareHouse(Long location);
	@Query(nativeQuery = true, value="select count(c) from (select distinct(p.id) from " + 
			" product p  left join product_flow pf on p.id = pf.product_id " + 
			" left join  transaction tx on tx.id = pf.transaction_id " +
			" where tx.type = 'TRANS_OUT_TO_WAREHOUSE' "
			+ " and tx.health_center_destination_id = ?1 "
			+ " and DATE_PART('day', AGE(pf.expired_date, current_timestamp)) <  ?2 and (pf.count- pf.used_count) > 0)  c "
			)
	BigInteger countNotEmptyProductInSpecifiedWareHouseWithExpDaysBefore(Long locationId, Integer expiredDaysWithin);
	
	
	default BigInteger countNontEmptyProduct(boolean ignoreEmptyValue, boolean isMasterHealthCenter, 
			@Nullable Integer expiredDaysWithin, Long locationId) {
		BigInteger totalData;
		
		if (ignoreEmptyValue) {
			boolean withExpDateFilter = expiredDaysWithin!=null;
			if (isMasterHealthCenter) {
				if (withExpDateFilter) {
					totalData = countNotEmptyProductInMasterWareHouseWithExpDaysBefore(expiredDaysWithin+1);
				} else {
					totalData = countNotEmptyProductInMasterWareHouse();
				}
			} else { 
				if (withExpDateFilter) {
					totalData = countNotEmptyProductInSpecifiedWareHouseWithExpDaysBefore(locationId, expiredDaysWithin+1);
				} else {
					totalData = countNotEmptyProductInSpecifiedWareHouse(locationId);
				}
			}
		} else { 
			totalData = countAll();
		}
		return totalData;
	}
	
	
	
	default List<Product> getAvailableProducts(boolean ignoreEmptyValue, boolean isMasterHealthCenter, 
			@Nullable Integer expiredDaysWithin, Long locationId, Pageable pageable) {
		
		List<Product> products = new ArrayList<Product>();
		
		if (ignoreEmptyValue) {
			boolean withExpDateFilter = expiredDaysWithin!=null;
			Date expiredDateWithin = withExpDateFilter? DateUtil.plusDay(new Date(), expiredDaysWithin+1):null;
			if (isMasterHealthCenter) {
				if (withExpDateFilter) {
					products = findNotEmptyProductInMasterWarehouseWithExpDateBefore(expiredDateWithin , pageable);
				} else {
					products = findNotEmptyProductInMasterWarehouse(pageable);
				}
			} else {
				if (withExpDateFilter) {
					products = findNotEmptyProductInSpecifiedWarehouseWithExpDateBefore(locationId, expiredDateWithin, pageable);
				} else {
					products = findNotEmptyProductInSpecifiedWarehouse(locationId, pageable);
				}
			}
		} else {
			products = findByOrderByName(pageable);
		}
		return products ;
	}
	
	
}
