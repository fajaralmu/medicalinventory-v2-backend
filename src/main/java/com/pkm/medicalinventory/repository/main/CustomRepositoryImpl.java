package com.pkm.medicalinventory.repository.main;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomRepositoryImpl implements CustomRepository {
	@Autowired
	private SessionFactory sessionFactory;

	public CustomRepositoryImpl() {
		log.info("-------CustomRepositoryImpl-----------");
	}

	public DatabaseProcessor createDatabaseProcessor() {
		return new DatabaseProcessor(sessionFactory);
	}
}
