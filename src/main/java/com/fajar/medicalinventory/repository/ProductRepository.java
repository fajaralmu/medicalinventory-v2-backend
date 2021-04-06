package com.fajar.medicalinventory.repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.dto.Filter;
import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.Unit;
import com.fajar.medicalinventory.util.DateUtil;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByUnit(Unit unit);

	List<Product> findByName(String name);

	List<Product> findByUtilityTool(boolean isUtility);

	List<Product> findByOrderByName(Pageable of);
	//LIST ALL LOCATION
	@Query("select distinct(p) from  ProductFlow pf left join pf.product p left join pf.transaction tx "
			+ " where (tx.type = 'TRANS_IN' OR tx.type = 'TRANS_OUT_TO_WAREHOUSE') and (pf.count- pf.usedCount) > 0 order by p.name")
	List<Product> findNotEmptyProductAllLocation(Pageable of);
	@Query("select distinct(p) from  ProductFlow pf  left join pf.product p left join pf.transaction tx "
			+ " where  (tx.type = 'TRANS_IN' OR tx.type = 'TRANS_OUT_TO_WAREHOUSE') and (pf.count- pf.usedCount) > 0  and pf.expiredDate <  ?1 "
			+ " order by p.name")
	List<Product> findNotEmptyProductAllLocationWithExpDateBefore(Date expiredDateWithin, Pageable pageable);
	@Query("select distinct(p) from  ProductFlow pf  left join pf.product p left join pf.transaction tx "
			+ " where  (tx.type = 'TRANS_IN' OR tx.type = 'TRANS_OUT_TO_WAREHOUSE') and (pf.count- pf.usedCount) > 0 "
			+ " and pf.expiredDate <  ?1 "
			+ " and pf.expiredDate > ?2 "
			+ " order by p.name")
	List<Product> findNotEmptyProductAllLocationWithExpDateBeforeAfter(Date expiredDateBefore, Date expDateAfter, Pageable pageable);
	// LIST AT Master warehouse
	@Query("select distinct(p) from  ProductFlow pf left join pf.product p left join pf.transaction tx "
			+ " where tx.type = 'TRANS_IN' and (pf.count- pf.usedCount) > 0 order by p.name")
	List<Product> findNotEmptyProductInMasterWarehouse(Pageable of);

	@Query("select distinct(p) from  ProductFlow pf  left join pf.product p left join pf.transaction tx "
			+ " where tx.type = 'TRANS_IN' and (pf.count- pf.usedCount) > 0  and pf.expiredDate <  ?1 "
			+ " order by p.name")
	List<Product> findNotEmptyProductInMasterWarehouseWithExpDateBefore(Date expDateBefore, Pageable pageable);
	@Query("select distinct(p) from  ProductFlow pf  left join pf.product p left join pf.transaction tx "
			+ " where tx.type = 'TRANS_IN' and (pf.count- pf.usedCount) > 0  and pf.expiredDate <  ?1 and pf.expiredDate > ?2 "
			+ " order by p.name")
	List<Product> findNotEmptyProductInMasterWarehouseWithExpDateBeforeAfter(Date expDateBefore, Date expDateAfter, Pageable pageable);

	// LIST AT Branch warehouse
	@Query("select distinct(p) from  ProductFlow pf  left join pf.product p "
			+ " left join pf.transaction tx left join tx.healthCenterDestination location "
			+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE' and location.id = ?1 and (pf.count- pf.usedCount) > 0 order by p.name")
	List<Product> findNotEmptyProductInSpecifiedWarehouse(Long location, Pageable of);

	@Query("select distinct(p) from  ProductFlow pf  left join pf.product p "
			+ " left join pf.transaction tx left join tx.healthCenterDestination location "
			+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE' and location.id = ?1 and (pf.count- pf.usedCount) > 0 "
			+ " and pf.expiredDate <  ?2 order by p.name")
	List<Product> findNotEmptyProductInSpecifiedWarehouseWithExpDateBefore(Long location, Date expDateBefore, Pageable of);
	@Query("select distinct(p) from  ProductFlow pf  left join pf.product p "
			+ " left join pf.transaction tx left join tx.healthCenterDestination location "
			+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE' and location.id = ?1 and (pf.count- pf.usedCount) > 0 "
			+ " and pf.expiredDate <  ?2 and pf.expiredDate > ?3  order by p.name")
	List<Product> findNotEmptyProductInSpecifiedWarehouseWithExpDateBeforeAfter(Long location, Date expDateBefore, Date expDateAfter, Pageable of);

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
	 
	

	default List<Product> getAvailableProducts(boolean isMasterHealthCenter, Filter filter, Long locationId) {
		final boolean ignoreEmptyValue = filter.isIgnoreEmptyValue();
		final Integer expDaysWithin = filter.isFilterExpDate() ? filter.getDay() : null;
		final PageRequest pageable = filter.getPageRequest();
		List<Product> products = new ArrayList<Product>();

		if (ignoreEmptyValue) {
			boolean withExpDateFilter = expDaysWithin != null;
			Date expiredDateWithin = withExpDateFilter ? DateUtil.plusDay(new Date(), expDaysWithin  ) : null;
			if (isMasterHealthCenter) {
				products = findNotEmptyProductAtMasterWarehouse(expiredDateWithin, pageable);
			} else {
				products = findNotEmptyProductAtBranchWarehouse(expiredDateWithin, pageable, locationId);
			}
		} else {
			products = findByOrderByName(pageable);
		}
		return products;
	}

	default List<Product> findNotEmptyProductAtBranchWarehouse(@Nullable Date expiredDateWithin, PageRequest pageable,
			Long locationId) {

		if (expiredDateWithin != null) {
			boolean afterToday = DateUtil.afterToday(expiredDateWithin);
			if (afterToday) {
				findNotEmptyProductInSpecifiedWarehouseWithExpDateBeforeAfter(locationId, expiredDateWithin, new Date(), pageable);
			}
			return findNotEmptyProductInSpecifiedWarehouseWithExpDateBefore(locationId, expiredDateWithin, pageable);
		} else {
			return findNotEmptyProductInSpecifiedWarehouse(locationId, pageable);
		}
	}

	default List<Product> findNotEmptyProductAtMasterWarehouse(@Nullable Date expDateBefore, PageRequest pageable) {
		if (expDateBefore != null) {
			boolean afterToday = DateUtil.afterToday(expDateBefore);
			if (afterToday) {
				return findNotEmptyProductInMasterWarehouseWithExpDateBeforeAfter(expDateBefore, new Date(), pageable);
			}
			return findNotEmptyProductInMasterWarehouseWithExpDateBefore(expDateBefore, pageable);
		} else {
			return findNotEmptyProductInMasterWarehouse(pageable);
		}
	}
	default List<Product> findNotEmptyProductAtAllLocation(@Nullable Date expiredDateWithin, PageRequest pageable) {
		if (expiredDateWithin != null) {
			boolean afterToday = DateUtil.afterToday(expiredDateWithin);
			if (afterToday) {
				return findNotEmptyProductAllLocationWithExpDateBeforeAfter(expiredDateWithin, new Date(), pageable);
			}
			return findNotEmptyProductAllLocationWithExpDateBefore(expiredDateWithin, pageable);
		} else {
			return findNotEmptyProductAllLocation(pageable);
		}
	}

	default List<Product> getAvailableProductsAllLocation(Filter filter) {
		List<Product> products = new ArrayList<>();
		final boolean ignoreEmptyValue = filter.isIgnoreEmptyValue();
		final Integer expDaysWithin = filter.isFilterExpDate() ? filter.getDay() : null;
		final PageRequest pageable = filter.getPageRequest();

		if (ignoreEmptyValue) {
			boolean withExpDateFilter = expDaysWithin != null;
			Date expiredDateWithin = withExpDateFilter ? DateUtil.plusDay(new Date(), expDaysWithin  ) : null;
			products = findNotEmptyProductAtAllLocation(expiredDateWithin, pageable);
			 
		} else {
			products = findByOrderByName(pageable);
		}
		return products ;
	}

	

}
