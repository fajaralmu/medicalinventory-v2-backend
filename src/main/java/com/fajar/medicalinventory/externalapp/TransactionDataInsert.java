package com.fajar.medicalinventory.externalapp;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.fajar.medicalinventory.constants.Gender;
import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.entity.Customer;
import com.fajar.medicalinventory.entity.HealthCenter;
import com.fajar.medicalinventory.entity.Supplier;
import com.fajar.medicalinventory.entity.Transaction;
import com.fajar.medicalinventory.entity.User;
import com.fajar.medicalinventory.util.DateUtil;

public class TransactionDataInsert {
	static Session session;
	static int c = 1;

	public static void main(String[] args) {
//DONE		insertTransactions();
		 
	}
	
	

	


	private static void insertTransactions() {
		session = HibernateSessions.setSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		String[] transactionDataRaws = TransactionRawData.DATA.split("\r\n");
		for (int i = 0; i < transactionDataRaws.length; i++) {
			String transactionDataRaw = transactionDataRaws[i];
			Transaction object = extractData(transactionDataRaw);
			System.out.println(i + "   ==>   " + object);
			if (null != object) {
				session.save(object);
			}
		}
		tx.commit();
		session.close();
		System.exit(0);
	}



	private static Transaction extractData(String transactionDataRaw) {
		String[] properties = transactionDataRaw.split(";");
		if ("3".equals(properties[1])) {
			return null;
		}
		Transaction transaction = new Transaction();
		transaction.setCode(properties[0]);
		transaction.setUser((User) session.get(User.class, Long.valueOf(properties[2])));

		String customerCode = properties[4];
		String supplierCode = properties[3];
		String healthCenterCode = properties[5];
		String healthCenterLocationCode = properties[7];
		if ("".equals(supplierCode) == false) {
			List result = getObjectByCode(Supplier.class, supplierCode);
			System.out.println("Supplier result: " + result.size());
			if (result.size() == 0) {
				Supplier customer = new Supplier(supplierCode, "SUPPLIER" + c, "Address", "Contact");
				c++;
				session.save(customer);
			} else {
				transaction.setSupplier((Supplier) result.get(0));
			}
			transaction.setType(TransactionType.TRANS_IN);
		} else if ("".equals(customerCode) == false) {
			List result = getObjectByCode(Customer.class, customerCode);
			System.out.println("Customer result: " + result.size());
			if (result.size() == 0) {
				Customer customer = new Customer(customerCode, "CUSTOMER " + c, "Address", Gender.MALE,
						DateUtil.getDate(1990, 3, 15), null);
				c++;
				session.save(customer);
			} else {
				transaction.setCustomer((Customer) result.get(0));
			}
			transaction.setType(TransactionType.TRANS_OUT);
		} else if ("".equals(healthCenterCode) == false) {
			List result = getObjectByCode(HealthCenter.class, healthCenterCode);
			System.out.println("HealthCenter result: " + result.size() + " with code:  " + "PKM" + healthCenterCode);
			if (result.size() > 0) {
				transaction.setHealthCenterDestination((HealthCenter) result.get(0));
			}
			transaction.setType(TransactionType.TRANS_OUT_TO_WAREHOUSE);
		}

		
		if ("".equals(healthCenterLocationCode) == false) {
			 
			String filterCode;
			if ("1".equals(healthCenterLocationCode)) {
				filterCode = "SRUWENG"; 
			} else {
				filterCode = "PKM" + healthCenterLocationCode;
			} 
			List result = getObjectByCode(HealthCenter.class, filterCode);
			System.out.println("HealthCenter Location result: " + result.size() + " with code: "+ healthCenterLocationCode);
			if (result.size() > 0) {
				transaction.setHealthCenterLocation((HealthCenter) result.get(0));
			}
		}
		
		if (transaction.getSupplier() == null && transaction.getCustomer() == null
				&& transaction.getHealthCenterDestination() == null) {
			return null;
		}
		
		transaction.setTransactionDate(TransactionRawData.getTransactionDate(transaction.getCode()));
		return transaction;
	}
	
	static <T> List<T> getObjectByCode(Class<T> _class, String code) {
		Criteria criteria = session.createCriteria(_class);
		criteria.add(Restrictions.eq("code", code));
		return criteria.list();
	}
}
