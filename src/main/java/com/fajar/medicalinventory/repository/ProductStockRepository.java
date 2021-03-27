package com.fajar.medicalinventory.repository;

import static com.fajar.medicalinventory.util.DateUtil.clock00Midnight;
import static com.fajar.medicalinventory.util.DateUtil.clock24Midnight;

import java.math.BigInteger;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.util.DateUtil;

@Service
public class ProductStockRepository {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.openSession();
	}
	private BigInteger bigint(Object result) {
		if (null == result) {
			return BigInteger.ZERO;
		}
		return BigInteger.valueOf(Long.valueOf(result.toString()));
	}
	
	////////////////Total Items //////////////////
	/**
	* TOTAL Items At Branch Warehouse
	*/
	 
	public BigInteger getTotalItemsAtBranchWarehouse(Long locationId) {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE' "
				+ " and tx.healthCenterDestination.id = ? "
				+ " and (pf.count-pf.usedCount) > 0";
		Session session = getSession();
		org.hibernate.Query q = session.createQuery(queryString);
		q.setParameter(0, locationId);
		Object result = q.uniqueResult();
		return bigint(result);
	}
	 
	public BigInteger getTotalItemsAtBranchWarehouseAndExpDateBefore(Long locationId, Date expBefore) {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE' "
				+ " and tx.healthCenterDestination.id = ? "
				+ " and (pf.count-pf.usedCount) > 0 "
				+ " and pf.expiredDate < ?  ";
		Session session = getSession();
		org.hibernate.Query q = session.createQuery(queryString);
		q.setParameter(0, locationId);
		q.setParameter(1, expBefore);
		Object result = q.uniqueResult();
		return bigint(result);
	}
	public BigInteger getTotalItemsAtBranchWarehouseAndExpDateBeforeAfter(Long locationId, Date expBefore, Date expAfter) {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE' "
				+ " and tx.healthCenterDestination.id = ?  "
				+ " and (pf.count-pf.usedCount) > 0 "
				+ " and pf.expiredDate between ? and ?  ";
		Session session = getSession();
		org.hibernate.Query q = session.createQuery(queryString);
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


	public BigInteger getTotalItemsAllLocation() {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where (tx.type = 'TRANS_IN' or tx.type = 'TRANS_OUT_TO_WAREHOUSE') "
				+ " and (pf.count-pf.usedCount) > 0";
		Session session = getSession();
		org.hibernate.Query q = session.createQuery(queryString);
		return bigint(q.uniqueResult());
	}
	
	public BigInteger getTotalItemsAllLocationAndExpDateBeforeAndAfter(Date before, Date after) {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx  "
				+ " where (tx.type = 'TRANS_IN' or tx.type = 'TRANS_OUT_TO_WAREHOUSE') "
				+ " and pf.expiredDate < ? and pf.expiredDate > ? "
				+ " and (pf.count-pf.usedCount) > 0";
		Session session = getSession();
		org.hibernate.Query q = session.createQuery(queryString);
		q.setParameter(0, before);
		q.setParameter(1, after);
		Object result = q.uniqueResult();
		return bigint(result);
	}
 
	public BigInteger getTotalItemsAllLocationAndExpDateBefore(Date expiredDateWithin ) {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where (tx.type = 'TRANS_IN' or tx.type = 'TRANS_OUT_TO_WAREHOUSE') "
				+ " and pf.expiredDate < ? " 
				+ " and (pf.count-pf.usedCount) > 0";
		Session session = getSession();
		org.hibernate.Query q = session.createQuery(queryString);
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
	public BigInteger getTotalItemsAtMasterWarehouse() {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where tx.type = 'TRANS_IN' "
				+ " and (pf.count-pf.usedCount) > 0";
		Session session = getSession();
		org.hibernate.Query q = session.createQuery(queryString);
		return bigint(q.uniqueResult());
	}
	
	public BigInteger getTotalItemsAtMasterWarehouseAndExpDateBefore(Date expiredDateWithin ) {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where tx.type = 'TRANS_IN' "
				+ " and pf.expiredDate < ? " 
				+ " and (pf.count-pf.usedCount) > 0";
		Session session = getSession();
		org.hibernate.Query q = session.createQuery(queryString);
		q.setParameter(0, expiredDateWithin);
		return bigint(q.uniqueResult());
	}
	
	public BigInteger getTotalItemsAtMasterWarehouseAndExpDateBeforeAndAfter(Date before, Date after) {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where tx.type = 'TRANS_IN' "
				+ " and pf.expiredDate < ? and pf.expiredDate > ? "
				+ " and (pf.count-pf.usedCount) > 0";
		Session session = getSession();
		org.hibernate.Query q = session.createQuery(queryString);
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
