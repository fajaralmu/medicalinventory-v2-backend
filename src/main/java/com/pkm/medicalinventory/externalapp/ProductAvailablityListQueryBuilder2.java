package com.pkm.medicalinventory.externalapp;

import static com.pkm.medicalinventory.util.DateUtil.getDate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.pkm.medicalinventory.constants.TransactionType;
import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.entity.ProductFlow;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class ProductAvailablityListQueryBuilder2 {

	static long branch_id = 15L;
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

	static Criteria commonCriteria(Long locationId) {
		Criteria criteria = session.createCriteria(ProductFlow.class);
		criteria.createAlias("transaction", "transaction");
		criteria.createAlias("product", "product");
		if (null != locationId) {
			criteria.add(Restrictions.eq("transaction.healthCenterDestination.id", locationId));
		}
		criteria.add(Restrictions.sqlRestriction("(this_.count - this_.used_count) > ?", 0, IntegerType.INSTANCE));
		criteria.setProjection(projections());
		criteria.addOrder(Order.asc("product.name"));
		return criteria;
	}

	static Criteria commonCriteria() {
		return commonCriteria(null);
	}

	public static void main(String[] args) {
		try {
			session = HibernateSessions.setSession();
			Pageable pageable = PageRequest.of(0, 100);

			// branch
//			findNotEmptyProductInSpecifiedWarehouse(branch_id, pageable);
//			findNotEmptyProductInSpecifiedWarehouseWithExpDateBefore(branch_id, DateUtil.getDate(2020, 0, 1), pageable);
//			findNotEmptyProductInSpecifiedWarehouseWithExpDateBeforeAfter(branch_id, DateUtil.getDate(2022, 0, 1),
//					DateUtil.getDate(2020, 0, 1), pageable);

			// master
//			findNotEmptyProductInMasterWarehouse(pageable);
//			findNotEmptyProductInMasterWarehouseWithExpDateBefore(DateUtil.getDate(2018, 1, 1), pageable);
//			findNotEmptyProductInMasterWarehouseWithExpDateBeforeAfter(DateUtil.getDate(2022, 0, 1),
//					DateUtil.getDate(2020, 0, 1), pageable);

			// all
//			findNotEmptyProductAllLocation(pageable);
//			findNotEmptyProductAllLocationWithExpDateBefore(getDate(2019, 1, 1), pageable);
			findNotEmptyProductAllLocationWithExpDateBeforeAfter(getDate(2022, 0, 1), getDate(2020, 0, 1), pageable);
		} catch (Exception e) {
			e.printStackTrace();

		}
		if (null != session) {
			session.close();
		}
		System.exit(0);
	}

	/////////////////////// Branch /////////////////////////////////

	static List<Product> findNotEmptyProductInSpecifiedWarehouse(Long locationId, Pageable of) {
		String query = "select distinct(p) from  ProductFlow pf  left join pf.product p "
				+ " left join pf.transaction tx left join tx.healthCenterDestination location "
				+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE' and location.id = ?  and (pf.count- pf.usedCount) > 0 order "
				+ "by p.name";
		Query q = getQuery(query);
		q.setParameter(0, locationId);
		q.setFirstResult(Long.valueOf(of.getOffset()).intValue());
		q.setMaxResults(of.getPageSize());

		List list1 = q.list();
		System.out.println(list1);
		System.out.println(" ==================***=================== ");
		Criteria criteria = commonCriteria(locationId);

		criteria.add(Restrictions.eq("transaction.type", TransactionType.TRANS_OUT_TO_WAREHOUSE));

		setLimitOffset(criteria, of);
		List<Product> products = extractProducts(criteria);
		System.out.println(products);
		return products;
	}

	private static Projection projections() {
		return Projections.projectionList().add(Projections.distinct(Projections.property("product.id")))
				.add(Projections.property("product.name"));
	}

	static List<Product> getProductWhereIdIn(List ids) {
		if (ids == null || ids.isEmpty()) {
			return new ArrayList<>();
		}
		Criteria criteria = session.createCriteria(Product.class);
		criteria.add(Restrictions.in("id", ids));
		return criteria.list();
	}

	static List<Product> findNotEmptyProductInSpecifiedWarehouseWithExpDateBefore(Long locationId, Date expDateBefore,
			Pageable of) {
		String query = "select distinct(p) from  ProductFlow pf  left join pf.product p "
				+ " left join pf.transaction tx left join tx.healthCenterDestination location "
				+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE' and location.id = ? and (pf.count- pf.usedCount) > 0 "
				+ " and pf.expiredDate <  ? order by p.name";
		Query q = getQuery(query);
		q.setParameter(0, locationId);
		q.setParameter(1, expDateBefore);
		q.setFirstResult(Long.valueOf(of.getOffset()).intValue());
		q.setMaxResults(of.getPageSize());
		List list1 = q.list();
		System.out.println(list1);
		System.out.println(" ==================***=================== ");
		Criteria criteria = commonCriteria(locationId);

		criteria.add(Restrictions.eq("transaction.type", TransactionType.TRANS_OUT_TO_WAREHOUSE));
		criteria.add(Restrictions.lt("expiredDate", expDateBefore));

		setLimitOffset(criteria, of);
		List<Product> products = extractProducts(criteria);
		System.out.println(products);
		return products;
	}

	static void setLimitOffset(Criteria criteria, Pageable of) {

		criteria.setFirstResult(Long.valueOf(of.getOffset()).intValue());
		criteria.setMaxResults(of.getPageSize());
	}

//		@Query("select distinct(p) from  ProductFlow pf  left join pf.product p "
//				+ " left join pf.transaction tx left join tx.healthCenterDestination location "
//				+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE' and location.id = ?1 and (pf.count- pf.usedCount) > 0 "
//				+ " and pf.expiredDate <  ?2 and pf.expiredDate > ?3  order by p.name")
	static List<Product> findNotEmptyProductInSpecifiedWarehouseWithExpDateBeforeAfter(Long locationId,
			Date expDateBefore, Date expDateAfter, Pageable of) {
		String query = "select distinct(p) from  ProductFlow pf  left join pf.product p "
				+ " left join pf.transaction tx left join tx.healthCenterDestination location "
				+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE' and location.id = ? and (pf.count- pf.usedCount) > 0 "
				+ " and pf.expiredDate between ? and ? order by p.name";
		Query q = getQuery(query);
		q.setParameter(0, locationId);
		q.setParameter(1, expDateAfter);
		q.setParameter(2, expDateBefore);
		q.setFirstResult(Long.valueOf(of.getOffset()).intValue());
		q.setMaxResults(of.getPageSize());
		List list1 = q.list();
		System.out.println(list1);
		System.out.println(" ==================***=================== ");
		Criteria criteria = commonCriteria(locationId);

		criteria.add(Restrictions.eq("transaction.type", TransactionType.TRANS_OUT_TO_WAREHOUSE));
		criteria.add(Restrictions.between("expiredDate", expDateAfter, expDateBefore));

		setLimitOffset(criteria, of);
		List<Product> products = extractProducts(criteria);
		System.out.println(products);
		return products;

	}

	///////////////////////// MASTER ///////////////////////////////

	// LIST AT Master warehouse

	static List<Product> findNotEmptyProductInMasterWarehouse(Pageable of) {
		String query = "select distinct(p) from  ProductFlow pf left join pf.product p left join pf.transaction tx "
				+ " where tx.type = 'TRANS_IN' and (pf.count- pf.usedCount) > 0 order by p.name";
		Query q = getQuery(query);
		q.setFirstResult(Long.valueOf(of.getOffset()).intValue());
		q.setMaxResults(of.getPageSize());
		List<Product> list1 = q.list();
		final List _ids = new LinkedList<>();
		list1.forEach(p -> {
			_ids.add(p.getId());
		});
		System.out.println(_ids);
		System.out.println(list1);
		System.out.println(" ==================***=================== ");
		Criteria criteria = commonCriteria();

		criteria.add(Restrictions.eq("transaction.type", TransactionType.TRANS_IN));
		setLimitOffset(criteria, of);

		List<Product> products = extractProducts(criteria);

		final List ids = new LinkedList<>();
		products.forEach(p -> {
			ids.add(p.getId());
		});
		System.out.println(ids);
		System.out.println(products);
		return products;
	}

	static List<Product> findNotEmptyProductInMasterWarehouseWithExpDateBefore(Date expDateBefore, Pageable of) {
		String query = "select distinct(p) from  ProductFlow pf  left join pf.product p left join pf.transaction tx "
				+ " where tx.type = 'TRANS_IN' and (pf.count- pf.usedCount) > 0  and pf.expiredDate <  ?  "
				+ " order by p.name";
		Query q = getQuery(query);
		q.setParameter(0, expDateBefore);
		q.setFirstResult(Long.valueOf(of.getOffset()).intValue());
		q.setMaxResults(of.getPageSize());

		List<Product> list1 = q.list();
		final List _ids = new LinkedList<>();
		list1.forEach(p -> {
			_ids.add(p.getId());
		});
		System.out.println(_ids);
		System.out.println(list1);
		System.out.println(" ==================***=================== ");
		Criteria criteria = commonCriteria();

		criteria.add(Restrictions.eq("transaction.type", TransactionType.TRANS_IN));
		criteria.add(Restrictions.lt("expiredDate", expDateBefore));
		setLimitOffset(criteria, of);

		List<Product> products = extractProducts(criteria);

		final List ids = new LinkedList<>();
		products.forEach(p -> {
			ids.add(p.getId());
		});
		System.out.println(ids);
		System.out.println(products);
		return products;
	}

	static List<Product> findNotEmptyProductInMasterWarehouseWithExpDateBeforeAfter(Date expDateBefore,
			Date expDateAfter, Pageable of) {
		String query = "select distinct(p) from  ProductFlow pf  left join pf.product p left join pf.transaction tx "
				+ " where tx.type = 'TRANS_IN' and (pf.count- pf.usedCount) > 0  and pf.expiredDate between ? and ? "
				+ " order by p.name";
		Query q = getQuery(query);
		q.setParameter(0, expDateAfter);
		q.setParameter(1, expDateBefore);
		q.setFirstResult(Long.valueOf(of.getOffset()).intValue());
		q.setMaxResults(of.getPageSize());

		List<Product> list1 = q.list();
		final List _ids = new LinkedList<>();
		list1.forEach(p -> {
			_ids.add(p.getId());
		});
		System.out.println(_ids);
		System.out.println(list1);
		System.out.println(" ==================***=================== ");
		Criteria criteria = commonCriteria();

		criteria.add(Restrictions.eq("transaction.type", TransactionType.TRANS_IN));
		criteria.add(Restrictions.between("expiredDate", expDateAfter, expDateBefore));
		setLimitOffset(criteria, of);

		List<Product> products = extractProducts(criteria);

		final List ids = new LinkedList<>();
		products.forEach(p -> {
			ids.add(p.getId());
		});
		System.out.println(ids);
		System.out.println(products);
		return products;
	}

	////////////////// ALL LOCATION //////////////////////
	// @Query("select distinct(p) from ProductFlow pf left join pf.product p left
	////////////////// join pf.transaction tx "
	// + " where (tx.type = 'TRANS_IN' OR tx.type = 'TRANS_OUT_TO_WAREHOUSE') and
	////////////////// (pf.count- pf.usedCount) > 0 order by p.name")
	static List<Product> findNotEmptyProductAllLocation(Pageable of) {
		String query = "select distinct(p) from  ProductFlow pf left join pf.product p left join pf.transaction tx "
				+ " where (tx.type = 'TRANS_IN' OR tx.type = 'TRANS_OUT_TO_WAREHOUSE') and"
				+ " (pf.count- pf.usedCount) > 0 order by p.name";
		Query q = getQuery(query);
		q.setFirstResult(Long.valueOf(of.getOffset()).intValue());
		q.setMaxResults(of.getPageSize());

		List<Product> list1 = q.list();
		final List _ids = new LinkedList<>();
		list1.forEach(p -> {
			_ids.add(p.getId());
		});
		System.out.println(_ids);
		System.out.println(list1);
		System.out.println(" ==================***=================== ");
		Criteria criteria = commonCriteria();

		criteria.add(Restrictions.or(Restrictions.eq("transaction.type", TransactionType.TRANS_IN),
				Restrictions.eq("transaction.type", TransactionType.TRANS_OUT_TO_WAREHOUSE)));
		setLimitOffset(criteria, of);

		List<Product> products = extractProducts(criteria);

		final List ids = new LinkedList<>();
		products.forEach(p -> {
			ids.add(p.getId());
		});
		System.out.println(ids);
		System.out.println(products);
		return products;
	}

//	 @Query("select distinct(p) from ProductFlow pf left join pf.product p left join pf.transaction tx "
//	 + " where (tx.type = 'TRANS_IN' OR tx.type = 'TRANS_OUT_TO_WAREHOUSE') and (pf.count- pf.usedCount) > 0 and pf.expiredDate < ?1 "
//	 + " order by p.name")
	static List<Product> findNotEmptyProductAllLocationWithExpDateBefore(Date expDateBefore, Pageable of) {
		String query = "select distinct(p) from ProductFlow pf left join pf.product p left join pf.transaction tx "
				+ " where (tx.type = 'TRANS_IN' OR tx.type = 'TRANS_OUT_TO_WAREHOUSE') and (pf.count- pf.usedCount) > 0 and pf.expiredDate < ? "
				+ " order by p.name";
		Query q = getQuery(query);
		q.setParameter(0, expDateBefore);
		q.setFirstResult(Long.valueOf(of.getOffset()).intValue());
		q.setMaxResults(of.getPageSize());

		List<Product> list1 = q.list();
		final List _ids = new LinkedList<>();
		list1.forEach(p -> {
			_ids.add(p.getId());
		});
		System.out.println(_ids);
		System.out.println(list1);
		System.out.println(" ==================***=================== ");
		Criteria criteria = commonCriteria();

		criteria.add(Restrictions.or(Restrictions.eq("transaction.type", TransactionType.TRANS_IN),
				Restrictions.eq("transaction.type", TransactionType.TRANS_OUT_TO_WAREHOUSE)));
		;
		criteria.add(Restrictions.lt("expiredDate", expDateBefore));
		setLimitOffset(criteria, of);

		List<Product> products = extractProducts(criteria);

		final List ids = new LinkedList<>();
		products.forEach(p -> {
			ids.add(p.getId());
		});
		System.out.println(ids);
		System.out.println(products);
		return products;
	}

	static List<Product> findNotEmptyProductAllLocationWithExpDateBeforeAfter(Date expDateBefore, Date expDateAfter,
			Pageable of) {
		String query = "select distinct(p) from ProductFlow pf left join pf.product p left  join pf.transaction tx "
				+ " where (tx.type = 'TRANS_IN' OR tx.type = 'TRANS_OUT_TO_WAREHOUSE') and (pf.count- pf.usedCount) > 0 "
				+ " and pf.expiredDate between ? and ? " + " order by p.name";
		Query q = getQuery(query);
		q.setParameter(0, expDateAfter);
		q.setParameter(1, expDateBefore);
		q.setFirstResult(Long.valueOf(of.getOffset()).intValue());
		q.setMaxResults(of.getPageSize());

		List<Product> list1 = q.list();
		final List _ids = new LinkedList<>();
		list1.forEach(p -> {
			_ids.add(p.getId());
		});
		System.out.println(_ids);
		System.out.println(list1);
		System.out.println(" ==================***=================== ");
		Criteria criteria = commonCriteria();

		criteria.add(Restrictions.or(Restrictions.eq("transaction.type", TransactionType.TRANS_IN),
				Restrictions.eq("transaction.type", TransactionType.TRANS_OUT_TO_WAREHOUSE)));
		;
		criteria.add(Restrictions.between("expiredDate", expDateAfter, expDateBefore));
		setLimitOffset(criteria, of);

		List<Product> products = extractProducts(criteria);

		final List ids = new LinkedList<>();
		products.forEach(p -> {
			ids.add(p.getId());
		});
		System.out.println(ids);
		System.out.println(products);
		return products;
	}

	static List<Product> extractProducts(Criteria criteria) {
		List result = criteria.list();
		List ids = new LinkedList<>();
		for (Object object : result) {
			Object[] o = (Object[]) object;
			ids.add(o[0]);
		}
		List<Product> products = getProductWhereIdIn(ids);
		products.sort(comp());
		return products;
	}

	private static Comparator<Product> comp() {
		return new Comparator<Product>() {
			@Override
			public int compare(Product o1, Product o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		};
	}

}
