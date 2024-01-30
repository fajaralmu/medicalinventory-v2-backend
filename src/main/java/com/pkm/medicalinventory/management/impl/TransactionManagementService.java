package com.pkm.medicalinventory.management.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.pkm.medicalinventory.constants.TransactionType;
import com.pkm.medicalinventory.entity.ProductFlow;
import com.pkm.medicalinventory.entity.Transaction;
import com.pkm.medicalinventory.entity.User;
import com.pkm.medicalinventory.repository.readonly.ProductFlowRepository;
import com.pkm.medicalinventory.util.EntityUtil;

@Service
public class TransactionManagementService extends ManagementService<Transaction> {

	@Autowired
	private ProductFlowRepository productFlowRepository;

	@Override
	public Transaction saveEntity(Transaction object, boolean newRecord)  {
		Transaction record = entityRepository.findById(Transaction.class, object.getId());
		Assert.notNull(record, "Record not found");
		validateType(record.getType(), object);
		modify(object, record);

		return entityRepository.save(record);

	}

	private static void validateType(TransactionType type, Transaction object) {
		
		if (type.equals(TransactionType.TRANS_IN)) {
			Assert.notNull(object.getSupplier(), "Supplier not found for TRANS_IN");
			object.setCustomer(null);
			object.setHealthCenterDestination(null);
		}
		if (type.equals(TransactionType.TRANS_OUT)) {
			Assert.notNull(object.getCustomer(), "Customer not found for TRANS_OUT");
			object.setSupplier(null);
			object.setHealthCenterDestination(null);
		}
		if (type.equals(TransactionType.TRANS_OUT_TO_WAREHOUSE)) {
			Assert.notNull(object.getHealthCenterDestination(), "HealthCenterDestination not found");
			object.setSupplier(null);
			object.setCustomer(null);
		}
		 

	}

	private static void modify(Transaction source, Transaction target) {
		EntityUtil.copyProperties(source,
				                  target,
				                  true,
				                  "transactionDate",
				                  "description",
				                  "customer",
				                  "supplier",
				                  "healthCenterDestination");
	}

	public static void main(String[] args) throws Exception {
		User u = User.builder().displayName("FAJAR AM").username("FAJAR").build();
		User u2 = User.builder().displayName("FAJAR AM v2").username("FAJAR_v2").build();
		EntityUtil.copyProperties(u2, u, true, "username");
		System.out.println(u);
	}

	@Override
	public Transaction deleteEntity(Long id, Class _class){
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
