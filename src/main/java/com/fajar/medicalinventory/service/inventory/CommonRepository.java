package com.fajar.medicalinventory.service.inventory;

import java.math.BigInteger;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.BigIntegerType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.entity.ProductFlow;

@Service
public class CommonRepository {

	@Autowired
	protected SessionFactory sessionFactory;
	
	protected Session getSession() {
		return sessionFactory.openSession();
	}
	protected BigInteger bigint(Object result) {
		if (null == result) {
			return BigInteger.ZERO;
		}
		return BigInteger.valueOf(Long.valueOf(result.toString()));
	}
	protected BigInteger bigintResult(Criteria criteria) {
		return bigint(criteria.uniqueResult());
	}
	protected Query getQuery(String queryString) {
		Session session = getSession();
		org.hibernate.Query q = session.createQuery(queryString);
		return q;
	}
	
	protected Criteria getCriteria(Class<? extends BaseEntity> _class) {
		Session session = getSession();
		return  session.createCriteria(ProductFlow.class);
	}
	protected Criteria commonGetStockCriteria(@Nullable Long locationId, TransactionType ... transactionTypes) {
		Criteria criteria = commonGetStockCriteria(locationId);
		Criterion[] typeRestrictions = new Criterion[transactionTypes.length];
		for (int i = 0; i < typeRestrictions.length; i++) {
			typeRestrictions[i] = Restrictions.eq("transaction.type", transactionTypes[i]);
		}
		criteria.add(Restrictions.or(
			typeRestrictions 
		));
		return criteria;
	}
	protected Criteria commonGetStockCriteria(@Nullable Long locationId) {
		Criteria criteria = getCriteria(ProductFlow.class);
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
}
