package com.fajar.medicalinventory.service.transaction;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.dto.WebRequest;
import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.entity.HealthCenter;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.entity.Transaction;
import com.fajar.medicalinventory.exception.ApplicationException;
import com.fajar.medicalinventory.exception.DataNotFoundException;
import com.fajar.medicalinventory.repository.DatabaseProcessor;
import com.fajar.medicalinventory.repository.HealthCenterRepository;
import com.fajar.medicalinventory.repository.ProductFlowRepository;
import com.fajar.medicalinventory.repository.TransactionRepository;
import com.fajar.medicalinventory.service.ProgressService;
import com.fajar.medicalinventory.service.SessionValidationService;
import com.fajar.medicalinventory.service.config.DefaultHealthCenterMasterService;

@Service
public class TransactionService {

	@Autowired
	private ProgressService progressService;
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private SessionValidationService sessionValidationService;
	@Autowired
	private DefaultHealthCenterMasterService defaultHealthCenterMasterService;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private ProductFlowRepository productFlowRepository;
	@Autowired
	private HealthCenterRepository healthCenterRepository;
	
	public WebResponse getTransactionByCode(String code) {
		Transaction transaction = transactionRepository.findByCode(code);
		if (null == transaction) {
			throw new DataNotFoundException("transaction not found");
		}
		List<ProductFlow> productFlows = productFlowRepository.findByTransaction(transaction);
		transaction.setProductFlows(productFlows);
		
		WebResponse response = new WebResponse();
		response.setTransaction(transaction);
		return response ;
	}

	public WebResponse performTransactionIN(WebRequest webRequest, HttpServletRequest httpServletRequest) {
		Session session = sessionFactory.openSession();
		org.hibernate.Transaction hibernateTransaction = session.beginTransaction();
		try {
			 
			Transaction transaction = buildTransactionINObject(webRequest, httpServletRequest);
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
			transaction.setProductFlowsTransactionNull();
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

	private Transaction buildTransactionINObject(WebRequest webRequest, HttpServletRequest httpServletRequest) {
		HealthCenter masterHealthCenter = defaultHealthCenterMasterService.getMasterHealthCenter();
		Transaction transaction = webRequest.getTransaction();
		transaction.setUser(sessionValidationService.getLoggedUser(httpServletRequest));
		transaction.setTypeAndCode();
		transaction.setHealthCenterLocation(masterHealthCenter);
		return transaction;
	}
	
	private Transaction buildTransactionOUTObject(WebRequest webRequest, HttpServletRequest httpServletRequest) {
		
		Transaction transaction = webRequest.getTransaction();
		 
		Optional<HealthCenter> locationOptional = healthCenterRepository.findById(transaction.getHealthCenterLocation().getId());
		if (locationOptional.isPresent() == false) {
			throw new DataNotFoundException("Location not found");
		}
		
		transaction.setUser(sessionValidationService.getLoggedUser(httpServletRequest));
		transaction.setTypeAndCode();
		 
		return transaction;
	}

	
	public WebResponse performTransactionOUT(WebRequest webRequest, HttpServletRequest httpServletRequest) {
		Session session = sessionFactory.openSession();
		org.hibernate.Transaction hibernateTransaction = session.beginTransaction();
		try {
			 
			Transaction transaction = buildTransactionOUTObject(webRequest, httpServletRequest);
			progressService.sendProgress(10, httpServletRequest);
			
			if (null == transaction.getCustomer() && transaction.getHealthCenterDestination() == null) {
				throw new ApplicationException("Fields Missing");
			}
			transaction = DatabaseProcessor.save(transaction, session);
			
			progressService.sendProgress(10, httpServletRequest);
			List<ProductFlow> productFlows = transaction.getProductFlows();
			for (ProductFlow productFlow : productFlows) {
				if (productFlow.getReferenceProductFlow() == null) {
					throw new ApplicationException("Reference flow does not exist in the request");
				}
				productFlow.setProduct(productFlow.getReferenceProductFlow().getProduct());
				productFlow.setTransaction(transaction);
				ProductFlow referenceFlow = (ProductFlow)session.get(ProductFlow.class, productFlow.getReferenceProductFlow().getId());
				if (null == referenceFlow) {
					throw new ApplicationException("Reference flow does not exist in the DB");
				}
				referenceFlow.addUsedCount(productFlow.getCount());
				
				DatabaseProcessor.save(referenceFlow, session);
				DatabaseProcessor.save(productFlow, session);
				
				progressService.sendProgress(1, productFlows.size(), 80, httpServletRequest);
			}
			hibernateTransaction.commit();
			
			WebResponse response = new WebResponse();
			transaction.setProductFlowsTransactionNull();
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

	
}
