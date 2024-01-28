package com.pkm.medicalinventory.repository.readonly;

import static com.pkm.medicalinventory.util.DateUtil.clock00Midnight;
import static com.pkm.medicalinventory.util.DateUtil.clock24Midnight;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pkm.medicalinventory.constants.TransactionType;
import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.entity.ProductFlow;
import com.pkm.medicalinventory.entity.Transaction;
import com.pkm.medicalinventory.util.DateUtil;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductFlowRepository extends JpaRepository<ProductFlow, Long> {

	default List<ProductFlow> empty() {
		return new ArrayList<>();
	}

	public List<ProductFlow> findByTransaction(Transaction transaction);

	public List<ProductFlow> findByProductAndTransaction_type(Product p, TransactionType transIn);

	// LIST ALL LOCATION
	@Query("select pf from ProductFlow pf left join pf.transaction tx left join pf.product p "
			+ " where (tx.type='TRANS_IN' or tx.type='TRANS_OUT_TO_WAREHOUSE') "
			+ " and p.id = ?1 and (pf.count- pf.usedCount) > 0 " + " and pf.expiredDate < ?2 order by pf.expiredDate")
	public List<ProductFlow> findAvailableStocksAllLocationWithExpDateBefore(Long productId, Date expDate);

	@Query("select pf from ProductFlow pf left join pf.transaction tx  left join pf.product p "
			+ " where (tx.type='TRANS_IN' or tx.type='TRANS_OUT_TO_WAREHOUSE') "
			+ " and p.id = ?1 and (pf.count- pf.usedCount) > 0 "
			+ " and pf.expiredDate < ?2  and pf.expiredDate > ?3 order by pf.expiredDate")
	public List<ProductFlow> findAvailableStocksAllLocationWithExpDateBeforeAndAfter(Long productId,
			Date expiredDateWithin, Date tomorrow);

	@Query("select pf from ProductFlow pf left join pf.transaction tx left join pf.product p "
			+ " where (tx.type='TRANS_IN' or tx.type='TRANS_OUT_TO_WAREHOUSE') "
			+ " and p.id = ?1 and (pf.count- pf.usedCount) > 0 order by pf.expiredDate")
	public List<ProductFlow> findAvailableStocksAllLocation(Long productId);

	// --multiple products
	@Query("select pf from ProductFlow pf left join pf.transaction tx  left join pf.product p "
			+ " where (tx.type='TRANS_IN' or tx.type='TRANS_OUT_TO_WAREHOUSE') "
			+ " and p in ?1 and (pf.count- pf.usedCount) > 0 " + " and pf.expiredDate < ?2 order by pf.expiredDate")
	public List<ProductFlow> findAvailableStocksAllLocationWithExpDateBefore(List<Product> products, Date expDate);

	@Query("select pf from ProductFlow pf left join pf.transaction tx  left join pf.product p "
			+ " where (tx.type='TRANS_IN' or tx.type='TRANS_OUT_TO_WAREHOUSE') "
			+ " and p in ?1 and (pf.count- pf.usedCount) > 0 "
			+ " and pf.expiredDate < ?2  and pf.expiredDate > ?3 order by pf.expiredDate")
	public List<ProductFlow> findAvailableStocksAllLocationWithExpDateBeforeAndAfter(List<Product> products,
			Date expiredDateWithin, Date tomorrow);

	@Query("select pf from ProductFlow pf left join pf.transaction tx left join pf.product p "
			+ " where (tx.type='TRANS_IN' or tx.type='TRANS_OUT_TO_WAREHOUSE') "
			+ " and p in ?1 and (pf.count- pf.usedCount) > 0 order by pf.expiredDate")
	public List<ProductFlow> findAvailableStocksAllLocation(List<Product> products);

	default List<ProductFlow> findAvailableStocksAllLocation(Long productId, Integer expDaysWithin) {

		if (null != expDaysWithin) {
			Date expiredDateWithin = DateUtil.plusDay(new Date(), expDaysWithin + 1);
			if (expDaysWithin > 0) {
				Date tomorrow = DateUtil.plusDay(new Date(), 1);

				return findAvailableStocksAllLocationWithExpDateBeforeAndAfter(productId,
						clock24Midnight(expiredDateWithin), clock00Midnight(tomorrow));
			}
			return findAvailableStocksAllLocationWithExpDateBefore(productId, clock24Midnight(expiredDateWithin));
		}

		return findAvailableStocksAllLocation(productId);
	}

	default List<ProductFlow> findAvailableStocksAllLocation(List<Product> products, Integer expDaysWithin) {
		if (null == products || products.size() == 0)
			return empty();
		if (null != expDaysWithin) {
			Date expiredDateWithin = DateUtil.plusDay(new Date(), expDaysWithin + 1);
			if (expDaysWithin > 0) {
				Date tomorrow = DateUtil.plusDay(new Date(), 1);

				return findAvailableStocksAllLocationWithExpDateBeforeAndAfter(products,
						clock24Midnight(expiredDateWithin), clock00Midnight(tomorrow));
			}
			return findAvailableStocksAllLocationWithExpDateBefore(products, clock24Midnight(expiredDateWithin));
		}

		return findAvailableStocksAllLocation(products);
	}

	// LIST MAIN WAREHOUSE
	@Query("select pf from ProductFlow pf left join pf.transaction tx " + " left join pf.product p "
			+ " where tx.type='TRANS_IN' and p.id = ?1 and (pf.count- pf.usedCount) > 0 ")
	public List<ProductFlow> findAvailabeProductsAtMainWareHouse(Long productId);

	@Query("select pf from ProductFlow pf left join pf.transaction tx " + " left join pf.product p "
			+ " where tx.type='TRANS_IN' " + " and p.id = ?1 and (pf.count- pf.usedCount) > 0 "
			+ " and pf.expiredDate < ?2 order by pf.expiredDate")
	public List<ProductFlow> findAvailabeProductsAtMainWareHouseWithExpDateBefore(Long productId, Date expDate);

	@Query("select pf from ProductFlow pf left join pf.transaction tx " + " left join pf.product p "
			+ " where tx.type='TRANS_IN' " + " and p.id = ?1 and (pf.count- pf.usedCount) > 0 "
			+ " and pf.expiredDate < ?2 and pf.expiredDate > ?3 order by pf.expiredDate")
	public List<ProductFlow> findAvailabeProductsAtMainWareHouseWithExpDateBeforeAfter(Long productId,
			Date expDateBefore, Date expDateAfter);

	// --multiple products
	@Query("select pf from ProductFlow pf left join pf.transaction tx " + " left join pf.product p "
			+ " where tx.type='TRANS_IN' and p in ?1 and (pf.count- pf.usedCount) > 0 ")
	public List<ProductFlow> findAvailabeProductsAtMainWareHouse(List<Product> products);

	@Query("select pf from ProductFlow pf left join pf.transaction tx " + " left join pf.product p "
			+ " where tx.type='TRANS_IN' " + " and p in ?1 and (pf.count- pf.usedCount) > 0 "
			+ " and pf.expiredDate < ?2 order by pf.expiredDate")
	public List<ProductFlow> findAvailabeProductsAtMainWareHouseWithExpDateBefore(List<Product> products, Date expDate);

	@Query("select pf from ProductFlow pf left join pf.transaction tx " + " left join pf.product p "
			+ " where tx.type='TRANS_IN' " + " and p in ?1 and (pf.count- pf.usedCount) > 0 "
			+ " and pf.expiredDate < ?2 and pf.expiredDate > ?3 order by pf.expiredDate")
	public List<ProductFlow> findAvailabeProductsAtMainWareHouseWithExpDateBeforeAfter(List<Product> products,
			Date expDateBefore, Date expDateAfter);

	default List<ProductFlow> findAvailableStocksAtMainWareHouse(Long productId, @Nullable Integer expDaysWithin) {
		if (null != expDaysWithin) {
			Date expiredDateWithin = DateUtil.plusDay(new Date(), expDaysWithin + 1);
			if (expDaysWithin > 0) {
				Date tomorrow = DateUtil.plusDay(new Date(), 1);
				return findAvailabeProductsAtMainWareHouseWithExpDateBeforeAfter(productId,
						clock24Midnight(expiredDateWithin), clock00Midnight(tomorrow));
			}
			return findAvailabeProductsAtMainWareHouseWithExpDateBefore(productId, clock24Midnight(expiredDateWithin));
		}

		return findAvailabeProductsAtMainWareHouse(productId);
	}

	default List<ProductFlow> findAvailableStocksAtMainWareHouse(List<Product> products,
			@Nullable Integer expDaysWithin) {
		if (null == products || products.size() == 0)
			return empty();
		if (null != expDaysWithin) {
			Date expiredDateWithin = DateUtil.plusDay(new Date(), expDaysWithin + 1);
			if (expDaysWithin > 0) {
				Date tomorrow = DateUtil.plusDay(new Date(), 1);
				return findAvailabeProductsAtMainWareHouseWithExpDateBeforeAfter(products,
						clock24Midnight(expiredDateWithin), clock00Midnight(tomorrow));
			}
			return findAvailabeProductsAtMainWareHouseWithExpDateBefore(products, clock24Midnight(expiredDateWithin));
		}

		return findAvailabeProductsAtMainWareHouse(products);
	}

//	@Query("select pf from ProductFlow pf left join pf.transaction tx "
//			+ " left join pf.product p "
//			+ " where tx.type='TRANS_IN' and tx.transactionDate <= ?2 and p.id = ?1 and (pf.count- pf.usedCount) > 0 ") 
//	List<ProductFlow> findAvailableProductsAtMainWareHouseAtDate(Long productId, Date date);

	// LIST BRANCH WAREHOUSE
	@Query("select pf from ProductFlow pf left join pf.transaction tx  " + " left join pf.product p "
			+ " left join tx.healthCenterDestination location " + " where tx.type = 'TRANS_OUT_TO_WAREHOUSE'   "
			+ " and location.id = ?1 and p.id = ?2 and " + " (pf.count- pf.usedCount)  > 0 ")
	public List<ProductFlow> findAvailableStocksAtBranchWareHouse(Long locationId, Long productId);

	@Query("select pf from ProductFlow pf left join pf.transaction tx  " + " left join pf.product p "
			+ " left join tx.healthCenterDestination location " + " where tx.type = 'TRANS_OUT_TO_WAREHOUSE'   "
			+ " and location.id = ?1 and p.id = ?2 " + " and pf.expiredDate < ?3"
			+ " and (pf.count- pf.usedCount)  > 0 order by pf.expiredDate")
	public List<ProductFlow> findAvailableStocksAtBranchWareHouseWithExpDateBefore(Long locationId, Long productId,
			Date expDate);

	@Query("select pf from ProductFlow pf left join pf.transaction tx  " + " left join pf.product p "
			+ " left join tx.healthCenterDestination location " + " where tx.type = 'TRANS_OUT_TO_WAREHOUSE'   "
			+ " and location.id = ?1 and p.id = ?2 " + " and pf.expiredDate < ?3 and pf.expiredDate > ?4"
			+ " and (pf.count- pf.usedCount)  > 0 order by pf.expiredDate")
	public List<ProductFlow> findAvailableStocksAtBranchWareHouseWithExpDateBeforeAfter(Long locationId, Long productId,
			Date expDateBefore, Date expDateAfter);

	// -multiple products
	@Query("select pf from ProductFlow pf left join pf.transaction tx  " + " left join pf.product p "
			+ " left join tx.healthCenterDestination location " + " where tx.type = 'TRANS_OUT_TO_WAREHOUSE'   "
			+ " and location.id = ?1 and p in ?2 and " + " (pf.count- pf.usedCount)  > 0 ")
	public List<ProductFlow> findAvailableStocksAtBranchWareHouse(Long locationId, List<Product> products);

	@Query("select pf from ProductFlow pf left join pf.transaction tx  " + " left join pf.product p "
			+ " left join tx.healthCenterDestination location " + " where tx.type = 'TRANS_OUT_TO_WAREHOUSE'   "
			+ " and location.id = ?1 and p in ?2 " + " and pf.expiredDate < ?3"
			+ " and (pf.count- pf.usedCount)  > 0 order by pf.expiredDate")
	public List<ProductFlow> findAvailableStocksAtBranchWareHouseWithExpDateBefore(Long locationId,
			List<Product> products, Date expDate);

	@Query("select pf from ProductFlow pf left join pf.transaction tx  " + " left join pf.product p "
			+ " left join tx.healthCenterDestination location " + " where tx.type = 'TRANS_OUT_TO_WAREHOUSE'   "
			+ " and location.id = ?1 and p in ?2 " + " and pf.expiredDate < ?3 and pf.expiredDate > ?4"
			+ " and (pf.count- pf.usedCount)  > 0 order by pf.expiredDate")
	public List<ProductFlow> findAvailableStocksAtBranchWareHouseWithExpDateBeforeAfter(Long locationId,
			List<Product> products, Date expDateBefore, Date expDateAfter);

	default List<ProductFlow> findAvailableStocksAtBranchWareHouse(Long locationId, Long productId,
			@Nullable Integer expDaysWithin) {
		if (null != expDaysWithin) {
			Date expiredDateWithin = DateUtil.plusDay(new Date(), expDaysWithin + 1);
			if (expDaysWithin > 0) {
				Date tomorrow = DateUtil.plusDay(new Date(), 1);
				return findAvailableStocksAtBranchWareHouseWithExpDateBeforeAfter(locationId, productId,
						clock24Midnight(expiredDateWithin), clock00Midnight(tomorrow));
			}
			return findAvailableStocksAtBranchWareHouseWithExpDateBefore(locationId, productId,
					clock24Midnight(expiredDateWithin));
		}
		return findAvailableStocksAtBranchWareHouse(locationId, productId);
	}

	default List<ProductFlow> findAvailableStocksAtBranchWareHouse(Long locationId, List<Product> products,
			@Nullable Integer expDaysWithin) {
		if (null == products || products.size() == 0)
			return empty();
		if (null != expDaysWithin) {
			Date expiredDateWithin = DateUtil.plusDay(new Date(), expDaysWithin + 1);
			if (expDaysWithin > 0) {
				Date tomorrow = DateUtil.plusDay(new Date(), 1);
				return findAvailableStocksAtBranchWareHouseWithExpDateBeforeAfter(locationId, products,
						clock24Midnight(expiredDateWithin), clock00Midnight(tomorrow));
			}
			return findAvailableStocksAtBranchWareHouseWithExpDateBefore(locationId, products,
					clock24Midnight(expiredDateWithin));
		}
		return findAvailableStocksAtBranchWareHouse(locationId, products);
	}

	//////////////////////////////////////////// END AVAILABLE
	//////////////////////////////////////////// LIST//////////////////////

	@Query("select pf.price from ProductFlow pf " + "left join pf.transaction tx " + "left join pf.product p "
			+ "where tx.transactionDate <= ?2 and tx.type = 'TRANS_IN' "
			+ "and p.id = ?1 order by tx.transactionDate desc")
	public List<Long> getProductPriceAtDate(Long id, Date d, Pageable pageable);

	public List<ProductFlow> findByTransactionIn(List<Transaction> transactions);

	////////// INCOMING PRODUCT COUNT //////////
	// FROM Supplier
	@Query("select sum(pf.count)  from ProductFlow pf " + "left join  pf.transaction tx " + "left join pf.product p "
			+ "where tx.type = 'TRANS_IN' and p.id=?1 and tx.transactionDate <= ?2")
	public BigInteger getTotalIncomingProductFromSupplier(long productId, Date date);

	@Query("select sum(pf.count)  from ProductFlow pf " + "left join  pf.transaction tx " + "left join pf.product p "
			+ "where tx.type = 'TRANS_IN' and p.id=?1 and tx.transactionDate >= ?2 and tx.transactionDate <= ?3")
	public BigInteger getTotalIncomingProductFromSupplierBetweenDate(long productId, Date date1, Date date2);

	// multiple
	@Query("select p.id, sum(pf.count) from ProductFlow pf " + "left join  pf.transaction tx "
			+ "left join pf.product p "
			+ "where tx.type = 'TRANS_IN' and p in ?1 and tx.transactionDate <= ?2 group by p.id")
	public List<Object[]> getTotalIncomingProductsFromSupplier(List<Product> products, Date date);

	@Query("select p.id, sum(pf.count) from ProductFlow pf " + "left join  pf.transaction tx "
			+ "left join pf.product p "
			+ "where tx.type = 'TRANS_IN' and p in ?1 and tx.transactionDate >= ?2 and tx.transactionDate <= ?3 group by p.id")
	public List<Object[]> getTotalIncomingProductsFromSupplierBetweenDate(List<Product> products, Date date1,
			Date date2);

	@Query("select pf from ProductFlow pf " + "left join  pf.transaction tx " + "left join pf.product p "
			+ "where tx.type = 'TRANS_IN' and p in ?1 and tx.transactionDate >= ?2 and tx.transactionDate <= ?3 ")
	public List<ProductFlow> getIncomingProductsFromSupplierBetweenDate(List<Product> products, Date date1, Date date2);

	// FROM Main Warehouse
	@Query("select   sum(pf.count)  from ProductFlow pf " + "left join  pf.transaction tx " + "left join pf.product p "
			+ "left join tx.healthCenterDestination destination "
			+ "where tx.type = 'TRANS_OUT_TO_WAREHOUSE' and p.id=?1 and tx.transactionDate <= ?2 and destination.id = ?3")
	public BigInteger getTotalIncomingProductAtBranchWarehouse(long productId, Date date, long locationId);

	@Query("select   sum(pf.count)  from ProductFlow pf " + "left join  pf.transaction tx " + "left join pf.product p "
			+ "left join tx.healthCenterDestination destination "
			+ "where tx.type = 'TRANS_OUT_TO_WAREHOUSE' and p.id=?1 and tx.transactionDate <= ?2 and tx.transactionDate <= ?3 and destination.id = ?4")
	public BigInteger getTotalIncomingProductAtBranchWarehouseBetweenDate(long productId, Date date1, Date date2,
			long locationId);

	// multiple
	@Query("select  p.id, sum(pf.count) from ProductFlow pf " + "left join  pf.transaction tx "
			+ "left join pf.product p " + "left join tx.healthCenterDestination destination "
			+ "where tx.type = 'TRANS_OUT_TO_WAREHOUSE' and p in ?1 and tx.transactionDate <= ?2 and destination.id = ?3 group by p.id")
	public List<Object[]> getTotalIncomingProductsAtBranchWarehouse(List<Product> products, Date date, long locationId);

	@Query("select  p.id, sum(pf.count) from ProductFlow pf " + "left join  pf.transaction tx "
			+ "left join pf.product p " + "left join tx.healthCenterDestination destination "
			+ "where tx.type = 'TRANS_OUT_TO_WAREHOUSE' and p in ?1 and tx.transactionDate <= ?2 and tx.transactionDate <= ?3 and destination.id = ?4 group by p.id")
	public List<Object[]> getTotalIncomingProductsAtBranchWarehouseBetweenDate(List<Product> products, Date date1,
			Date date2, long locationId);

	@Query("select  pf from ProductFlow pf " + "left join  pf.transaction tx " + "left join pf.product p "
			+ "left join tx.healthCenterDestination destination "
			+ "where tx.type = 'TRANS_OUT_TO_WAREHOUSE' and p in ?1 and tx.transactionDate <= ?2 and tx.transactionDate <= ?3 and destination.id = ?4 ")
	public List<ProductFlow> getIncomingProductsAtBranchWarehouseBetweenDate(List<Product> products, Date date1,
			Date date2, long locationId);

	/////// USED PRODUCT COUNT //////
	// to Customer
	@Query("select sum(pf.count)  from ProductFlow pf " + "left join  pf.transaction tx " + "left join pf.product p "
			+ "left join tx.healthCenterLocation location "
			+ "where tx.type = 'TRANS_OUT' and p.id=?1 and tx.transactionDate <= ?2 and location.id = ?3")
	public BigInteger getTotalUsedProductToCustomerAtDate(Long productId, Date date, Long locationId);

	@Query("select sum(pf.count)  from ProductFlow pf " + "left join  pf.transaction tx " + "left join pf.product p "
			+ "left join tx.healthCenterLocation location "
			+ "where tx.type = 'TRANS_OUT' and p.id=?1 and tx.transactionDate >= ?1 and tx.transactionDate <= ?2 and location.id = ?4")
	public BigInteger getTotalUsedProductToCustomerBetweenDate(Long productId, Date date1, Date date2, Long locationId);

	// multiple
	@Query("select p.id, sum(pf.count) from ProductFlow pf " + "left join  pf.transaction tx "
			+ "left join pf.product p " + "left join tx.healthCenterLocation location "
			+ "where tx.type = 'TRANS_OUT' and p in ?1 and tx.transactionDate <= ?2 and location.id = ?3 group by p.id")
	public List<Object[]> getTotalUsedProductsToCustomerAtDate(List<Product> products, Date date, Long locationId);

	@Query("select p.id, sum(pf.count) from ProductFlow pf " + "left join  pf.transaction tx "
			+ "left join pf.product p " + "left join tx.healthCenterLocation location "
			+ "where tx.type = 'TRANS_OUT' and p in ?1 and tx.transactionDate >= ?1 and tx.transactionDate <= ?2 and location.id = ?4 group by p.id")
	public List<Object[]> getTotalUsedProductsToCustomerBetweenDate(List<Product> products, Date date1, Date date2,
			Long locationId);

	@Query("select pf from ProductFlow pf " + "left join  pf.transaction tx " + "left join pf.product p "
			+ "left join tx.healthCenterLocation location "
			+ "where tx.type = 'TRANS_OUT' and p in ?1 and tx.transactionDate >= ?1 and tx.transactionDate <= ?2 and location.id = ?4 group by p.id")
	public List<ProductFlow> getUsedProductsToCustomerBetweenDate(List<Product> products, Date date1, Date date2,
			Long locationId);

	// to Customer or Warehouse
	@Query("select sum(pf.count)  from ProductFlow pf " + "left join  pf.transaction tx " + "left join pf.product p "
			+ "left join tx.healthCenterLocation location "
			+ "where (tx.type = 'TRANS_OUT' or tx.type='TRANS_OUT_TO_WAREHOUSE') and p.id=?1 and tx.transactionDate >= ?2 and tx.transactionDate <= ?3 and location.id = ?4")
	public BigInteger getTotalUsedProductToCustomerOrBranchWarehouseBetweenDate(Long productId, Date date, Date date2,
			Long locationId);

	@Query("select sum(pf.count)  from ProductFlow pf " + "left join  pf.transaction tx " + "left join pf.product p "
			+ "left join tx.healthCenterLocation location "
			+ "where (tx.type = 'TRANS_OUT' or tx.type='TRANS_OUT_TO_WAREHOUSE') and p.id=?1 and tx.transactionDate <= ?2 and location.id = ?3")
	public BigInteger getTotalUsedProductToCustomerOrBranchWarehouseAtDate(Long productId, Date date, Long locationId);

	// multiple
	@Query("select p.id, sum(pf.count)  from ProductFlow pf " + "left join  pf.transaction tx "
			+ "left join pf.product p " + "left join tx.healthCenterLocation location "
			+ "where (tx.type = 'TRANS_OUT' or tx.type='TRANS_OUT_TO_WAREHOUSE') and p in ?1 and tx.transactionDate >= ?2 and tx.transactionDate <= ?3 and location.id = ?4 "
			+ "group by p.id")
	public List<Object[]> getTotalUsedProductsToCustomerOrBranchWarehouseBetweenDate(List<Product> products, Date date,
			Date date2, Long locationId);

	@Query("select p.id, sum(pf.count)  from ProductFlow pf " + "left join  pf.transaction tx "
			+ "left join pf.product p " + "left join tx.healthCenterLocation location "
			+ "where (tx.type = 'TRANS_OUT' or tx.type='TRANS_OUT_TO_WAREHOUSE') and p in ?1 and tx.transactionDate <= ?2 and location.id = ?3 "
			+ "group by p.id")
	public List<Object[]> getTotalUsedProductsToCustomerOrBranchWarehouseAtDate(List<Product> products, Date date,
			Long locationId);

	@Query("select pf from ProductFlow pf " + "left join  pf.transaction tx " + "left join pf.product p "
			+ "left join tx.healthCenterLocation location "
			+ "where (tx.type = 'TRANS_OUT' or tx.type='TRANS_OUT_TO_WAREHOUSE') and p in ?1 and tx.transactionDate >= ?2 and tx.transactionDate <= ?3 and location.id = ?4 "
			+ " ")
	public List<ProductFlow> getUsedProductsToCustomerOrBranchWarehouseBetweenDate(List<Product> products, Date date,
			Date date2, Long locationId);

	/**
	 * get specified product
	 * 
	 * @param productCode
	 * @param type
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Query("select pf from ProductFlow pf left join pf.transaction tx left join pf.product p "
			+ " where p.code = ?1 and tx.type = ?2 and tx.transactionDate between ?3 and ?4 order by tx.transactionDate")
	public List<ProductFlow> getByTransactionTypeAndDateBetween(String productCode, TransactionType type,
			Date startDate, Date endDate);

	/**
	 * get all product
	 * 
	 * @param type
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Query("select pf from ProductFlow pf left join pf.transaction tx left join pf.product p "
			+ " where pf.product in ?4 and tx.type = ?1 and tx.transactionDate between ?2 and ?3 order by tx.transactionDate")
	public List<ProductFlow> getByTransactionTypeAndDateBetweenAndProducts(TransactionType type, Date startDate,
			Date endDate, List<Product> products);

	@Query("select sum(pf.count) from ProductFlow pf left join pf.transaction tx where tx.type = ?1 and pf.product.id = ?2")
	public BigInteger getSumOfProductFlowByTransactionType(TransactionType type, Long productId);

	public List<ProductFlow> findByReferenceProductFlowIn(List<ProductFlow> productFlows);

}