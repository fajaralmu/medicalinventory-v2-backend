package com.fajar.medicalinventory.service.inventory;

import java.math.BigInteger;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	protected Query getQuery(String queryString) {
		Session session = getSession();
		org.hibernate.Query q = session.createQuery(queryString);
		return q;
	}
}
