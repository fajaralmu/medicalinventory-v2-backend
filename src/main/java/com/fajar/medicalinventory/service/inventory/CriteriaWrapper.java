package com.fajar.medicalinventory.service.inventory;

import org.hibernate.Criteria;
import org.hibernate.Session;

public class CriteriaWrapper {

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
	public void closeSession() {
		session.close();
	}
}
