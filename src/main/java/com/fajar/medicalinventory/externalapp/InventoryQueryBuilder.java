package com.fajar.medicalinventory.externalapp;

import static com.fajar.medicalinventory.util.DateUtil.getDate;

import java.math.BigInteger;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.BigIntegerType;
import org.hibernate.type.Type;
import org.springframework.lang.Nullable;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.entity.ProductFlow;

public class InventoryQueryBuilder {

	static long branch_id = 24L;
	static Session session;
	protected static BigInteger bigint(Object result) {
		if (null == result) {
			return BigInteger.ZERO;
		}
		return BigInteger.valueOf(Long.valueOf(result.toString()));
	}
	protected static Query getQuery(String queryString) {
		if (null == session) {
			session =  HibernateSessions.setSession();
		}
		org.hibernate.Query q = session.createQuery(queryString);
		return q;
	}
	public static void main(String[] args) {
		try {
			session = HibernateSessions.setSession();
			//branch location
//			getTotalItemsAtBranchWarehouse(24L);
//			getTotalItemsAtBranchWarehouseAndExpDateBefore(24L, getDate(2020, 0, 1));
//			getTotalItemsAtBranchWarehouseAndExpDateBeforeAfter(branch_id, getDate(2020, 0, 1), getDate(2021, 0, 1));
			//all location
//			getTotalItemsAllLocation();
			getTotalItemsAllLocationAndExpDateBeforeAndAfter(getDate(2019, 0, 1), getDate(2021, 0, 1));
//			getTotalItemsAllLocationAndExpDateBefore(getDate(2019, 0, 1));
			// master location
//			getTotalItemsAtMasterWarehouse();
//			getTotalItemsAtMasterWarehouseAndExpDateBefore(getDate(2019, 0, 1));
//			getTotalItemsAtMasterWarehouseAndExpDateBeforeAndAfter(getDate(2019, 0, 1), getDate(2021, 0, 1));
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		if (null != session) {
			session.close();
		}
		System.exit(0);
	}
	///////////////// MASTER LOCATION //////////////
	
	static BigInteger getTotalItemsAtMasterWarehouse() {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where tx.type = 'TRANS_IN' "
				+ " and (pf.count-pf.usedCount) > 0";
		org.hibernate.Query q = getQuery(queryString);
		Object res1 = q.uniqueResult();
		Criteria criteria = commonGetStockCriteria(null, TransactionType.TRANS_IN);
		printResult(res1, criteria.uniqueResult());
		
		return bigint(res1);
	}
	
	static BigInteger getTotalItemsAtMasterWarehouseAndExpDateBefore(Date before ) {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where tx.type = 'TRANS_IN' "
				+ " and pf.expiredDate < ? " 
				+ " and (pf.count-pf.usedCount) > 0";
		org.hibernate.Query q = getQuery(queryString);
		q.setParameter(0, before);
		Object res1 = q.uniqueResult();
		Criteria criteria = commonGetStockCriteria(null, TransactionType.TRANS_IN);
		criteria.add(Restrictions.lt("expiredDate", before));
		printResult(res1, criteria.uniqueResult());
		
		return bigint(res1);
	}
	
	static BigInteger getTotalItemsAtMasterWarehouseAndExpDateBeforeAndAfter(Date before, Date after) {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where tx.type = 'TRANS_IN' "
				+ " and pf.expiredDate between ? and ?  "
				+ " and (pf.count-pf.usedCount) > 0";
		org.hibernate.Query q = getQuery(queryString);
		q.setParameter(0, before);
		q.setParameter(1, after);
		Object res1 = q.uniqueResult();
		Criteria criteria = commonGetStockCriteria(null, TransactionType.TRANS_IN);
		criteria.add(Restrictions.between("expiredDate", before, after));
		printResult(res1, criteria.uniqueResult());
		
		return bigint(res1);
		
	}
	
	///////////////// ALL LOCATION /////////////////
	private static BigInteger getTotalItemsAllLocation() {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where (tx.type = 'TRANS_IN' or tx.type = 'TRANS_OUT_TO_WAREHOUSE') "
				+ " and (pf.count-pf.usedCount) > 0";
		org.hibernate.Query q = getQuery(queryString);
		Object res1 = q.uniqueResult();
		Criteria criteria = commonGetStockCriteria(null, TransactionType.TRANS_IN, TransactionType.TRANS_OUT_TO_WAREHOUSE);
		
		printResult(res1, criteria.uniqueResult());
		
		return bigint(res1);
	}
	
	private static BigInteger getTotalItemsAllLocationAndExpDateBeforeAndAfter(Date before, Date after) {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx  "
				+ " where (tx.type = 'TRANS_IN' or tx.type = 'TRANS_OUT_TO_WAREHOUSE') "
//				+ " and pf.expiredDate < ? and pf.expiredDate > ? "
				+ " and pf.expiredDate between ? and ? "
				+ " and (pf.count-pf.usedCount) > 0";
		org.hibernate.Query q = getQuery(queryString);
		q.setParameter(0, before);
		q.setParameter(1, after);
		Object res1 = q.uniqueResult();
		Criteria criteria = commonGetStockCriteria(null, TransactionType.TRANS_IN, TransactionType.TRANS_OUT_TO_WAREHOUSE);
		criteria.add(Restrictions.between("expiredDate", before, after));
		printResult(res1, criteria.uniqueResult());
		
		return bigint(res1);
	}
 
	private static BigInteger getTotalItemsAllLocationAndExpDateBefore(Date before ) {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where (tx.type = 'TRANS_IN' or tx.type = 'TRANS_OUT_TO_WAREHOUSE') "
				+ " and pf.expiredDate < ? " 
				+ " and (pf.count-pf.usedCount) > 0";
		org.hibernate.Query q = getQuery(queryString);
		q.setParameter(0, before);
		Object res1 = q.uniqueResult();
		Criteria criteria = commonGetStockCriteria(null, TransactionType.TRANS_IN, TransactionType.TRANS_OUT_TO_WAREHOUSE);
		criteria.add(Restrictions.lt("expiredDate", before));
		printResult(res1, criteria.uniqueResult());
		
		return bigint(res1);
	}
	
	
	///////////////// BRANCH //////////////////

	private static BigInteger getTotalItemsAtBranchWarehouseAndExpDateBefore(Long locationId, Date expBefore) {
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
		
		Criteria criteria = commonGetStockCriteria(locationId, TransactionType.TRANS_OUT_TO_WAREHOUSE);
		criteria.add(Restrictions.lt("expiredDate", expBefore));
		Object result2 = criteria.uniqueResult();
		
		printResult(result, result2);
		
		return bigint(result);
	}
	
	static void printResult(Object res1, Object res2) {
		System.out.println("RESULT1: "+res1+" RESULT2: "+res2);
	}
	
	private static BigInteger getTotalItemsAtBranchWarehouseAndExpDateBeforeAfter(Long locationId, Date expBefore, Date expAfter) {
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
		Criteria criteria = commonGetStockCriteria(locationId, TransactionType.TRANS_OUT_TO_WAREHOUSE);
		criteria.add(Restrictions.between("expiredDate", expBefore, expAfter));
		Object result2 = criteria.uniqueResult();
		
		printResult(result, result2);
		
		return bigint(result);
	}
	
	static Criteria commonGetStockCriteria(@Nullable Long locationId, TransactionType ... transactionTypes) {
		Criteria criteria = commonGetStockCriteria(locationId);
		if (transactionTypes != null && transactionTypes.length > 0) {
			Criterion[] typeRestrictions = new Criterion[transactionTypes.length];
			for (int i = 0; i < typeRestrictions.length; i++) {
				typeRestrictions[i] = Restrictions.eq("transaction.type", transactionTypes[i]);
			} 
			criteria.add(Restrictions.or(typeRestrictions
			));
		}
		return criteria;
	}
	static Criteria commonGetStockCriteria(Long locationId) {
		Criteria criteria = session.createCriteria(ProductFlow.class);
		criteria.createAlias("transaction", "transaction");
		if (null != locationId) {
			criteria.add(Restrictions.eq("transaction.healthCenterDestination.id", locationId));
		}
		criteria.add(Restrictions.sqlRestriction("(count - used_count) > 0"));
		Type[] type = new Type[] {
				new BigIntegerType()
		};
		criteria.setProjection(Projections.sqlProjection("sum(count - used_count) as stock", new String[] { "stock" }, type));
		
		return criteria;
	}
	
	private static void getTotalItemsAtBranchWarehouse(Long locationId) {
		final String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx " + " where tx.type = 'TRANS_OUT_TO_WAREHOUSE' "
				+ " and tx.healthCenterDestination.id = ? " + " and (pf.count-pf.usedCount) > 0";
		org.hibernate.Query q = session.createQuery(queryString);
		q.setParameter(0, locationId);
		Object result1 = q.uniqueResult();
		Criteria criteria = commonGetStockCriteria(locationId, TransactionType.TRANS_OUT_TO_WAREHOUSE); 
		
		Object result = criteria.uniqueResult();
		System.out.println("RESULT1: " +result1+ " RESULT2: " + result);
	}
}
