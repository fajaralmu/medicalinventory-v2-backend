package com.fajar.medicalinventory.externalapp;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.BeanUtils;

import com.fajar.medicalinventory.entity.Unit;
import com.fajar.medicalinventory.entity.User;

public class TestModel {

	static Session session;
	public static void main(String[] args) {
		session = HibernateSessions.setSession();
//		getUser();
		unit();
		session.close();
		System.exit(0);
		
	}
	private static void unit() {
		
		Transaction tx = session.beginTransaction();
		Unit unitRecord = (Unit) session.get(Unit.class, 1076L);
		Unit newObject = new Unit();
		
		//////////WORK
		
		BeanUtils.copyProperties(unitRecord, newObject );
		newObject.setId(1077L);
		
//		/////////////NOT WORK
//		unitRecord.setId(1077L);
//		BeanUtils.copyProperties(unitRecord, newObject );
		
		newObject.setName("TESTONLY TESTSTST");
		session.save(newObject);
		tx.commit();
	}
	private static void getUser() {
		// TODO Auto-generated method stub
		Criteria c = session.createCriteria(User.class);
		List<User>users = c.list();
		for (User user : users) {
			System.out.println(user.getAuthorities());
		}
	}
}
