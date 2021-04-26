package com.fajar.medicalinventory.externalapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.entity.Transaction;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestModel {

	static Session session;

	public static void main(String[] args) {
		session = HibernateSessions.setSession();

		transactionHistory(52L);
		session.close();
		System.exit(0);

	}

	private static void transactionHistory(Long id) {
		Transaction record = (Transaction) session.get(Transaction.class, id);
		final TransactionType type = record.getType();
		List<ProductFlow> productFlows = getProductFlows(record);

		if (type.equals(TransactionType.TRANS_OUT)) {
			return;
		}
		System.out.println("========= level 1 =========");
		setReferencingFlows(productFlows);

		if (type.equals(TransactionType.TRANS_OUT_TO_WAREHOUSE)) {
			return;
		}
		System.out.println("========= level 2 ========="); 
		setReferencingFlows(combineReferencingItems(productFlows));

		summary(record);

	}

	private static void summary(Transaction record) {
		
		log.info("Id: {}", record.getId());
		log.info("Type: {}", record.getType());
		List<ProductFlow> productFlows = record.getProductFlows();
		int i = 1;
		for (ProductFlow item : productFlows) {
			log.info("{}. Item id: {}, qty: ({}) --- {}",i,  item.getId(), item.getCount(), transactionDate(item));
			List<ProductFlow> referencing1 = item.getReferencingProductFlow();
			if (referencing1 != null && !referencing1.isEmpty()) {
				printReferencing(referencing1, 1);
			}
			i++;
		}
	}

	private static void printReferencing(List<ProductFlow> referencingItems, int level) {
		int i = 1;
		for (ProductFlow item : referencingItems) {
			List<ProductFlow> referencing1 = item.getReferencingProductFlow();

			log.info(StringUtils.repeat("  ", level) + i + ". Item id:{}, qty: ({}) --- {}", item.getId(),
					item.getCount(), transactionDate(item));
			if (referencing1 != null && !referencing1.isEmpty()) {
				printReferencing(referencing1, 2);
			}
			i++;
		}

	}

	private static String transactionDate(ProductFlow item) {
		return item.getTransaction().getTransactionDate().toGMTString();
	}

	private static List<ProductFlow> combineReferencingItems(List<ProductFlow> productFlows) {
		List<ProductFlow> items = new ArrayList<>();
		productFlows.forEach(p->{
			items.addAll(p.getReferencingProductFlow());
		});
		return items;
	}

	private static void setReferencingFlows(List<ProductFlow> productFlows) {
		List<ProductFlow> referencingFlows = getReferencingFlows(productFlows);
		mapReferencingFlows(productFlows, referencingFlows);
	}

	private static void mapReferencingFlows(List<ProductFlow> productFlows, List<ProductFlow> referencingFlows) {
		Map<Long, List<ProductFlow>> mapped = new HashMap<>();
		for (ProductFlow productFlow : referencingFlows) {
			Long refId = productFlow.getReferenceProductFlow().getId();
			if (mapped.get(refId) == null) {
				mapped.put(refId, new ArrayList<>());
			}
			mapped.get(refId).add(productFlow);
		}
		for (ProductFlow productFlow : productFlows) {
			List<ProductFlow> referencingItems = mapped.get(productFlow.getId());
			productFlow.setReferencingProductFlow(referencingItems == null ? new ArrayList<>() : referencingItems);
		}
	}

	private static List<ProductFlow> getReferencingFlows(List<ProductFlow> productFlows) {
		 
		if (productFlows == null || productFlows.isEmpty()) {
			return new ArrayList<>();
		}
		Criteria criteria = session.createCriteria(ProductFlow.class);
		criteria.add(Restrictions.in("referenceProductFlow", productFlows));
		return criteria.list();
	}

	private static List<ProductFlow> getProductFlows(Transaction record) {
		 
		Criteria criteria = session.createCriteria(ProductFlow.class);
		criteria.add(Restrictions.eq("transaction", record));
		List<ProductFlow> items = criteria.list();
		record.setProductFlows(items);
		return items;
	}
}
