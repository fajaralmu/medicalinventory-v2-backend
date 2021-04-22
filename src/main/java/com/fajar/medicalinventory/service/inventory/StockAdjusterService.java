package com.fajar.medicalinventory.service.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.fajar.medicalinventory.service.ProgressService;

@Service
public class StockAdjusterService {
	
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private ProgressService progressService;
	
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
		criteria.add(Restrictions.or(
				Restrictions.eq("transaction.type", TransactionType.TRANS_IN),
				Restrictions.eq("transaction.type", TransactionType.TRANS_OUT_TO_WAREHOUSE)
				));
		List productSupplyFlows = criteria.list();
		Map<Long, ProductFlow> productFlowMap = new HashMap<>();
		for (Object productSupplyFlow : productSupplyFlows) {
			ProductFlow pf = (ProductFlow) productSupplyFlow;
			pf.resetUsedCount();
			productFlowMap.put(pf.getId(), pf);
		}
		return productFlowMap;
	}
}
