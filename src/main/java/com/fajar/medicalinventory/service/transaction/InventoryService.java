package com.fajar.medicalinventory.service.transaction;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.dto.ProductStock;
import com.fajar.medicalinventory.dto.WebRequest;
import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.entity.HealthCenter;
import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.exception.DataNotFoundException;
import com.fajar.medicalinventory.repository.HealthCenterRepository;
import com.fajar.medicalinventory.repository.ProductFlowRepository;
import com.fajar.medicalinventory.repository.ProductRepository;
import com.fajar.medicalinventory.service.ProgressService;
import com.fajar.medicalinventory.service.config.DefaultHealthCenterMasterService;

@Service
public class InventoryService {

	@Autowired
	private ProductFlowRepository productFlowRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private HealthCenterRepository healthCenterRepository;
	@Autowired
	private DefaultHealthCenterMasterService healthCenterMasterService;
	@Autowired
	private ProgressService progressService;

	public WebResponse getAvailableProducts(String code, WebRequest webRequest) {
		HealthCenter healthCenter = healthCenterRepository.findTop1ByCode(webRequest.getHealthcenter().getCode());
		if (null == healthCenter) {
			throw new DataNotFoundException("Health center not found");
		}
		Product product = productRepository.findTop1ByCode(code);
		if (null == product) {
			throw new DataNotFoundException("Product not found");
		}
		WebResponse response = new WebResponse();
		List<ProductFlow> availableProductFlows;
		
		if (healthCenterMasterService.isMasterHealthCenter(healthCenter)) {
			availableProductFlows = productFlowRepository.findAvailabeProductsAtMainWareHouse(product.getId());

		} else {
			availableProductFlows = productFlowRepository.findAvailabeProductsAtBranchWareHouse(healthCenter.getId(),
					product.getId());

		}
		response.setEntities(availableProductFlows);
		return response;
	}

	public WebResponse getProducts(WebRequest webRequest, HttpServletRequest httpServletRequest) {

		HealthCenter healthCenter = webRequest.getHealthcenter();
		boolean isMasterHealthCenter = healthCenterMasterService.isMasterHealthCenter(healthCenter);

		int page = webRequest.getFilter().getPage();
		int size = webRequest.getFilter().getLimit();
		PageRequest pageReuqest = PageRequest.of(page, size);
		boolean ignoreEmptyValue = webRequest.getFilter().isIgnoreEmptyValue();
		
		List<Product> products;
		if (ignoreEmptyValue) {
			
			if (isMasterHealthCenter) {
				products = productRepository.findNotEmptyProductInMasterWarehouse(pageReuqest);
			} else {
				products = productRepository.findNotEmptyProductInSpecifiedWarehouse(healthCenter.getId(), pageReuqest);
			}
		} else {
			products = productRepository.findByOrderByName(pageReuqest);
		}
		//TODO: count only product available
		BigInteger totalData = productRepository.countAll();
		progressService.sendProgress(20, httpServletRequest);
		
		List<ProductStock> productStocks = new ArrayList<ProductStock>();
		for (int i = 0; i < products.size(); i++) {
			Product product = products.get(i);
			List<ProductFlow> productFlows;
			
			if (isMasterHealthCenter) {
				productFlows = productFlowRepository.findAvailabeProductsAtMainWareHouse(product.getId());

			} else {
				productFlows = productFlowRepository.findAvailabeProductsAtBranchWareHouse(healthCenter.getId(),
						product.getId());

			}
			ProductStock productStock = new ProductStock(product, productFlows);
			productStocks.add(productStock);
			progressService.sendProgress(1, products.size(), 80, httpServletRequest);
		}
		
		WebResponse response = new WebResponse();
		
		response.setTotalData(totalData.intValue());
		response.setGeneralList(productStocks);
		return response;
	}

}
