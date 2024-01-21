package com.pkm.medicalinventory.inventory.query;

import org.hibernate.Criteria;
import org.hibernate.Session;

public class CriteriaWrapper implements AutoCloseable  {

	final Session session;
	final Criteria criteria;
	
	public CriteriaWrapper(Session session, Criteria criteria) {
		this.session = session;
		this.criteria = criteria;
	}
	
	public Session getSession() {
		return session;
	}
	public Criteria getCriteria() {
		return criteria;
	} 

	@Override
	public void close() throws Exception {
		 
		session.close();
	}
}

