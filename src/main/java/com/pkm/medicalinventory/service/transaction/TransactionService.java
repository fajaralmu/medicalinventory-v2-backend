package com.pkm.medicalinventory.service.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.dto.model.ProductFlowModel;
import com.pkm.medicalinventory.dto.model.TransactionModel;
import com.pkm.medicalinventory.entity.HealthCenter;
import com.pkm.medicalinventory.entity.ProductFlow;
import com.pkm.medicalinventory.entity.Transaction;
import com.pkm.medicalinventory.exception.ApplicationException;
import com.pkm.medicalinventory.exception.DataNotFoundException;
import com.pkm.medicalinventory.repository.DatabaseProcessor;
import com.pkm.medicalinventory.repository.HealthCenterRepository;
import com.pkm.medicalinventory.repository.ProductFlowRepository;
import com.pkm.medicalinventory.repository.TransactionRepository;
import com.pkm.medicalinventory.service.ProgressService;
import com.pkm.medicalinventory.service.SessionValidationService;
import com.pkm.medicalinventory.service.config.DefaultHealthCenterMasterService;
import com.pkm.medicalinventory.service.inventory.InventoryService;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
	@Autowired
	private InventoryService inventoryService;
	
	public WebResponse getTransactionByCode(String code) {
		Transaction transaction = transactionRepository.findByCode(code);
		if (null == transaction) {
			throw new DataNotFoundException("transaction not found");
		}
		List<ProductFlow> productFlows = productFlowRepository.findByTransaction(transaction);
		transaction.setProductFlows(productFlows);
		
		WebResponse response = new WebResponse();
		response.setTransaction(transaction.toModel());
		return response ;
	}

	public synchronized WebResponse performTransactionSupply(WebRequest webRequest, HttpServletRequest httpServletRequest) {
		Session session = sessionFactory.openSession();
		org.hibernate.Transaction hibernateTransaction = session.beginTransaction();
		try {
			 
			final Transaction transaction = buildTransactionSupply(webRequest, httpServletRequest);
			List<ProductFlow> productFlows = transaction.getProductFlows();
			progressService.sendProgress(10, httpServletRequest);
			
			if (null == transaction.getSupplier()) {
				throw new DataNotFoundException("Supplier Missing");
			}			
			log.info("transaction (supply) items: {}", productFlows.size());
			if (productFlows.size() == 0) {
				throw ApplicationException.fromMessage("Transaction item is empty!");
			}
			
			Transaction savedTransaction = DatabaseProcessor.save(transaction, session);
			
			log.info("saved trx items: {}", transaction.getProductFlows().size());
			
			progressService.sendProgress(10, httpServletRequest);
			
			
			for (ProductFlow productFlow : productFlows) {
				
				productFlow.setId(null);
				productFlow.setTransaction(savedTransaction);
				DatabaseProcessor.save(productFlow, session);
				
				progressService.sendProgress(1, productFlows.size(), 80, httpServletRequest);
			}
			hibernateTransaction.commit();
			
			WebResponse response = new WebResponse();
			
			transaction.setProductFlowsTransactionNull();
			response.setTransaction(transaction.toModel());
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			if (null != hibernateTransaction) {
				hibernateTransaction.rollback();
			}
			throw new ApplicationException(e);
		} finally {
			session.close();
		}
	}
	
	public static void main(String[] args) {
		TransactionModel model = new TransactionModel();
		List<ProductFlowModel> productFlows = new ArrayList<ProductFlowModel>();
		productFlows.add(ProductFlowModel.builder().count(111).build());
		model.setProductFlows(productFlows );
		Transaction e = model.toEntity();
		System.out.println(e.getProductFlows());
	}

	private Transaction buildTransactionSupply(WebRequest webRequest, HttpServletRequest httpServletRequest) {
		HealthCenter masterHealthCenter = defaultHealthCenterMasterService.getMasterHealthCenter();
		Transaction transaction = webRequest.getTransaction().toEntity();
		
		transaction.setId(null);
		transaction.setUser(sessionValidationService.getLoggedUser(httpServletRequest));
		transaction.setTypeAndCode();
		transaction.setHealthCenterLocation(masterHealthCenter);
		return transaction;
	}
	
	private Transaction buidTransactionDistribution(WebRequest webRequest, HttpServletRequest httpServletRequest) {
		
		Transaction transaction = webRequest.getTransaction().toEntity();
		 
		Optional<HealthCenter> locationOptional = healthCenterRepository.findById(transaction.getHealthCenterLocation().getId());
		if (locationOptional.isPresent() == false) {
			throw new DataNotFoundException("Location not found");
		}
		transaction.setId(null);
		transaction.setUser(sessionValidationService.getLoggedUser(httpServletRequest));
		transaction.setTypeAndCode();
		 
		return transaction;
	}

	
	public synchronized WebResponse performDistribution(WebRequest webRequest, HttpServletRequest httpServletRequest) {
		Session session = sessionFactory.openSession();
		org.hibernate.Transaction hibernateTransaction = session.beginTransaction();
		try {
			 
			final Transaction transaction = buidTransactionDistribution(webRequest, httpServletRequest);
			List<ProductFlow> productFlows = transaction.getProductFlows();
			
			progressService.sendProgress(10, httpServletRequest);
			
			if (null == transaction.getCustomer() && transaction.getHealthCenterDestination() == null) {
				throw ApplicationException.fromMessage("Fields Missing");
			}
			log.info("transaction (distribution) items: {}", productFlows.size());
			if (productFlows.size() == 0) {
				throw ApplicationException.fromMessage("Transaction item is empty!");
			}
			Transaction savedTransaction = DatabaseProcessor.save(transaction, session);
			
			progressService.sendProgress(10, httpServletRequest);
			
			
			for (ProductFlow productFlow : productFlows) {
				if (productFlow.getReferenceProductFlow() == null) {
					throw ApplicationException.fromMessage("Reference flow does not exist in the request");
				}
				
				ProductFlow referenceFlow = getReferenceFlow(session, productFlow);
				if (null == referenceFlow) {
					throw ApplicationException.fromMessage("Reference flow does not exist in the DB");
				}
				
				productFlow.setId(null);
				productFlow.setTransaction(savedTransaction);
				productFlow.setReferenceProductFlow(referenceFlow);				
				referenceFlow.addUsedCount(productFlow.getCount());
				
				DatabaseProcessor.save(referenceFlow, session);
				DatabaseProcessor.save(productFlow, session);
				
				progressService.sendProgress(1, productFlows.size(), 80, httpServletRequest);
			}
			hibernateTransaction.commit();
			
			WebResponse response = new WebResponse();
			
			transaction.setProductFlowsTransactionNull();
			response.setTransaction(transaction.toModel());
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			if (null != hibernateTransaction) {
				hibernateTransaction.rollback();
			}
			throw new ApplicationException(e);
		} finally {
			session.close();
		}
	}

	private ProductFlow getReferenceFlow(Session session, ProductFlow productFlow) {
		Object record = session.get(ProductFlow.class, productFlow.getReferenceProductFlow().getId());
		return (ProductFlow) record;
	}

	public synchronized WebResponse deleteRecordByCode(String code) {
		Session session = sessionFactory.openSession();
		org.hibernate.Transaction hibernateTransaction = session.beginTransaction();
		try {
			
			Transaction record = transactionRepository.findByCode(code);
			if (null == record) {
				throw new Exception("Record not found");
			}
			List<ProductFlow> productFlows = productFlowRepository.findByTransaction(record);
			for (ProductFlow productFlow : productFlows) {
				session.delete(productFlow);
			}
			session.delete(record);
			hibernateTransaction.commit();

			inventoryService.adjustStock(null);
			return new WebResponse();
		}catch (Exception e) {

			if (null != hibernateTransaction) {
				hibernateTransaction.rollback();
			}
			throw new ApplicationException(e);
		} finally {
			session.close();
		}
	}

	
}
