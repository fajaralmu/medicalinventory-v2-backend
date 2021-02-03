package com.fajar.medicalinventory.service.transaction;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
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
	@Autowired
	private SessionFactory sessionFactory;

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
		final PageRequest pageReuqest = PageRequest.of(page, size);
		boolean ignoreEmptyValue = webRequest.getFilter().isIgnoreEmptyValue();
		final BigInteger totalData;
		final List<Product> products;
		if (ignoreEmptyValue) {
			if (isMasterHealthCenter) {
				products = productRepository.findNotEmptyProductInMasterWarehouse(pageReuqest);
				totalData = productRepository.countNotEmptyProductInMasterWareHouse();
			} else {
				products = productRepository.findNotEmptyProductInSpecifiedWarehouse(healthCenter.getId(), pageReuqest);
				totalData = productRepository.countNotEmptyProductInSpecifiedWareHouse(healthCenter.getId());
			}
		} else {
			products = productRepository.findByOrderByName(pageReuqest);
			totalData = productRepository.countAll();
		}
		 
		 
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

	public int getProductStockAtDate(Product product, HealthCenter location, Date date) {
		boolean isMasterLocation = healthCenterMasterService.isMasterHealthCenter(location);

		BigInteger tptalSupplied;
		BigInteger totalUsed;
		if (isMasterLocation) {
			tptalSupplied = productFlowRepository.getTotalIncomingProductFromSupplier(product.getId(), date);
		} else {
			tptalSupplied = productFlowRepository.getTotalIncomingProductAtBranchWarehouse(product.getId(), date,
					location.getId());
		}

		totalUsed = productFlowRepository.getTotalUsedProductToCustomer(product.getId(), date, location.getId());
		int stock = (tptalSupplied == null ? 0 : tptalSupplied.intValue())
				- (totalUsed == null ? 0 : totalUsed.intValue());
		return stock;
	}
	
	public WebResponse adjustStock(HttpServletRequest httpServletRequest) {
		Map<Long, ProductFlow> productFlowMap = new HashMap<Long, ProductFlow>();
		Transaction tx = null;
		Session session = sessionFactory.openSession();
		try {
			
			tx = session.beginTransaction();
			Criteria criteriaSupply = session.createCriteria(ProductFlow.class);
			criteriaSupply.add(Restrictions.isNull("referenceProductFlow"));
			List productSupplyFlows = criteriaSupply.list();
			for (Object productSupplyFlow : productSupplyFlows) {
				ProductFlow pf = (ProductFlow) productSupplyFlow;
				pf.resetUsedCount();
				productFlowMap.put(pf.getId(), pf);
			}
			
			progressService.sendProgress(10, httpServletRequest);
			
			Criteria criteriaUsed = session.createCriteria(ProductFlow.class);
			criteriaUsed.add(Restrictions.isNotNull("referenceProductFlow"));
			List productUsedFlows = criteriaSupply.list();
			
			progressService.sendProgress(10, httpServletRequest);
			
			for (Object object : productUsedFlows) {
				ProductFlow pf = (ProductFlow) object;
				productFlowMap.get(pf.getReferenceProductFlow().getId()).addUsedCount(pf.getCount());
				
				progressService.sendProgress(1 , productUsedFlows.size(), 35, httpServletRequest);
			}
			for(Long id: productFlowMap.keySet()) {
				session.merge(productFlowMap.get(id));
				
				progressService.sendProgress(1 , productFlowMap.keySet().size(), 35, httpServletRequest);
			}
			tx.commit();
			progressService.sendProgress(10, httpServletRequest);
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
		} finally {
			session.close();
			progressService.sendComplete(httpServletRequest);
		}
		return new WebResponse();
	}
}
