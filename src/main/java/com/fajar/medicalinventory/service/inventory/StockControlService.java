package com.fajar.medicalinventory.service.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.exception.ApplicationException;
import com.fajar.medicalinventory.exception.DataNotFoundException;
import com.fajar.medicalinventory.repository.ProductFlowRepository;
import com.fajar.medicalinventory.repository.TransactionRepository;
import com.fajar.medicalinventory.service.ProgressService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StockControlService {

	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private ProgressService progressService;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private ProductFlowRepository productFlowRepository;

	public synchronized WebResponse adjustStock(HttpServletRequest httpServletRequest) {
		Transaction tx = null;
		Session session = sessionFactory.openSession();
		try {

			tx = session.beginTransaction();

			Map<Long, ProductFlow> productFlowMap = getSupplyFlowReseted(session);
			progressService.sendProgress(10, httpServletRequest);

			List productUsedFlows = getDistributedFlow(session);
			progressService.sendProgress(10, httpServletRequest);
			System.out.println("==");
			for (Object object : productUsedFlows) {
				ProductFlow pf = (ProductFlow) object;
				Long refId = pf.getReferenceProductFlow().getId();
				productFlowMap.get(refId).addUsedCount(pf.getCount());
				pf.copyFromReferenceFlow();

				session.merge(pf);
				sendProgress(1, productUsedFlows.size(), 35, httpServletRequest);

			}
			for (Long id : productFlowMap.keySet()) {
				session.merge(productFlowMap.get(id));
				sendProgress(1, productFlowMap.keySet().size(), 35, httpServletRequest);
			}
			tx.commit();
			progressService.sendProgress(10, httpServletRequest);
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
		} finally {
			session.close();
		}
		System.out.println("==");
		return new WebResponse();
	}

	private void sendProgress(int progress, int maxProgress, int percent, HttpServletRequest httpServletRequest) {
		progressService.sendProgress(progress, maxProgress, percent, httpServletRequest);
	}

	private List getDistributedFlow(Session session) {
		Criteria criteriaUsed = session.createCriteria(ProductFlow.class);
		criteriaUsed.add(Restrictions.isNotNull("referenceProductFlow"));
		List productUsedFlows = criteriaUsed.list();
		return productUsedFlows;
	}

	private Map<Long, ProductFlow> getSupplyFlowReseted(Session session) {

//				"select pf from ProductFlow pf left join pf.transaction tx " + " where tx.type = ? or tx.type = ? "

		Criteria criteria = session.createCriteria(ProductFlow.class);
		criteria.createAlias("transaction", "transaction");
		criteria.add(Restrictions.or(Restrictions.eq("transaction.type", TransactionType.TRANS_IN),
				Restrictions.eq("transaction.type", TransactionType.TRANS_OUT_TO_WAREHOUSE)));
		List productSupplyFlows = criteria.list();
		Map<Long, ProductFlow> productFlowMap = new HashMap<>();
		for (Object productSupplyFlow : productSupplyFlows) {
			ProductFlow pf = (ProductFlow) productSupplyFlow;
			pf.resetUsedCount();
			productFlowMap.put(pf.getId(), pf);
		}
		return productFlowMap;
	}

	public WebResponse getTransactionRelatedRecords(String code) {

		try {
			com.fajar.medicalinventory.entity.Transaction record = transactionRepository.findByCode(code);
			if (null == record) {
				throw new DataNotFoundException("Record not found");
			}
			final TransactionType type = record.getType();
			List<ProductFlow> productFlows = productFlowRepository.findByTransaction(record);

			if (type.equals(TransactionType.TRANS_OUT)) {
				return WebResponse.builder().transaction(record.toModel()).build();
			}
			System.out.println("========= level 1 =========");
			setReferencingFlows(productFlows);

			if (type.equals(TransactionType.TRANS_OUT_TO_WAREHOUSE)) {
				return WebResponse.builder().transaction(record.toModel()).build();
			}
			System.out.println("========= level 2 =========");
			setReferencingFlows(combineReferencingItems(productFlows));
			
			record.setProductFlows(productFlows);
//			summary(record);
			return WebResponse.builder().transaction(record.toModel()).build();
		} catch (Exception e) {
			if (e instanceof DataNotFoundException) {
				throw e;
			}
			e.printStackTrace();
			throw new ApplicationException(e);
		}

	}

	private static void summary(com.fajar.medicalinventory.entity.Transaction record) {

		log.info("Id: {}", record.getId());
		log.info("Type: {}", record.getType());
		List<ProductFlow> productFlows = record.getProductFlows();
		int i = 1;
		for (ProductFlow item : productFlows) {
			log.info("{}. Item id: {}, qty: ({}) --- {}", i, item.getId(), item.getCount(), transactionDate(item));
			List<ProductFlow> referencing1 = item.getReferencingItems();
			if (referencing1 != null && !referencing1.isEmpty()) {
				printReferencing(referencing1, 1);
			}
			i++;
		}
	}

	private static void printReferencing(List<ProductFlow> referencingItems, int level) {
		int i = 1;
		for (ProductFlow item : referencingItems) {
			List<ProductFlow> referencing1 = item.getReferencingItems();

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
		productFlows.forEach(p -> {
			items.addAll(p.getReferencingItems());
		});
		return items;
	}

	private void setReferencingFlows(List<ProductFlow> productFlows) {
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
			productFlow.setReferencingItems(referencingItems == null ? new ArrayList<>() : referencingItems);
		}
	}

	private List<ProductFlow> getReferencingFlows(List<ProductFlow> productFlows) {

		if (productFlows == null || productFlows.isEmpty()) {
			return new ArrayList<>();
		}
		return productFlowRepository.findByReferenceProductFlowIn(productFlows);
	}

}
