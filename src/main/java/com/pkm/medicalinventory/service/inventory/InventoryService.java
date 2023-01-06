package com.pkm.medicalinventory.service.inventory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.pkm.medicalinventory.dto.Filter;
import com.pkm.medicalinventory.dto.InventoryData;
import com.pkm.medicalinventory.dto.ProductInventory;
import com.pkm.medicalinventory.dto.ProductStock;
import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.dto.model.BaseModel;
import com.pkm.medicalinventory.dto.model.ConfigurationModel;
import com.pkm.medicalinventory.entity.Configuration;
import com.pkm.medicalinventory.entity.HealthCenter;
import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.entity.ProductFlow;
import com.pkm.medicalinventory.exception.DataNotFoundException;
import com.pkm.medicalinventory.repository.HealthCenterRepository;
import com.pkm.medicalinventory.repository.ProductFlowRepository;
import com.pkm.medicalinventory.repository.ProductRepository;
import com.pkm.medicalinventory.service.ProgressService;
import com.pkm.medicalinventory.service.config.DefaultHealthCenterMasterService;
import com.pkm.medicalinventory.service.config.InventoryConfigurationService;
import com.pkm.medicalinventory.service.entity.CommonFilterResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InventoryService {

	@Autowired
	private ProductFlowRepository productFlowRepository;
	@Autowired
	private ProductStockRepositoryv2 productStockRepository;
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
	private StockControlService stockAdjusterService;
	@Autowired
	private ProductAvailabilityRepository productAvailabilityRepository;

	public WebResponse getAvailableProductStocks(String code, WebRequest webRequest) {
		log.info("get Available Product Stocks {}", code);
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

	public WebResponse getProductStocks(WebRequest webRequest, HttpServletRequest httpServletRequest) {
		if (!webRequest.getFilter().isAllFlag()) {
			healthCenterMasterService.checkLocationRecord(webRequest.getHealthcenter().toEntity());
		}
		final HealthCenter location = webRequest.getHealthcenter().toEntity();
		final boolean isMasterHealthCenter = healthCenterMasterService.isMasterHealthCenter(location);
		final Filter filter = webRequest.getFilter();
		final List<Product> products;

		if (filter.isAllFlag()) {
			products = productAvailabilityRepository.getAvailableProductsAllLocation(filter);
		} else {
			products = productAvailabilityRepository.getAvailableProducts(isMasterHealthCenter, filter, location.getId());
		}

		List<ProductStock> productStocks = new ArrayList<ProductStock>(); 
		
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
			totalItems = productStockRepository.getTotalItemsAllLocation(expDaysWithin(filter));
		} else {
			if (isMasterHealthCenter) {
				totalItems = productStockRepository.getTotalItemsWillExpireAtMasterWarehouse(expDaysWithin(filter));
			} else {
				totalItems = productStockRepository.getTotalItemsWillExpireAtBranchWarehouse(location.getId(),
						expDaysWithin(filter));
			}
		}
		return totalItems == null ? BigInteger.ZERO : totalItems;
	}

	private BigInteger getTotalProduct(boolean isMasterHealthCenter, Filter filter, HealthCenter location) {
		final BigInteger totalData;
		if (filter.isIgnoreEmptyValue()) {
			if (filter.isAllFlag()) {
				totalData = productAvailabilityRepository.countNontEmptyProductAllLocation(isMasterHealthCenter,
						expDaysWithin(filter), filter);
			} else {
				totalData = productAvailabilityRepository.countNontEmptyProduct(isMasterHealthCenter, expDaysWithin(filter),
						filter, location.getId());
			}
		} else {
			String filterProductName = filter.getFieldsFilterValue("name")==null?"":filter.getFieldsFilterValue("name").toString();
			totalData = productRepository.countWhereNameLowerCaseLike(filterProductName.toLowerCase());
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
			totalUsed = productFlowRepository.getTotalUsedProductToCustomerOrBranchWarehouseAtDate(product.getId(), date, location.getId());
		} else {
			tptalSupplied = productFlowRepository.getTotalIncomingProductAtBranchWarehouse(product.getId(), date,
					location.getId());
			totalUsed = productFlowRepository.getTotalUsedProductToCustomerAtDate(product.getId(), date, location.getId());
		}

		
		int stock = (tptalSupplied == null ? 0 : tptalSupplied.intValue())
				- (totalUsed == null ? 0 : totalUsed.intValue());
		return stock;
	}
	/**
	 * 
	 * @param products
	 * @param location
	 * @param date
	 * @return map of product id and remaining stocks
	 */
	public Map<Long, Integer> getProductsStockAtDate(List<Product> products, HealthCenter location, Date date) {
		boolean isMasterLocation = healthCenterMasterService.isMasterHealthCenter(location);
		Map<Long, Integer> result = ProductUsageService.getMapPopulatedWithKey(products);
		
		List<Object[]> totalSupplied;
		List<Object[]> totalUsed;
		if (isMasterLocation) {
			totalSupplied = productFlowRepository.getTotalIncomingProductsFromSupplier(products, date);
			totalUsed = productFlowRepository.getTotalUsedProductsToCustomerOrBranchWarehouseAtDate(products, date, location.getId());
		} else {
			totalSupplied = productFlowRepository.getTotalIncomingProductsAtBranchWarehouse(products, date,
					location.getId());
			totalUsed = productFlowRepository.getTotalUsedProductsToCustomerAtDate(products, date, location.getId());
		}
		if (null == totalSupplied) {
			return result;
		}
		//populate supplied
		for (Object[] objects : totalSupplied) {
			Long productId = Long.parseLong(objects[0].toString());
			Integer suppliedCount = Integer.parseInt(objects[1].toString());
			result.put(productId, suppliedCount);
		}
		if (null != totalUsed)
			for (Object[] objects : totalUsed) {
				Long productId = Long.parseLong(objects[0].toString());
				Integer usedCount = Integer.parseInt(objects[1].toString());
				Integer supplied = result.get(productId);
				Integer stock = supplied - usedCount;
				result.put(productId, stock);
			}
		 
//		int stock = (tptalSupplied == null ? 0 : tptalSupplied.intValue())
//				- (totalUsed == null ? 0 : totalUsed.intValue());
		Integer supplied = totalSupplied.stream().map(t -> Integer.parseInt(t[1].toString())).collect(Collectors.summingInt(i -> i));
		Integer used = totalUsed == null ? 0 : totalUsed.stream().map(t -> Integer.parseInt(t[1].toString())).collect(Collectors.summingInt(i -> i));
		log.info("Sum Stock at {}: {} - {} = {}", date, supplied, used, supplied - used);;
		return result;
	}

	public synchronized WebResponse adjustStock(HttpServletRequest httpServletRequest) {
		 return stockAdjusterService.adjustStock(httpServletRequest);
	}

	private List<ProductInventory> getExpiringProductsData(List<HealthCenter> locations, Integer remainingDays,
			HttpServletRequest httpServletRequest) {

		List<ProductInventory> inventories = new ArrayList<>();
		for (HealthCenter location : locations) {
			BigInteger totalItems;
			if (healthCenterMasterService.isMasterHealthCenter(location)) {
				totalItems = productStockRepository.getTotalItemsWillExpireAtMasterWarehouse(remainingDays);
			} else {
				totalItems = productStockRepository.getTotalItemsWillExpireAtBranchWarehouse(location.getId(), remainingDays);
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
	
	public WebResponse filterStock(WebRequest webRequest) {
		log.info("filterStock: {}", webRequest.getFilter());
		
		CommonFilterResult<ProductFlow> result = productStockRepository.filter(webRequest.getFilter());
		Configuration config = inventoryConfigurationService.getTempConfiguration();
		
		WebResponse response = new WebResponse();
		response.setEntities(BaseModel.toModels(result.getEntities()));
		response.setTotalData(result.getCount());
		response.setFilter(webRequest.getFilter());
		response.setConfiguration(config.toModel());
		return response;
	}

	

}
