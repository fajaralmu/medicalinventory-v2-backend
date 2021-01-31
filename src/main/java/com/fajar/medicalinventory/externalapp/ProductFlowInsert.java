package com.fajar.medicalinventory.externalapp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.entity.Transaction;

public class ProductFlowInsert {

	static Session session;
	static Map<String, Product> productMap = new HashMap<>();

	public static void main(String[] args) throws Exception {
		session = HibernateSessions.setSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			fillProducts();
			insertProductFlows_TRANS_OUT();
			tx.commit();
		}catch (Exception e) {
			tx.rollback();
		}
		session.close();
		System.exit(0);
	}

	private static void fillProducts() {
		Criteria criteria = session.createCriteria(Product.class);
		List list = criteria.list();
		for (Object object : list) {
			Product p = (Product) object;
			productMap.put(p.getCode(), p);
		}
		
	}

	private static void insertProductFlows_TRANS_IN() throws Exception {
		 
		ProductFlowsData.setMap();
		Map<String, List<ProductFlow>> map = ProductFlowsData.map;
		int counter = 1;
		for (String code : map.keySet()) {
			List<Transaction> result = getObjectByCode(Transaction.class, code);
			if (result.size() == 0) {
				System.out.println("CODE NOT FOUND: "+code);
				continue;
			}
			Transaction trx = result.get(0);
			List<ProductFlow> productFlows = map.get(code);
			innerLoop: for (ProductFlow productFlow : productFlows) { 
				System.out.println("productFlow.getReferenceProductFlow()@@" +productFlow.getReferenceProductFlow().getId());
				if (  productFlow.getReferenceProductFlow() != null &&  productFlow.getReferenceProductFlow().getId() > 0) {
					continue innerLoop;
				}
				Product p =  productMap.get(productFlow.getProduct().getCode());
				if (null == p) {
					System.out.println("PRODUCT NULL "+productFlow.getProduct().getCode());
				}
				productFlow.setReferenceProductFlow(null);
				productFlow.setTransaction(trx);
				productFlow.setProduct(p );
				
				session.save(productFlow);
				 System.out.println("SAVED");
			}
			System.out.println(counter + "/" + map.keySet().size() + "  -------> " + trx);
			counter++;
		}

		 
		
	} 
	
	private static void insertProductFlows_TRANS_OUT() throws Exception {
		 
		ProductFlowsData.setMap();
		Map<String, List<ProductFlow>> map = ProductFlowsData.map;
		int counter = 1;
		for (String code : map.keySet()) {
			List<Transaction> result = getObjectByCode(Transaction.class, code);
			if (result.size() == 0) {
				System.out.println("CODE NOT FOUND: "+code);
				continue;
			}
			Transaction trx = result.get(0);
			List<ProductFlow> productFlows = map.get(code);
			innerLoop: for (ProductFlow productFlow : productFlows) { 
				 
				if (  productFlow.getReferenceProductFlow() == null ||  productFlow.getReferenceProductFlow().getId() <= 0) {
					continue innerLoop;
				}
				Product p =  productMap.get(productFlow.getProduct().getCode());
				if (null == p) {
					System.out.println("PRODUCT NULL "+productFlow.getProduct().getCode());
				}
//				productFlow.setReferenceProductFlow(sess);
				productFlow.setTransaction(trx);
				productFlow.setProduct(p );
				
				session.save(productFlow);
				 System.out.println("SAVED");
			}
			System.out.println(counter + "/" + map.keySet().size() + "  -------> " + trx);
			counter++;
		}

		 
		
	} 
	 
	static <T> List<T> getObjectByCode(Class<T> _class, String code) {
		Criteria criteria = session.createCriteria(_class);
		criteria.add(Restrictions.eq("code", code));
		return criteria.list();
	}

}
