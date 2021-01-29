package com.fajar.medicalinventory.service.transaction;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.config.exception.DataNotFoundException;
import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.dto.WebRequest;
import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.entity.HealthCenter;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.entity.Transaction;
import com.fajar.medicalinventory.repository.DatabaseProcessor;
import com.fajar.medicalinventory.service.ProgressService;
import com.fajar.medicalinventory.service.config.DefaultHealthCenterMasterService;

@Service
public class TransactionService {

	@Autowired
	private ProgressService progressService;
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private DefaultHealthCenterMasterService defaultHealthCenterMasterService;

	public WebResponse performTransactionIN(WebRequest webRequest, HttpServletRequest httpServletRequest) {
		Session session = sessionFactory.openSession();
		org.hibernate.Transaction hibernateTransaction = session.beginTransaction();
		try {
			 
			Transaction transaction = buildTransactionObject(webRequest);
			progressService.sendProgress(10, httpServletRequest);
			
			if (null == transaction.getSupplier()) {
				throw new DataNotFoundException("Supplier Missing");
			}
			transaction = DatabaseProcessor.save(transaction, session);
			
			progressService.sendProgress(10, httpServletRequest);
			List<ProductFlow> productFlows = transaction.getProductFlows();
			for (ProductFlow productFlow : productFlows) {
				productFlow.setTransaction(transaction);
				DatabaseProcessor.save(productFlow, session);
				
				progressService.sendProgress(1, productFlows.size(), 80, httpServletRequest);
			}
			hibernateTransaction.commit();
			
			WebResponse response = new WebResponse();
			response.setTransaction(transaction);
			return response;
		} catch (Exception e) {

			if (null != hibernateTransaction) {
				hibernateTransaction.rollback();
			}
			throw e;
		} finally {
			session.close();
		}
	}

	private Transaction buildTransactionObject(WebRequest webRequest) {
		HealthCenter masterHealthCenter = defaultHealthCenterMasterService.getMasterHealthCenter();
		Transaction transaction = webRequest.getTransaction();
		transaction.generateUniqueCode();
		transaction.setType(TransactionType.TRANS_IN);
		transaction.setHealthCenter(masterHealthCenter);
		return transaction;
	}
}
