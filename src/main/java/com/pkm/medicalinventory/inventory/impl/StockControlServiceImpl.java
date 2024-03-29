package com.pkm.medicalinventory.inventory.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pkm.medicalinventory.constants.TransactionType;
import com.pkm.medicalinventory.dto.model.TransactionModel;
import com.pkm.medicalinventory.entity.ProductFlow;
import com.pkm.medicalinventory.exception.ApplicationException;
import com.pkm.medicalinventory.exception.DataNotFoundException;
import com.pkm.medicalinventory.inventory.StockControlService;
import com.pkm.medicalinventory.repository.readonly.ProductFlowRepository;
import com.pkm.medicalinventory.repository.readonly.TransactionRepository;
import com.pkm.medicalinventory.service.ProgressService;
import com.pkm.medicalinventory.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StockControlServiceImpl implements StockControlService {
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private ProgressService progressService;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private ProductFlowRepository productFlowRepository;

	public synchronized void adjustStock() {
		Transaction tx = null;
		Session session = sessionFactory.openSession();
		List<Exception> errors = new ArrayList<>();

		try {
			tx = session.beginTransaction();

			Map<Long, ProductFlow> productFlowMap = getSupplyFlowReseted(session);
			progressService.sendProgress(10);

			List<ProductFlow> productUsedFlows = getDistributedFlow(session);
			progressService.sendProgress(10);
			System.out.println("==");
			for (ProductFlow pf : productUsedFlows) {
				try {
					Long refId = pf.getReferenceProductFlow().getId();
					productFlowMap.get(refId).addUsedCount(pf.getCount());
					pf.copyFromReferenceFlow();

					session.merge(pf);
					sendProgress(1, productUsedFlows.size(), 35);
				} catch (Exception e) {
					errors.add(e);
				}
			}
			for (Long id : productFlowMap.keySet()) {
				ProductFlow pf = productFlowMap.get(id);
				if (pf.getStock() > 0) {
					log.info("Reset product {}, incoming: {}, used: {}, remaining: {}", pf.getProduct().getName(),
							pf.getCount(), pf.getUsedCount(), pf.getStock());
				}
				session.merge(pf);
				sendProgress(1, productFlowMap.keySet().size(), 35);
			}
			if (errors.size() > 0) {
				throw ApplicationException.fromMessage("Failed to update stock");
			}
			tx.commit();
			progressService.sendProgress(10);
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			if (errors.size() > 0) {
				List<String> messages = errors.stream().map(ex -> ex.getMessage()).collect(Collectors.toList());
				throw ApplicationException.fromMessage(String.join(",", CollectionUtil.toArrayOfString(messages))
						+ "\n Please modify the stock records");
			}
		} finally {
			session.close();
		}
		log.info("= Adjust stock stopped =");
	}

	private void sendProgress(int progress, int maxProgress, int percent) {
		progressService.sendProgress(progress, maxProgress, percent);
	}

	private List<ProductFlow> getDistributedFlow(Session session) {
		Criteria criteriaUsed = session.createCriteria(ProductFlow.class);
		criteriaUsed.add(Restrictions.isNotNull("referenceProductFlow"));
		List<ProductFlow> productUsedFlows = criteriaUsed.list();
		return productUsedFlows;
	}

	private Map<Long, ProductFlow> getSupplyFlowReseted(Session session) {

		// "select pf from ProductFlow pf left join pf.transaction tx " + " where
		// tx.type = ? or tx.type = ? "

		Criteria criteria = session.createCriteria(ProductFlow.class);
		criteria.createAlias("transaction", "transaction");
		criteria.add(Restrictions.or(Restrictions.eq("transaction.type", TransactionType.TRANS_IN),
				Restrictions.eq("transaction.type", TransactionType.TRANS_OUT_TO_WAREHOUSE)));
		List<ProductFlow> productSupplyFlows = criteria.list();
		Map<Long, ProductFlow> productFlowMap = new HashMap<>();
		for (ProductFlow pf : productSupplyFlows) {
			pf.resetUsedCount();
			productFlowMap.put(pf.getId(), pf);
		}
		return productFlowMap;
	}

	public TransactionModel getTransactionRelatedRecords(String code) {
		try {
			com.pkm.medicalinventory.entity.Transaction record = transactionRepository.findByCode(code);
			progressService.sendProgress(10);

			if (null == record) {
				throw new DataNotFoundException("Record not found");
			}
			final TransactionType type = record.getType();
			List<ProductFlow> productFlows = productFlowRepository.findByTransaction(record);
			progressService.sendProgress(10);
			record.setProductFlows(productFlows);

			if (type.equals(TransactionType.TRANS_OUT)) {
				return record.toModel();
			}
			System.out.println("========= level 1 =========");
			setReferencingFlows(productFlows);
			progressService.sendProgress(40);

			if (type.equals(TransactionType.TRANS_OUT_TO_WAREHOUSE)) {
				return record.toModel();
			}
			System.out.println("========= level 2 =========");
			setReferencingFlows(combineReferencingItems(productFlows));
			progressService.sendProgress(40);

			// summary(record);
			return record.toModel();
		} catch (Exception e) {
			if (e instanceof DataNotFoundException) {
				throw e;
			}
			e.printStackTrace();
			throw new ApplicationException(e);
		}

	}

	private static void printReferencing(List<ProductFlow> referencingItems, int level) {
		int i = 1;
		for (ProductFlow item : referencingItems) {
			List<ProductFlow> referencing1 = item.getReferencingItems();

			log.info(StringUtils.repeat("  ", level) + i + ". Item id:{}, qty: ({}) --- {}", item.getId(), item.getCount(), transactionDate(item));
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
