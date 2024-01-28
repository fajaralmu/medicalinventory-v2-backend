package com.pkm.medicalinventory.repository.main;

import org.hibernate.Session;

public interface PersistenceOperation<T> {
	
	public T doPersist(Session hibernateSession);

}
