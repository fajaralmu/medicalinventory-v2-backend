package com.pkm.medicalinventory.externalapp;

import java.math.BigInteger;
import java.util.List;

import com.pkm.medicalinventory.constants.TransactionType;
import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.entity.ProductFlow;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

public class ProductAvailablityCountQueryBuilder {

	static long branch_id = 24L;
	static Session session;

	protected static BigInteger bigint(Object result) {
		if (null == result) {
			return BigInteger.ZERO;
		}
		try {
			return BigInteger.valueOf(Long.valueOf(result.toString()));
		} catch (Exception e) {
			System.out.println("ERROR PARSING NUMBER: " + result);
			e.printStackTrace();
			return BigInteger.ZERO;
		}
	}

	protected static Query getQuery(String queryString) {
		if (null == session) {
			session = HibernateSessions.setSession();
		}
		org.hibernate.Query q = session.createQuery(queryString);
		return q;
	}

	protected static Query getNativeQuery(String queryString) {
		if (null == session) {
			session = HibernateSessions.setSession();
		}
		org.hibernate.Query q = session.createSQLQuery(queryString);
		return q;
	}

	static Criteria commonGetStockCriteria(Long locationId) {
		Criteria criteria = session.createCriteria(ProductFlow.class);
		criteria.createAlias("transaction", "transaction");
		if (null != locationId) {
			criteria.add(Restrictions.eq("transaction.healthCenterDestination.id", locationId));
		}
		criteria.add(Restrictions.sqlRestriction("(this_.count - this_.used_count) > ?", 0, IntegerType.INSTANCE));
//		Type[] type = new Type[] { new BigIntegerType() };
//		criteria.setProjection(
//				Projections.sqlProjection("sum(count - used_count) as stock", new String[] { "stock" }, type));

		return criteria;
	}
	static Criteria commonGetStockCriteria( ) {
		return commonGetStockCriteria(null);
	}

	public static void main(String[] args) {
		try {
			session = HibernateSessions.setSession();
			//master
//			countNotEmptyProductInMasterWareHouseWithExpDaysBeforeAfter(1, -1000);
//			countNotEmptyProductInMasterWareHouse();
			
			//branch
//			countNotEmptyProductInSpecifiedWareHouse(24L);
//			countNotEmptyProductInSpecifiedWareHouseWithExpDaysBeforeAfter(24L, 1, -733);
			
			//all
//			countNotEmptyProductAllLocationWithExpDaysBeforeAfter(1, -733);
			countNotEmptyProductAllLocation();
		} catch (Exception e) {
			e.printStackTrace();

		}
		if (null != session) {
			session.close();
		}
		System.exit(0);
	}

	///////////// master ////////////////
	
	static BigInteger countNotEmptyProductInMasterWareHouseWithExpDaysBeforeAfter(Integer expDayDiffBefore,
			Integer expDayDiffAfter) {
		String value = " select distinct(p.id) from  product p "
				+ " left join product_flow pf on pf.product_id =  p.id "
				+ " left join transaction tx on pf.transaction_id = tx.id where tx.type = 'TRANS_IN' "
				+ " and extract(day from pf.expired_date - current_timestamp) <  " + expDayDiffBefore
				+ " and extract(day from pf.expired_date - current_timestamp) >   " + expDayDiffAfter
				+ " and  (pf.count- pf.used_count) > 0 order by p.id   ";

		Query q = getNativeQuery(value);
		List list1 = q.list();
		System.out.println(list1); 
		Criteria criteria = commonGetStockCriteria();

		criteria.add(Restrictions.eq("transaction.type", TransactionType.TRANS_IN));
		criteria.add(Restrictions.sqlRestriction("DATE_PART('day', AGE(expired_date, current_timestamp)) < ?",
				expDayDiffBefore, IntegerType.INSTANCE));
		criteria.add(Restrictions.sqlRestriction("DATE_PART('day', AGE(expired_date, current_timestamp)) > ?",
				expDayDiffAfter, IntegerType.INSTANCE));
		criteria.add(Property.forName("product").in(productDetachedCriteria()));
		criteria.setProjection(Projections.distinct(Projections.property("product.id")));
		criteria.addOrder(Order.asc("product.id"));
		List result = criteria.list();
		System.out.println(result);
		BigInteger count = BigInteger.valueOf(result.size());
		System.out.println(count);
		return count;
	}
	
	 
	static BigInteger countNotEmptyProductInMasterWareHouse() {
		String query = "select distinct(p.id) from  product p "
				+ " left join product_flow pf on pf.product_id =  p.id "
				+ " left join transaction tx on pf.transaction_id = tx.id"
				+ " where tx.type = 'TRANS_IN' and (pf.count- pf.used_count) > 0 order by p.id";
		Query q = getNativeQuery(query);
		List list1 = q.list();
		System.out.println(list1);
		
		Criteria criteria = commonGetStockCriteria();

		criteria.add(Restrictions.eq("transaction.type", TransactionType.TRANS_IN));
		criteria.add(Property.forName("product").in(productDetachedCriteria()));
		criteria.setProjection(Projections.distinct(Projections.property("product.id")));
		criteria.addOrder(Order.asc("product.id"));
		List result = criteria.list();
		System.out.println(result);
		BigInteger count = BigInteger.valueOf(result.size());
		System.out.println(count);
		return count;
	}

	
	//////// branch ////////////
	
	static BigInteger countNotEmptyProductInSpecifiedWareHouse(Long location) {
		String query = "select distinct(p.id) from "
				+ " product p  left join product_flow pf on p.id = pf.product_id "
				+ " left join  transaction tx on tx.id = pf.transaction_id  where tx.type = 'TRANS_OUT_TO_WAREHOUSE' "
				+ " and tx.health_center_destination_id = ? and (pf.count- pf.used_count) > 0 order by p.id";
		Query q = getNativeQuery(query);
		q.setParameter(0, location);
		List list1 = q.list();
		System.out.println(list1);
		
		Criteria criteria = commonGetStockCriteria(location);

		criteria.add(Restrictions.eq("transaction.type", TransactionType.TRANS_OUT_TO_WAREHOUSE));
		criteria.add(Property.forName("product").in(productDetachedCriteria()));
		criteria.setProjection(Projections.distinct(Projections.property("product.id")));
		criteria.addOrder(Order.asc("product.id"));
		List result = criteria.list();
		System.out.println(result);
		BigInteger count = BigInteger.valueOf(result.size());
		System.out.println(count);
		return count;
		
	} 
	
	static BigInteger countNotEmptyProductInSpecifiedWareHouseWithExpDaysBeforeAfter(Long locationId, Integer expiredDaysDiffBefore, Integer expDayDiffAfter) {
		String query = "select distinct(p.id) from "
				+ " product p  left join product_flow pf on p.id = pf.product_id "
				+ " left join  transaction tx on tx.id = pf.transaction_id  where tx.type = 'TRANS_OUT_TO_WAREHOUSE' "
				+ " and tx.health_center_destination_id = ?  "
				+ " and extract(day from pf.expired_date - current_timestamp) between ? and ? "
				+ " and (pf.count- pf.used_count) > 0";
		Query q = getNativeQuery(query);
		q.setParameter(0, locationId);
		q.setParameter(2, expiredDaysDiffBefore);
		q.setParameter(1, expDayDiffAfter);
		List list1 = q.list();
		System.out.println(list1);
		
		Criteria criteria = commonGetStockCriteria(locationId);
		criteria.add(Restrictions.sqlRestriction(" extract(day from this_.expired_date - current_timestamp) between ? and ?",
				new Object[]{expDayDiffAfter, expiredDaysDiffBefore}, new Type[] {IntegerType.INSTANCE, IntegerType.INSTANCE}));
		criteria.add(Restrictions.eq("transaction.type", TransactionType.TRANS_OUT_TO_WAREHOUSE));
		criteria.add(Property.forName("product").in(productDetachedCriteria()));
		criteria.setProjection(Projections.distinct(Projections.property("product.id")));
//		criteria.setProjection(null);
		criteria.addOrder(Order.asc("product.id"));
		List  result = criteria.list();
		
		System.out.println(result);
		BigInteger count = BigInteger.valueOf(result.size());
		System.out.println(result);
		return count;
	}
	
	/////////////// all location ///////////////
	 
	static BigInteger countNotEmptyProductAllLocationWithExpDaysBeforeAfter(Integer expDayDiffBefore, Integer expDayDiffAfter) {
		String query =  " select distinct(p.id) from  product p "
				+ " left join product_flow pf on pf.product_id =  p.id "
				+ " left join transaction tx on pf.transaction_id = tx.id where "
				+ " (tx.type = 'TRANS_IN' or tx.type = 'TRANS_OUT_TO_WAREHOUSE') "
				+ " and extract(day from pf.expired_date - current_timestamp) < ? "
				+ " and extract(day from pf.expired_date - current_timestamp) > ? "
				+ " and  (pf.count- pf.used_count) > 0 order by p.id ";
		Query q = getNativeQuery(query); 
		q.setParameter(0, expDayDiffBefore);
		q.setParameter(1, expDayDiffAfter);
		List list1 = q.list();
		System.out.println(list1);
		
		Criteria criteria = commonGetStockCriteria(null);
		criteria.add(Restrictions.sqlRestriction(" extract(day from this_.expired_date - current_timestamp) between ? and ?",
				new Object[]{expDayDiffAfter, expDayDiffBefore}, new Type[] {IntegerType.INSTANCE, IntegerType.INSTANCE}));
		criteria.add(Restrictions.or(Restrictions.eq("transaction.type", TransactionType.TRANS_OUT_TO_WAREHOUSE), 
				Restrictions.eq("transaction.type", TransactionType.TRANS_IN)));
		criteria.add(Property.forName("product").in(productDetachedCriteria()));
		criteria.setProjection(Projections.distinct(Projections.property("product.id")));
//		criteria.setProjection(null);
		criteria.addOrder(Order.asc("product.id"));
		List  result = criteria.list();
		
		BigInteger count = BigInteger.valueOf(result.size());
		System.out.println(result);
		return count;
	}
	
	static BigInteger countNotEmptyProductAllLocation() {
		String query = "select distinct(p.id) from  product p "
				+ " left join product_flow pf on pf.product_id =  p.id "
				+ " left join transaction tx on pf.transaction_id = tx.id"
				+ " where (tx.type = 'TRANS_IN' or tx.type = 'TRANS_OUT_TO_WAREHOUSE')  and (pf.count- pf.used_count) > 0 and p.name ilike '%ab%' "
				+ " order by p.id";
		Query q = getNativeQuery(query); 
		List list1 = q.list();
		System.out.println(list1);
		
		Criteria criteria = commonGetStockCriteria(null);
		criteria.add(Restrictions.or(Restrictions.eq("transaction.type", TransactionType.TRANS_OUT_TO_WAREHOUSE), 
				Restrictions.eq("transaction.type", TransactionType.TRANS_IN)));
		criteria.add(Property.forName("product").in(productDetachedCriteria()));
		criteria.createAlias("product", "product");
		criteria.setProjection(Projections.distinct(Projections.property("product.id")));
		
		criteria.add(Restrictions.ilike("product.name", "%ab%"));
//		criteria.setProjection(null);
		criteria.addOrder(Order.asc("product.id"));
		List  result = criteria.list();
		
		BigInteger count = BigInteger.valueOf(result.size());
		System.out.println(result);
		return count;
	}
	
	static DetachedCriteria productDetachedCriteria() {
		DetachedCriteria ownerCriteria = DetachedCriteria.forClass(Product.class);
		ownerCriteria.setProjection(Property.forName("id"));
		return ownerCriteria;
	}


}

/**
 * 
 * 
 * DetachedCriteria ownerCriteria = DetachedCriteria.forClass(Product.class);
 * ownerCriteria.setProjection(Property.forName("id"));
 * ownerCriteria.add(Restrictions.eq("name", "ABC")); // Criteria criteria =
 * getSession().createCriteria(Pet.class); //
 * criteria.add(Property.forName("ownerId").in(ownerCriteria)); Criteria
 * criteria = session.createCriteria(ProductFlow.class);
 * 
 * criteria.add(Property.forName("product").in(ownerCriteria));
 * criteria.createAlias("transaction", "transaction");
 * criteria.add(Restrictions.eq("transaction.type", TransactionType.TRANS_IN));
 * criteria.setProjection(Projections.distinct(Projections.count("product.id")));
 * 
 */
