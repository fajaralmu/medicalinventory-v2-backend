package com.fajar.medicalinventory.service.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.entity.Transaction;
import com.fajar.medicalinventory.exception.ApplicationException;
import com.fajar.medicalinventory.repository.ProductFlowRepository;

@Service
public class TransactionUpdateService extends BaseEntityUpdateService<Transaction> {

	@Autowired
	private ProductFlowRepository productFlowRepository;

	@Override
	public Transaction saveEntity(Transaction baseEntity, boolean newRecord, HttpServletRequest httpServletRequest)
			throws Exception {
		throw new ApplicationException("Not Allowed");
	}
	@Override
	public WebResponse deleteEntity(Long id, Class _class, HttpServletRequest httpServletRequest) throws Exception {
		throw new ApplicationException("Not Allowed");
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
