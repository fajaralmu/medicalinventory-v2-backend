package com.fajar.medicalinventory.service.inventory;

import static com.fajar.medicalinventory.util.DateUtil.clock00Midnight;
import static com.fajar.medicalinventory.util.DateUtil.clock24Midnight;

import java.math.BigInteger;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.util.DateUtil;

/**
 * Deprecated !
 * replaced with ProductStockRepositoryv2
 * @author Republic Of Gamers
 *
 */
@Service
@Deprecated
public class ProductStockWillExpiredRepository extends CommonRepository{
	
	
	
	////////////////Total Items //////////////////
	/**
	* TOTAL Items At Branch Warehouse
	*/
	 
	private BigInteger getTotalItemsAtBranchWarehouse(Long locationId) {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE' "
				+ " and tx.healthCenterDestination.id = ? "
				+ " and (pf.count-pf.usedCount) > 0";
		 
		org.hibernate.Query q = getQuery(queryString);
		q.setParameter(0, locationId);
		Object result = q.uniqueResult();
		return bigint(result);
	}
	 
	private BigInteger getTotalItemsAtBranchWarehouseAndExpDateBefore(Long locationId, Date expBefore) {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE' "
				+ " and tx.healthCenterDestination.id = ? "
				+ " and (pf.count-pf.usedCount) > 0 "
				+ " and pf.expiredDate < ?  ";
		org.hibernate.Query q = getQuery(queryString);
		q.setParameter(0, locationId);
		q.setParameter(1, expBefore);
		Object result = q.uniqueResult();
		return bigint(result);
	}
	private BigInteger getTotalItemsAtBranchWarehouseAndExpDateBeforeAfter(Long locationId, Date expBefore, Date expAfter) {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE' "
				+ " and tx.healthCenterDestination.id = ?  "
				+ " and (pf.count-pf.usedCount) > 0 "
				+ " and pf.expiredDate between ? and ?  ";
		org.hibernate.Query q = getQuery(queryString);
		q.setParameter(0, locationId);
		q.setParameter(1, expBefore);
		q.setParameter(2, expAfter);
		Object result = q.uniqueResult();
		return bigint(result);
	}


	public BigInteger getTotalItemsWillExpireAtBranchWarehouse(long locationId, Integer expDaysWithin) {
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


	private BigInteger getTotalItemsAllLocation() {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where (tx.type = 'TRANS_IN' or tx.type = 'TRANS_OUT_TO_WAREHOUSE') "
				+ " and (pf.count-pf.usedCount) > 0";
		org.hibernate.Query q = getQuery(queryString);
		return bigint(q.uniqueResult());
	}
	
	private BigInteger getTotalItemsAllLocationAndExpDateBeforeAndAfter(Date before, Date after) {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx  "
				+ " where (tx.type = 'TRANS_IN' or tx.type = 'TRANS_OUT_TO_WAREHOUSE') "
				+ " and pf.expiredDate between ? and ?  "
				+ " and (pf.count-pf.usedCount) > 0";
		org.hibernate.Query q = getQuery(queryString);
		q.setParameter(0, before);
		q.setParameter(1, after);
		Object result = q.uniqueResult();
		return bigint(result);
	}
 
	private BigInteger getTotalItemsAllLocationAndExpDateBefore(Date expiredDateWithin ) {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where (tx.type = 'TRANS_IN' or tx.type = 'TRANS_OUT_TO_WAREHOUSE') "
				+ " and pf.expiredDate < ? " 
				+ " and (pf.count-pf.usedCount) > 0";
		org.hibernate.Query q = getQuery(queryString);
		q.setParameter(0, expiredDateWithin);
		return bigint(q.uniqueResult());
	}
	public BigInteger getTotalItemsAllLocation(Integer expDaysWithin) {
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
	private BigInteger getTotalItemsAtMasterWarehouse() {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where tx.type = 'TRANS_IN' "
				+ " and (pf.count-pf.usedCount) > 0";
		org.hibernate.Query q = getQuery(queryString);
		return bigint(q.uniqueResult());
	}
	
	private BigInteger getTotalItemsAtMasterWarehouseAndExpDateBefore(Date expiredDateWithin ) {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where tx.type = 'TRANS_IN' "
				+ " and pf.expiredDate < ? " 
				+ " and (pf.count-pf.usedCount) > 0";
		org.hibernate.Query q = getQuery(queryString);
		q.setParameter(0, expiredDateWithin);
		return bigint(q.uniqueResult());
	}
	
	private BigInteger getTotalItemsAtMasterWarehouseAndExpDateBeforeAndAfter(Date before, Date after) {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where tx.type = 'TRANS_IN' "
				+ " and pf.expiredDate between ? and ?  "
				+ " and (pf.count-pf.usedCount) > 0";
		org.hibernate.Query q = getQuery(queryString);
		q.setParameter(0, before);
		q.setParameter(1, after);
		return bigint(q.uniqueResult());
		
	}
	public BigInteger getTotalItemsWillExpireAtMasterWarehouse(Integer expDaysWithin) {
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


}
