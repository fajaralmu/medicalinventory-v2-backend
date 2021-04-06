package com.fajar.medicalinventory.service.inventory;

import static com.fajar.medicalinventory.constants.TransactionType.TRANS_OUT_TO_WAREHOUSE;
import static com.fajar.medicalinventory.util.DateUtil.clock00Midnight;
import static com.fajar.medicalinventory.util.DateUtil.clock24Midnight;

import java.math.BigInteger;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.util.DateUtil;

@Service
public class ProductStockRepositoryv2 extends CommonRepository {

	private static final String FIELD_EXP_DATE = "expiredDate";
	
	////////////////// BRANCH WAREHOUSE ///////////////////
	
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
	
	public BigInteger getTotalItemsAtBranchWarehouse(long locationId) {
		
		Criteria criteria = commonGetStockCriteria(locationId, TRANS_OUT_TO_WAREHOUSE);
		
		return bigintResult(criteria);
	}

	public BigInteger getTotalItemsAtBranchWarehouseAndExpDateBefore(long locationId, Date expBefore) {
		
		Criteria criteria = commonGetStockCriteria(locationId, TRANS_OUT_TO_WAREHOUSE);
		criteria.add(Restrictions.lt(FIELD_EXP_DATE, expBefore));
		
		return bigintResult(criteria);
	}
	public BigInteger getTotalItemsAtBranchWarehouseAndExpDateBeforeAfter(Long locationId, Date expBefore, Date expAfter) {
		
		Criteria criteria = commonGetStockCriteria(locationId, TRANS_OUT_TO_WAREHOUSE);
		criteria.add(Restrictions.between(FIELD_EXP_DATE, expBefore, expAfter));
		
		return bigintResult(criteria);
	}
	
	//////////////////ALL WAREHOUSE ///////////////////
	
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
	private Criteria getTotalItemsAllLocationCriteria() {
		return commonGetStockCriteria(null, TransactionType.TRANS_IN, TransactionType.TRANS_OUT_TO_WAREHOUSE);
	}
	private BigInteger getTotalItemsAllLocation() {
		
		Criteria criteria = getTotalItemsAllLocationCriteria();
		return bigintResult(criteria);
	}
	private BigInteger getTotalItemsAllLocationAndExpDateBeforeAndAfter(Date before, Date after) {
		
		Criteria criteria = getTotalItemsAllLocationCriteria();
		criteria.add(Restrictions.between(FIELD_EXP_DATE, before, after));
		return bigintResult(criteria);
	}
	private BigInteger getTotalItemsAllLocationAndExpDateBefore(Date before ) {
		
		Criteria criteria = getTotalItemsAllLocationCriteria();
		criteria.add(Restrictions.lt(FIELD_EXP_DATE, before));
		return bigintResult(criteria);
	}
	
	
	////////////////// MASTER WAREHOUSE ///////////////////
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
	private BigInteger getTotalItemsAtMasterWarehouse() {
		 
		Criteria criteria = commonGetStockCriteria(null, TransactionType.TRANS_IN);
		return bigintResult(criteria);
	}
	
	private BigInteger getTotalItemsAtMasterWarehouseAndExpDateBefore(Date before ) {
		
		Criteria criteria = commonGetStockCriteria(null, TransactionType.TRANS_IN);
		criteria.add(Restrictions.lt(FIELD_EXP_DATE, before));
		return bigintResult(criteria);
	}
	
	private BigInteger getTotalItemsAtMasterWarehouseAndExpDateBeforeAndAfter(Date before, Date after) {
		
		Criteria criteria = commonGetStockCriteria(null, TransactionType.TRANS_IN);
		criteria.add(Restrictions.between(FIELD_EXP_DATE, before, after));
		return bigintResult(criteria);
		
	}
}
