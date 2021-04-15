package com.fajar.medicalinventory.service.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.entity.Transaction;
import com.fajar.medicalinventory.entity.User;
import com.fajar.medicalinventory.repository.ProductFlowRepository;
import com.fajar.medicalinventory.util.EntityUtil;

@Service
public class TransactionUpdateService extends BaseEntityUpdateService<Transaction> {

	@Autowired
	private ProductFlowRepository productFlowRepository;

	@Override
	public Transaction saveEntity(Transaction object, boolean newRecord, HttpServletRequest httpServletRequest)
			throws Exception {
		Transaction record = entityRepository.findById(Transaction.class, object.getId());
		Assert.notNull(record, "Record not found");
		
		modify(object, record);
		
		return entityRepository.save(record);
		
	}
	
	private static void modify(Transaction source, Transaction target) throws Exception {
		EntityUtil.copyProperties(source, target, true, 
				"transactionDate", "description");//, "customer", "supplier");
		  
		
	}
	
	public static void main(String[] args) throws Exception {
		User u =User.builder().displayName("FAJAR AM").username("FAJAR").build();
		User u2 =User.builder().displayName("FAJAR AM v2").username("FAJAR_v2").build();
		EntityUtil.copyProperties(u2, u, true, "username");
		System.out.println(u);
	}
	
	@Override
	public WebResponse deleteEntity(Long id, Class _class, HttpServletRequest httpServletRequest) throws Exception {
		throw new NotImplementedException("Not Allowed");
	}

	@Override
	public void postFilter(List<Transaction> objects) {
		if (null == objects || objects.size() == 0) {
			return;
		}
		
		Map<Long, List<ProductFlow>> mappedProductFlow = new HashMap<Long, List<ProductFlow>>();
		List<ProductFlow> productFlows = productFlowRepository.findByTransactionIn(objects);
		for (ProductFlow productFlow : productFlows) {
			Long transactionId = productFlow.getTransactionId();
			if (null == mappedProductFlow.get(transactionId)) {
				mappedProductFlow.put(transactionId, new ArrayList<ProductFlow>());
			}
			mappedProductFlow.get(transactionId).add(productFlow);
		}
		for (Transaction transaction : objects) {
			transaction.setProductFlows(mappedProductFlow.get(transaction.getId()));
		}
	}
}
