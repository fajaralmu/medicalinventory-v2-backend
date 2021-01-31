package com.fajar.medicalinventory.externalapp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.fajar.medicalinventory.entity.ProductFlow;

public class StockAdjustment {

	static Session session;
	static Map<Long, ProductFlow> productFlowMap = new HashMap<Long, ProductFlow>();
	
	public static void main(String[] args) {
		session = HibernateSessions.setSession();
		Criteria criteria = session.createCriteria(ProductFlow.class);
		criteria.add(Restrictions.isNotNull("referenceProductFlow"));
		List list = criteria.list();
		for (Object object : list) {
			ProductFlow productFlow = (ProductFlow) object;
			ProductFlow reference = productFlow.getReferenceProductFlow();
			if (null == productFlowMap.get(reference.getId())) {
				productFlowMap.put(reference.getId(), reference);
			}
			productFlowMap.get(reference.getId()).addUsedCount(productFlow.getCount());
			
		}
		
		Transaction tx = session.beginTransaction();
		for (Long id : productFlowMap.keySet()) {
			session.merge(productFlowMap.get(id));
			System.out.println("saved: "+id);
		}
		
		tx.commit();
		session.close();
		System.exit(0);
	}
}
