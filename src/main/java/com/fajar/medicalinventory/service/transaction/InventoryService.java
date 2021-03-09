package com.fajar.medicalinventory.service.transaction;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.math3.ode.events.FilterType;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.constants.FilterFlag;
import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.dto.Filter;
import com.fajar.medicalinventory.dto.InventoryData;
import com.fajar.medicalinventory.dto.ProductInventory;
import com.fajar.medicalinventory.dto.ProductStock;
import com.fajar.medicalinventory.dto.WebRequest;
import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.dto.model.BaseModel;
import com.fajar.medicalinventory.dto.model.ConfigurationModel;
import com.fajar.medicalinventory.entity.Configuration;
import com.fajar.medicalinventory.entity.HealthCenter;
import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.exception.DataNotFoundException;
import com.fajar.medicalinventory.repository.HealthCenterRepository;
import com.fajar.medicalinventory.repository.ProductFlowRepository;
import com.fajar.medicalinventory.repository.ProductRepository;
import com.fajar.medicalinventory.service.ProgressService;
import com.fajar.medicalinventory.service.config.DefaultHealthCenterMasterService;
import com.fajar.medicalinventory.service.config.InventoryConfigurationService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
			availableProductFlows = productFlowRepository.findAvailableStocksAtBranchWareHouse(healthCenter.getId(),
					product.getId());

		}
		response.setEntities(BaseModel.toModels(availableProductFlows));
		return response;
	}

	public WebResponse getProducts(WebRequest webRequest, HttpServletRequest httpServletRequest) {
		if (!webRequest.getFilter().isAllFlag()) {
			healthCenterMasterService.checkLocationRecord(webRequest.getHealthcenter().toEntity());
		}
		final HealthCenter location = webRequest.getHealthcenter().toEntity();
		final boolean isMasterHealthCenter = healthCenterMasterService.isMasterHealthCenter(location);
		final Filter filter = webRequest.getFilter();
		final List<Product> products;

		if (filter.isAllFlag()) {
			products = productRepository.getAvailableProductsAllLocation(filter);
		} else {
			products = productRepository.getAvailableProducts(isMasterHealthCenter, filter, location.getId());
		}

		List<ProductStock> productStocks = new ArrayList<ProductStock>();
//		for (int i = 0; i < products.size(); i++) {
//			Product product = products.get(i);
//			List<ProductFlow> productFlows;
//			if (filter.isAllFlag()) {
//				productFlows = productFlowRepository.findAvailableStocksAllLocation(product.getId(),
//						expDaysWithin(filter));
//			} else {
//				if (isMasterHealthCenter) {
//					productFlows = productFlowRepository.findAvailableStocksAtMainWareHouse(product.getId(),
//							expDaysWithin(filter));
//
//				} else {
//					productFlows = productFlowRepository.findAvailableStocksAtBranchWareHouse(location.getId(),
//							product.getId(), expDaysWithin(filter));
//
//				}
//			}
//			ProductStock productStock = new ProductStock(product.toModel(), productFlows);
//			productStocks.add(productStock);
//			progressService.sendProgress(1, products.size(), 80, httpServletRequest);
//		}
		
		if (products.size() > 0) {
			List<ProductFlow> allProductFlows; 
			if (filter.isAllFlag()) {
				allProductFlows = productFlowRepository.findAvailableStocksAllLocation(products,
						expDaysWithin(filter));
			} else {
				if (isMasterHealthCenter) {
					allProductFlows = productFlowRepository.findAvailableStocksAtMainWareHouse(products,
							expDaysWithin(filter));
	
				} else {
					allProductFlows = productFlowRepository.findAvailableStocksAtBranchWareHouse(location.getId(),
							products, expDaysWithin(filter));
	
				}
			}
			progressService.sendProgress(60, httpServletRequest);
			productStocks.addAll(mapProductFlows(products, allProductFlows));
			progressService.sendProgress(20, httpServletRequest);
		} else {
			progressService.sendProgress(80, httpServletRequest);
		}
		final BigInteger totalData = getTotalProduct(isMasterHealthCenter, filter, location);
		progressService.sendProgress(10, httpServletRequest);
		final BigInteger totalItems = getTotalProductStockRecord(isMasterHealthCenter, filter, location);
		progressService.sendProgress(10, httpServletRequest);

		WebResponse response = new WebResponse();

		ConfigurationModel configModel = inventoryConfigurationService.getTempConfiguration().toModel();
		response.setConfiguration(configModel);
		response.setTotalData(totalData.intValue());
		response.setTotalItems(totalItems == null ? 0 : totalItems.intValue());
		response.setGeneralList(productStocks);
		return response;
	}

	private List<ProductStock> mapProductFlows(List<Product> products, List<ProductFlow> allProductFlows) {
		List<ProductStock> productStocks = new ArrayList<>();
		Map<Long, List<ProductFlow> > map = new LinkedHashMap<>();
		for (Product product : products) {
			map.put(product.getId(), new LinkedList<>());
		}
		for(ProductFlow productFlow:allProductFlows) {
			Long productId = productFlow.getProduct().getId();
			if (null !=map.get(productId)) {
				map.get(productId).add(productFlow);
			}
		}
		for (Product product : products) {
			ProductStock productStock = new ProductStock(product.toModel(), map.get(product.getId()));
			productStocks.add(productStock);
		}
		return productStocks ;
	}

	private BigInteger getTotalProductStockRecord(boolean isMasterHealthCenter, Filter filter, HealthCenter location) {
		BigInteger totalItems;
		if (filter.isAllFlag()) {
			totalItems = productFlowRepository.getTotalItemsAllLocation(expDaysWithin(filter));
		} else {
			if (isMasterHealthCenter) {
				totalItems = productFlowRepository.getTotalItemsAtMasterWarehouse(expDaysWithin(filter));
			} else {
				totalItems = productFlowRepository.getTotalItemsAtBranchWarehouse(location.getId(),
						expDaysWithin(filter));
			}
		}
		return totalItems == null ? BigInteger.ZERO : totalItems;
	}

	private BigInteger getTotalProduct(boolean isMasterHealthCenter, Filter filter, HealthCenter location) {
		final BigInteger totalData;
		if (filter.isIgnoreEmptyValue()) {
			if (filter.isAllFlag()) {
				totalData = productRepository.countNontEmptyProductAllLocation(isMasterHealthCenter,
						expDaysWithin(filter));
			} else {
				totalData = productRepository.countNontEmptyProduct(isMasterHealthCenter, expDaysWithin(filter),
						location.getId());
			}
		} else {
			totalData = productRepository.countAll();
		}
		return totalData == null ? BigInteger.ZERO : totalData;
	}

	private Integer expDaysWithin(Filter filter) {
		final Integer expDaysWithin = filter.isFilterExpDate() ? filter.getDay() : null;
		return expDaysWithin;
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

	private List<ProductInventory> getExpiringProductsData(List<HealthCenter> locations, Integer remainingDays,
			HttpServletRequest httpServletRequest) {

		List<ProductInventory> inventories = new ArrayList<>();
		for (HealthCenter location : locations) {
			BigInteger totalItems;
			if (healthCenterMasterService.isMasterHealthCenter(location)) {
				totalItems = productFlowRepository.getTotalItemsAtMasterWarehouse(remainingDays);
			} else {
				totalItems = productFlowRepository.getTotalItemsAtBranchWarehouse(location.getId(), remainingDays);
			}

			ProductInventory inventory = new ProductInventory();
			inventory.setLocation(location.toModel());

			inventory.setTotalItems(totalItems == null ? 0 : totalItems.intValue());
			inventories.add(inventory);
			progressService.sendProgress(1, locations.size(), 30, httpServletRequest);
		}
		return inventories;
	}

	public WebResponse getInventoriesData(HttpServletRequest httpServletRequest) {
		Configuration config = inventoryConfigurationService.getTempConfiguration();
		int warningDays = config.getExpiredWarningDays();

		List<HealthCenter> locations = healthCenterRepository.findAll();
		progressService.sendProgress(10, httpServletRequest);
		List<ProductInventory> willExpiredList = getExpiringProductsData(locations, warningDays, httpServletRequest);
		List<ProductInventory> expiredList = getExpiringProductsData(locations, 0, httpServletRequest);
		List<ProductInventory> totalList = getExpiringProductsData(locations, null, httpServletRequest);

		InventoryData inventoryData = new InventoryData();
		inventoryData.setInventories(ProductInventory.combine(totalList, willExpiredList, expiredList));
		inventoryData.calculateInventoryStatusSummary();

		WebResponse response = new WebResponse();
		response.setConfiguration(config.toModel());
		response.setInventoryData(inventoryData);
		return response;
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
