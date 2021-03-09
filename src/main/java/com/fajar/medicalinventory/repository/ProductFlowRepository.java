package com.fajar.medicalinventory.repository;

import static com.fajar.medicalinventory.util.DateUtil.clock00Midnight;
import static com.fajar.medicalinventory.util.DateUtil.clock24Midnight;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.entity.Transaction;
import com.fajar.medicalinventory.util.DateUtil;

@Repository
public interface ProductFlowRepository extends JpaRepository<ProductFlow, Long> {

	public List<ProductFlow> findByTransaction(Transaction transaction);

	public List<ProductFlow> findByProductAndTransaction_type(Product p, TransactionType transIn);

	//LIST ALL LOCATION
	@Query("select pf from ProductFlow pf left join pf.transaction tx "
			+ " left join pf.product p "
			+ " where (tx.type='TRANS_IN' or tx.type='TRANS_OUT_TO_WAREHOUSE') "
			+ " and p.id = ?1 and (pf.count- pf.usedCount) > 0 "
			+ " and pf.expiredDate < ?2") 
	List<ProductFlow> findAvailableStocksAllLocationWithExpDateBefore(Long productId, Date expDate);
	@Query("select pf from ProductFlow pf left join pf.transaction tx "
			+ " left join pf.product p "
			+ " where (tx.type='TRANS_IN' or tx.type='TRANS_OUT_TO_WAREHOUSE') "
			+ " and p.id = ?1 and (pf.count- pf.usedCount) > 0 "
			+ " and pf.expiredDate < ?2  and pf.expiredDate > ?3") 
	public List<ProductFlow> findAvailableStocksAllLocationWithExpDateBeforeAndAfter(Long productId,
			Date expiredDateWithin, Date tomorrow);

	@Query("select pf from ProductFlow pf left join pf.transaction tx "
			+ " left join pf.product p "
			+ " where (tx.type='TRANS_IN' or tx.type='TRANS_OUT_TO_WAREHOUSE') "
			+ " and p.id = ?1 and (pf.count- pf.usedCount) > 0 ") 
	public List<ProductFlow> findAvailableStocksAllLocation(Long productId);
	default List<ProductFlow> findAvailableStocksAllLocation(Long productId, Integer expDaysWithin) {
		
		if (null != expDaysWithin) {
			Date expiredDateWithin = DateUtil.plusDay(new Date(), expDaysWithin+1);
			if ( expDaysWithin > 0) {
				Date tomorrow = DateUtil.plusDay(new Date(), 1 );
				
				return findAvailableStocksAllLocationWithExpDateBeforeAndAfter(productId, clock24Midnight(expiredDateWithin), clock00Midnight(tomorrow));
			}
			return findAvailableStocksAllLocationWithExpDateBefore(productId, clock24Midnight(expiredDateWithin));
		}
		
		return findAvailableStocksAllLocation(productId);
	}
	//LIST MAIN WAREHOUSE
	@Query("select pf from ProductFlow pf left join pf.transaction tx "
			+ " left join pf.product p "
			+ " where tx.type='TRANS_IN' and p.id = ?1 and (pf.count- pf.usedCount) > 0 ") 
	public List<ProductFlow> findAvailabeProductsAtMainWareHouse(Long productId);
	@Query("select pf from ProductFlow pf left join pf.transaction tx "
			+ " left join pf.product p "
			+ " where tx.type='TRANS_IN' "
			+ " and p.id = ?1 and (pf.count- pf.usedCount) > 0 "
			+ " and pf.expiredDate < ?2") 
	public List<ProductFlow> findAvailabeProductsAtMainWareHouseWithExpDateBefore(Long productId, Date expDate);
	@Query("select pf from ProductFlow pf left join pf.transaction tx "
			+ " left join pf.product p "
			+ " where tx.type='TRANS_IN' "
			+ " and p.id = ?1 and (pf.count- pf.usedCount) > 0 "
			+ " and pf.expiredDate < ?2 and pf.expiredDate > ?3") 
	public List<ProductFlow> findAvailabeProductsAtMainWareHouseWithExpDateBeforeAfter(Long productId, Date expDateBefore, Date expDateAfter);
	
	default List<ProductFlow> findAvailableStocksAtMainWareHouse(Long productId, @Nullable Integer expDaysWithin){
		if (null != expDaysWithin) {
			Date expiredDateWithin = DateUtil.plusDay(new Date(), expDaysWithin+1); 
			if ( expDaysWithin > 0) {
				Date tomorrow = DateUtil.plusDay(new Date(), 1 );
				return findAvailabeProductsAtMainWareHouseWithExpDateBeforeAfter(productId, clock24Midnight(expiredDateWithin), clock00Midnight(tomorrow));
			}
			return findAvailabeProductsAtMainWareHouseWithExpDateBefore(productId, clock24Midnight(expiredDateWithin));
		}
		
		return findAvailabeProductsAtMainWareHouse(productId);
	}
	
//	@Query("select pf from ProductFlow pf left join pf.transaction tx "
//			+ " left join pf.product p "
//			+ " where tx.type='TRANS_IN' and tx.transactionDate <= ?2 and p.id = ?1 and (pf.count- pf.usedCount) > 0 ") 
//	List<ProductFlow> findAvailableProductsAtMainWareHouseAtDate(Long productId, Date date);

	//LIST BRANCH WAREHOUSE
	@Query( "select pf from ProductFlow pf left join pf.transaction tx  "
			+ " left join pf.product p "
			+ " left join tx.healthCenterDestination location "
			+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE'   "
			+ " and location.id = ?1 and p.id = ?2 and "
			+ " (pf.count- pf.usedCount)  > 0 ")
	public List<ProductFlow> findAvailableStocksAtBranchWareHouse(Long locationId, Long productId);
	@Query( "select pf from ProductFlow pf left join pf.transaction tx  "
			+ " left join pf.product p "
			+ " left join tx.healthCenterDestination location "
			+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE'   "
			+ " and location.id = ?1 and p.id = ?2 "
			+ " and pf.expiredDate < ?3"
			+ " and (pf.count- pf.usedCount)  > 0 ")
	public List<ProductFlow> findAvailableStocksAtBranchWareHouseWithExpDateBefore(Long locationId, Long productId  , Date expDate);
	@Query( "select pf from ProductFlow pf left join pf.transaction tx  "
			+ " left join pf.product p "
			+ " left join tx.healthCenterDestination location "
			+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE'   "
			+ " and location.id = ?1 and p.id = ?2 "
			+ " and pf.expiredDate < ?3 and pf.expiredDate > ?4"
			+ " and (pf.count- pf.usedCount)  > 0 ")
	public List<ProductFlow> findAvailableStocksAtBranchWareHouseWithExpDateBeforeAfter(Long locationId, Long productId  , Date expDateBefore, Date expDateAfter);
 
	
	default List<ProductFlow> findAvailableStocksAtBranchWareHouse(Long locationId, Long productId, @Nullable Integer expDaysWithin) {
		if (null != expDaysWithin) {
			Date expiredDateWithin = DateUtil.plusDay(new Date(), expDaysWithin+1);
			if ( expDaysWithin > 0) {
				Date tomorrow = DateUtil.plusDay(new Date(), 1 );
				return findAvailableStocksAtBranchWareHouseWithExpDateBeforeAfter(locationId, productId, clock24Midnight(expiredDateWithin), clock00Midnight(tomorrow));
			}
			return findAvailableStocksAtBranchWareHouseWithExpDateBefore(locationId, productId, clock24Midnight(expiredDateWithin));
		}
		return findAvailableStocksAtBranchWareHouse(locationId, productId);
	}
	
	////////////////////////////////////////////END AVAILABLE LIST//////////////////////
	
	@Query("select pf.price from ProductFlow pf " + 
			"left join pf.transaction tx " + 
			"left join pf.product p " + 
			"where tx.transactionDate <= ?2 and tx.type = 'TRANS_IN' " + 
			"and p.id = ?1 order by tx.transactionDate desc")
	public List<Long> getProductPriceAtDate(Long id, Date d, Pageable pageable);

	public List<ProductFlow> findByTransactionIn(List<Transaction> transactions);

	@Query("select sum(pf.count)  from ProductFlow pf "  
			+ "left join  pf.transaction tx "
			+ "left join pf.product p " + 
			"where tx.type = 'TRANS_IN' and p.id=?1 and tx.transactionDate <= ?2")
	public BigInteger getTotalIncomingProductFromSupplier(long productId, Date date);  
	@Query("select   sum(pf.count)  from ProductFlow pf "  
			+ "left join  pf.transaction tx "
			+ "left join pf.product p "
			+ "left join tx.healthCenterDestination destination " + 
			"where tx.type = 'TRANS_OUT_TO_WAREHOUSE' and p.id=?1 and tx.transactionDate <= ?2 and destination.id = ?3")
	public BigInteger getTotalIncomingProductAtBranchWarehouse(long productId, Date date, long locationId);
	
	@Query("select sum(pf.count)  from ProductFlow pf "  
			+ "left join  pf.transaction tx "
			+ "left join pf.product p "
			+ "left join tx.healthCenterLocation location " + 
			"where tx.type = 'TRANS_OUT' and p.id=?1 and tx.transactionDate <= ?2 and location.id = ?3")
	public BigInteger getTotalUsedProductToCustomer(Long productId, Date date, Long locationId);
	
	//////////////// Total Items //////////////////
	/**
	 * TOTAL Items At Branch Warehouse
	 */
	@Query("select sum(pf.count-pf.usedCount) from ProductFlow pf "
			+ " left join pf.transaction tx "
			+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE' "
			+ " and tx.healthCenterDestination.id = ?1 "
			+ " and (pf.count-pf.usedCount) > 0")
	public BigInteger getTotalItemsAtBranchWarehouse(Long locationId);
	@Query("select sum(pf.count-pf.usedCount) from ProductFlow pf "
			+ " left join pf.transaction tx "
			+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE' "
			+ " and tx.healthCenterDestination.id = ?1 "
			+ " and (pf.count-pf.usedCount) > 0 "
			+ " and pf.expiredDate < ?2  ")
	public BigInteger getTotalItemsAtBranchWarehouseAndExpDateBefore(Long locationId, Date expBefore);
	@Query("select sum(pf.count-pf.usedCount) from ProductFlow pf "
			+ " left join pf.transaction tx "
			+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE' "
			+ " and tx.healthCenterDestination.id = ?1 "
			+ " and (pf.count-pf.usedCount) > 0 "
			+ " and pf.expiredDate between ?2 " 
			+ " and ?3 ")
	public BigInteger getTotalItemsAtBranchWarehouseAndExpDateBeforeAfter(Long locationId, Date expBefore, Date expAfter);
	default BigInteger getTotalItemsAtBranchWarehouse(long locationId, Integer expDaysWithin) {
		if (null != expDaysWithin) {
			Date expiredDateWithin = DateUtil.plusDay(new Date(), expDaysWithin+1);
			if (expDaysWithin > 0) {
				Date tomorrow = DateUtil.plusDay(new Date(), 1 );
				return getTotalItemsAtBranchWarehouseAndExpDateBeforeAfter(locationId, clock24Midnight(expiredDateWithin), clock00Midnight(tomorrow));
			}
			
			return getTotalItemsAtBranchWarehouseAndExpDateBefore(locationId, clock24Midnight(expiredDateWithin));
		}
		return getTotalItemsAtBranchWarehouse(locationId);
	}
	
	/**
	 * TOTAL Items All Location
	 */
	@Query("select sum(pf.count-pf.usedCount) from ProductFlow pf "
			+ " left join pf.transaction tx "
			+ " where (tx.type = 'TRANS_IN' or tx.type = 'TRANS_OUT_TO_WAREHOUSE') "
//			+ " and tx.healthCenterDestination.id = ?1 "
			+ " and (pf.count-pf.usedCount) > 0")
	public BigInteger getTotalItemsAllLocation();
	@Query("select sum(pf.count-pf.usedCount) from ProductFlow pf "
			+ " left join pf.transaction tx  "
			+ " where (tx.type = 'TRANS_IN' or tx.type = 'TRANS_OUT_TO_WAREHOUSE') "
			+ " and pf.expiredDate < ?1"
			+ " and pf.expiredDate > ?2"
			+ " and (pf.count-pf.usedCount) > 0")
	BigInteger getTotalItemsAllLocationAndExpDateBeforeAndAfter(Date before, Date after);
	@Query("select sum(pf.count-pf.usedCount) from ProductFlow pf "
			+ " left join pf.transaction tx "
			+ " where (tx.type = 'TRANS_IN' or tx.type = 'TRANS_OUT_TO_WAREHOUSE') "
			+ " and pf.expiredDate < ?1" 
			+ " and (pf.count-pf.usedCount) > 0")
	public BigInteger getTotalItemsAllLocationAndExpDateBefore(Date expiredDateWithin );
	default BigInteger getTotalItemsAllLocation(Integer expDaysWithin) {
		if (null != expDaysWithin) {
			Date expiredDateWithin = DateUtil.plusDay(new Date(), expDaysWithin+1);
			if ( expDaysWithin > 0) {
				Date tomorrow = DateUtil.plusDay(new Date(), 1 );
				return getTotalItemsAllLocationAndExpDateBeforeAndAfter( expiredDateWithin,tomorrow);
			}
			return getTotalItemsAllLocationAndExpDateBefore(expiredDateWithin);
			
		}
		return getTotalItemsAllLocation();
	}
	/**
	 * TOTAL Items At Main Warehouse
	 */
	@Query("select sum(pf.count-pf.usedCount) from ProductFlow pf "
			+ " left join pf.transaction tx "
			+ " where tx.type = 'TRANS_IN' "
//			+ " and tx.healthCenterDestination.id = ?1 "
			+ " and (pf.count-pf.usedCount) > 0")
	public BigInteger getTotalItemsAtMasterWarehouse();
	@Query("select sum(pf.count-pf.usedCount) from ProductFlow pf "
			+ " left join pf.transaction tx "
			+ " where tx.type = 'TRANS_IN' "
			+ " and pf.expiredDate < ?1" 
			+ " and (pf.count-pf.usedCount) > 0")
	public BigInteger getTotalItemsAtMasterWarehouseAndExpDateBefore(Date expiredDateWithin );
	@Query("select sum(pf.count-pf.usedCount) from ProductFlow pf "
			+ " left join pf.transaction tx "
			+ " where tx.type = 'TRANS_IN' "
			+ " and pf.expiredDate < ?1"
			+ " and pf.expiredDate > ?2"
			+ " and (pf.count-pf.usedCount) > 0")
	public BigInteger getTotalItemsAtMasterWarehouseAndExpDateBeforeAndAfter(Date before, Date after);
	default BigInteger getTotalItemsAtMasterWarehouse(Integer expDaysWithin) {
		if (null != expDaysWithin) {
			Date expiredDateWithin = DateUtil.plusDay(new Date(), expDaysWithin+1);
			if ( expDaysWithin > 0) {
				Date tomorrow = DateUtil.plusDay(new Date(), 1 );
				return getTotalItemsAtMasterWarehouseAndExpDateBeforeAndAfter( expiredDateWithin,tomorrow);
			}
			return getTotalItemsAtMasterWarehouseAndExpDateBefore(expiredDateWithin);
			
		}
		return getTotalItemsAtMasterWarehouse();
	}
	

	/**
	 * get specified product
	 * @param productCode 
	 * @param type
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Query("select pf from ProductFlow pf left join pf.transaction tx left join pf.product p "
			+ " where p.code = ?1 and tx.type = ?2 and tx.transactionDate between ?3 and ?4 order by tx.transactionDate")
	public List<ProductFlow> getByTransactionTypeAndDateBetween(String productCode, TransactionType type, Date startDate, Date endDate);
	
	/**
	 * get all product
	 * @param type
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Query("select pf from ProductFlow pf left join pf.transaction tx left join pf.product p "
			+ " where pf.product in ?4 and tx.type = ?1 and tx.transactionDate between ?2 and ?3 order by tx.transactionDate")
	public List<ProductFlow> getByTransactionTypeAndDateBetweenAndProducts( TransactionType type, Date startDate, Date endDate, List<Product> products);

	
	@Query("select sum(pf.count) from ProductFlow pf left join pf.transaction tx where tx.type = ?1 and pf.product.id = ?2")
	public BigInteger getSumOfProductFlowByTransactionType(TransactionType type, Long productId);

	

	
}