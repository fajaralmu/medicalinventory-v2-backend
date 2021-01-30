package com.fajar.medicalinventory.service.transaction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.repository.ProductFlowRepository;

@Service
public class InventoryService {

	@Autowired
	private ProductFlowRepository productFlowRepository;
	
	public WebResponse getAvailableProducts(String code) {
		
		WebResponse response = new WebResponse();
		List<ProductFlow> availableProductFlows = productFlowRepository.findByProduct_codeAndTransaction_type(code, TransactionType.TRANS_IN);
		response.setEntities(availableProductFlows);
		return response ;
	}
}
