package com.fajar.medicalinventory.service.transaction;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.dto.Filter;
import com.fajar.medicalinventory.dto.ProductStock;
import com.fajar.medicalinventory.dto.WebRequest;
import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.dto.model.BaseModel;
import com.fajar.medicalinventory.dto.model.ConfigurationModel;
import com.fajar.medicalinventory.entity.HealthCenter;
import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.exception.DataNotFoundException;
import com.fajar.medicalinventory.repository.HealthCenterRepository;
import com.fajar.medicalinventory.repository.ProductFlowRepository;
import com.fajar.medicalinventory.repository.ProductRepository;
import com.fajar.medicalinventory.service.ProgressService;
import com.fajar.medicalinventory.service.config.DefaultApplicationProfileService;
import com.fajar.medicalinventory.service.config.DefaultHealthCenterMasterService;
import com.fajar.medicalinventory.service.config.InventoryConfigurationService;

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
	private InventoryConfigurationService inventoryConfigurationService;
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
		response.setEntities(BaseModel.toModels(availableProductFlows));
		return response;
	}

	public WebResponse getProducts(WebRequest webRequest, HttpServletRequest httpServletRequest) {

		final HealthCenter location = webRequest.getHealthcenter().toEntity();
		final boolean isMasterHealthCenter = healthCenterMasterService.isMasterHealthCenter(location);
		
		final Filter filter = webRequest.getFilter();
		final PageRequest pageReuqest = PageRequest.of(filter.getPage(), filter.getLimit());
		final boolean ignoreEmptyValue = webRequest.getFilter().isIgnoreEmptyValue();
		final Integer expDateWithin = filter.isFilterExpDate()?filter.getDay():null;
		final BigInteger totalData = productRepository.countNontEmptyProduct(isMasterHealthCenter, ignoreEmptyValue, expDateWithin ,
				location.getId());
		final List<Product> products = productRepository.getAvailableProducts(ignoreEmptyValue, isMasterHealthCenter, expDateWithin,
				location.getId(), pageReuqest);
		
		progressService.sendProgress(20, httpServletRequest);

		List<ProductStock> productStocks = new ArrayList<ProductStock>();
		for (int i = 0; i < products.size(); i++) {
			Product product = products.get(i);
			List<ProductFlow> productFlows;

			if (isMasterHealthCenter) {
				productFlows = productFlowRepository.findAvailabeProductsAtMainWareHouse(product.getId(), expDateWithin);

			} else {
				productFlows = productFlowRepository.findAvailabeProductsAtBranchWareHouse(location.getId(),
						product.getId(), expDateWithin);

			}
			ProductStock productStock = new ProductStock(product, productFlows);
			productStocks.add(productStock);
			progressService.sendProgress(1, products.size(), 80, httpServletRequest);
		}

		WebResponse response = new WebResponse();

		ConfigurationModel configModel = inventoryConfigurationService.getTempConfiguration().toModel();
		response.setConfiguration(configModel );
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
		Transaction tx = null;
		Session session = sessionFactory.openSession();
		try {

			tx = session.beginTransaction();

			Map<Long, ProductFlow> productFlowMap = getSupplyFlowReseted(session);
			progressService.sendProgress(10, httpServletRequest);

			List productUsedFlows = getDistributedFlow(session);
			progressService.sendProgress(10, httpServletRequest);

			for (Object object : productUsedFlows) {
				ProductFlow pf = (ProductFlow) object;
				Long refId = pf.getReferenceProductFlow().getId();
				productFlowMap.get(refId).addUsedCount(pf.getCount());
				pf.setExpDate();
				session.merge(pf);
				progressService.sendProgress(1, productUsedFlows.size(), 35, httpServletRequest);
			}
			for (Long id : productFlowMap.keySet()) {
				session.merge(productFlowMap.get(id));

				progressService.sendProgress(1, productFlowMap.keySet().size(), 35, httpServletRequest);
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

	private List getDistributedFlow(Session session) {
		Criteria criteriaUsed = session.createCriteria(ProductFlow.class);
		criteriaUsed.add(Restrictions.isNotNull("referenceProductFlow"));
		List productUsedFlows = criteriaUsed.list();
		return productUsedFlows;
	}

	private Map<Long, ProductFlow> getSupplyFlowReseted(Session session) {
		Query query = session.createQuery(
				"select pf from ProductFlow pf left join pf.transaction tx " + " where tx.type = ? or tx.type = ? ");

		query.setString(0, TransactionType.TRANS_IN.toString());
		query.setString(1, TransactionType.TRANS_OUT_TO_WAREHOUSE.toString());
		List productSupplyFlows = query.list();
		Map<Long, ProductFlow> productFlowMap = new HashMap<>();
		for (Object productSupplyFlow : productSupplyFlows) {
			ProductFlow pf = (ProductFlow) productSupplyFlow;
			pf.resetUsedCount();
			productFlowMap.put(pf.getId(), pf);
		}
		return productFlowMap;
	}
}
