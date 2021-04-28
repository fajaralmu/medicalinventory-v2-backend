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
import org.hibernate.type.IntegerType;
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
		try {
			return BigInteger.valueOf(Long.valueOf(result.toString()));
		} catch (Exception e) {
			System.out.println("ERROR PARSING NUMBER: "+result);
			e.printStackTrace();
			return BigInteger.ZERO;
		}
	}
	protected BigInteger bigintResult(CriteriaWrapper criteriaWrapper) {
		BigInteger result = bigint(criteriaWrapper.getCriteria().uniqueResult());
		return result;
	}
	protected Query getQuery(String queryString) {
		Session session = getSession();
		org.hibernate.Query q = session.createQuery(queryString);
		return q;
	}
	
	protected CriteriaWrapper getCriteria(Class<? extends BaseEntity> _class) {
		Session session = getSession();
		Criteria c = session.createCriteria(_class);
	
		return new CriteriaWrapper(session, c);
	}
	protected CriteriaWrapper commonStockCriteria(@Nullable Long locationId, TransactionType ... transactionTypes) {
		CriteriaWrapper criteriaWrapper = commonStockCriteria(locationId);
		Criteria criteria = criteriaWrapper.getCriteria();
		Criterion[] typeRestrictions = new Criterion[transactionTypes.length];
		for (int i = 0; i < typeRestrictions.length; i++) {
			typeRestrictions[i] = Restrictions.eq("transaction.type", transactionTypes[i]);
		}
		criteria.add(Restrictions.or(typeRestrictions));
		return criteriaWrapper;
	}
	protected CriteriaWrapper commonStockCriteria(@Nullable Long locationId) {
		CriteriaWrapper criteriaWrapper = getCriteria(ProductFlow.class);
		Criteria criteria = criteriaWrapper.getCriteria();
		criteria.createAlias("transaction", "transaction");
		if (null != locationId) {
			criteria.add(Restrictions.eq("transaction.healthCenterDestination.id", locationId));
		}
		criteria.add(Restrictions.sqlRestriction("(count - used_count) > ?", 0 , IntegerType.INSTANCE));
		
		return criteriaWrapper;
	}
	
	
}
