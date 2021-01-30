package com.fajar.medicalinventory.service.transaction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.dto.WebRequest;
import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.entity.HealthCenter;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.exception.DataNotFoundException;
import com.fajar.medicalinventory.repository.HealthCenterRepository;
import com.fajar.medicalinventory.repository.ProductFlowRepository;

@Service
public class InventoryService {

	@Autowired
	private ProductFlowRepository productFlowRepository;
	@Autowired
	private HealthCenterRepository healthCenterRepository;
	
	public WebResponse getAvailableProducts(String code, WebRequest webRequest) {
		HealthCenter healthCenter = healthCenterRepository.findTop1ByCode(webRequest.getHealthcenter().getCode());
		if (null == healthCenter) {
			throw new DataNotFoundException("Health center not found");
		}
		WebResponse response = new WebResponse();
		List<ProductFlow> availableProductFlows = productFlowRepository.findAvailabeProductsForTransactionIN(code);
		response.setEntities(availableProductFlows);
		return response ;
	}
}
