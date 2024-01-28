package com.pkm.medicalinventory.inventory.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pkm.medicalinventory.dto.Filter;
import com.pkm.medicalinventory.dto.InventoryData;
import com.pkm.medicalinventory.dto.ProductInventory;
import com.pkm.medicalinventory.dto.ProductStock;
import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.dto.model.BaseModel;
import com.pkm.medicalinventory.dto.model.ConfigurationModel;
import com.pkm.medicalinventory.entity.HealthCenter;
import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.entity.ProductFlow;
import com.pkm.medicalinventory.exception.DataNotFoundException;
import com.pkm.medicalinventory.inventory.InventoryConfigurationService;
import com.pkm.medicalinventory.inventory.InventoryService;
import com.pkm.medicalinventory.inventory.StockControlService;
import com.pkm.medicalinventory.inventory.WarehouseService;
import com.pkm.medicalinventory.inventory.query.ProductAvailabilityRepository;
import com.pkm.medicalinventory.inventory.query.ProductStockRepository;
import com.pkm.medicalinventory.management.impl.CommonFilterResult;
import com.pkm.medicalinventory.repository.main.HealthCenterRepository;
import com.pkm.medicalinventory.repository.readonly.ProductFlowRepository;
import com.pkm.medicalinventory.repository.readonly.ProductRepository;
import com.pkm.medicalinventory.service.ProgressService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InventoryServiceImpl implements InventoryService {
	@Autowired
	private ProductFlowRepository productFlowRepository;
	@Autowired
	private ProductStockRepository productStockRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private HealthCenterRepository healthCenterRepository;
	@Autowired
	private WarehouseService wareHouseService;
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

		if (wareHouseService.isMasterHealthCenter(healthCenter)) {
			availableProductFlows = productFlowRepository.findAvailabeProductsAtMainWareHouse(product.getId());

		} else {
			availableProductFlows = productFlowRepository.findAvailableStocksAtBranchWareHouse(healthCenter.getId(),
					product.getId());

		}
		response.setEntities(BaseModel.toModels(availableProductFlows));
		return response;
	}

	public WebResponse getProductStocks(WebRequest webRequest) {
		if (!webRequest.getFilter().isAllFlag()) {
			wareHouseService.checkLocationRecord(webRequest.getHealthcenter().toEntity());
		}
		final HealthCenter location = webRequest.getHealthcenter().toEntity();
		final boolean isMasterHealthCenter = wareHouseService.isMasterHealthCenter(location);
		final Filter filter = webRequest.getFilter();
		final List<Product> products;

		if (filter.isAllFlag()) {
			products = productAvailabilityRepository.getAvailableProductsAllLocation(filter);
		} else {
			products = productAvailabilityRepository.getAvailableProducts(isMasterHealthCenter, filter,
					location.getId());
		}

		List<ProductStock> productStocks = new ArrayList<ProductStock>();

		if (products.size() > 0) {
			List<ProductFlow> allProductFlows;
			if (filter.isAllFlag()) {
				allProductFlows = productFlowRepository.findAvailableStocksAllLocation(products, expDaysWithin(filter));
			} else {
				if (isMasterHealthCenter) {
					allProductFlows = productFlowRepository.findAvailableStocksAtMainWareHouse(products,
							expDaysWithin(filter));

				} else {
					allProductFlows = productFlowRepository.findAvailableStocksAtBranchWareHouse(location.getId(),
							products, expDaysWithin(filter));

				}
			}
			progressService.sendProgress(60);
			productStocks.addAll(mapProductFlows(products, allProductFlows));
			progressService.sendProgress(20);
		} else {
			progressService.sendProgress(80);
		}
		final BigInteger totalData = getTotalProduct(isMasterHealthCenter, filter, location);
		progressService.sendProgress(10);
		final BigInteger totalItems = getTotalProductStockRecord(isMasterHealthCenter, filter, location);
		progressService.sendProgress(10);

		WebResponse response = new WebResponse();

		ConfigurationModel configModel = inventoryConfigurationService.getConfiguration();
		response.setConfiguration(configModel);
		response.setTotalData(totalData.intValue());
		response.setTotalItems(totalItems == null ? 0 : totalItems.intValue());
		response.setGeneralList(productStocks);
		return response;
	}

	private List<ProductStock> mapProductFlows(List<Product> products, List<ProductFlow> allProductFlows) {
		List<ProductStock> productStocks = new ArrayList<>();
		Map<Long, List<ProductFlow>> map = new LinkedHashMap<>();
		for (Product product : products) {
			map.put(product.getId(), new LinkedList<>());
		}
		for (ProductFlow productFlow : allProductFlows) {
			Long productId = productFlow.getProduct().getId();
			if (null != map.get(productId)) {
				map.get(productId).add(productFlow);
			}
		}
		for (Product product : products) {
			ProductStock productStock = new ProductStock(product.toModel(), map.get(product.getId()));
			productStocks.add(productStock);
		}
		return productStocks;
	}

	private BigInteger getTotalProductStockRecord(
		boolean isMasterHealthCenter,
		Filter filter,
		HealthCenter location
	) {
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
				totalData = productAvailabilityRepository.countNontEmptyProduct(isMasterHealthCenter,
						expDaysWithin(filter), filter, location.getId());
			}
		} else {
			String filterProductName = filter.getFieldsFilterValue("name") == null ? ""
					: filter.getFieldsFilterValue("name").toString();
			totalData = productRepository.countWhereNameLowerCaseLike(filterProductName.toLowerCase());
		}
		return totalData == null ? BigInteger.ZERO : totalData;
	}

	private Integer expDaysWithin(Filter filter) {
		final Integer expDaysWithin = filter.isFilterExpDate() ? filter.getDay() : null;
		return expDaysWithin;
	}

	public int getProductStockAtDate(Product product, HealthCenter location, Date date) {
		boolean isMasterLocation = wareHouseService.isMasterHealthCenter(location);

		BigInteger tptalSupplied;
		BigInteger totalUsed;
		if (isMasterLocation) {
			tptalSupplied = productFlowRepository.getTotalIncomingProductFromSupplier(product.getId(), date);
			totalUsed = productFlowRepository.getTotalUsedProductToCustomerOrBranchWarehouseAtDate(
				product.getId(),
				date,
				location.getId()
			);
		} else {
			tptalSupplied = productFlowRepository.getTotalIncomingProductAtBranchWarehouse(
				product.getId(),
				date,
				location.getId()
			);
			totalUsed = productFlowRepository.getTotalUsedProductToCustomerAtDate(
				product.getId(),
				date,
				location.getId()
			);
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
		boolean isMasterLocation = wareHouseService.isMasterHealthCenter(location);
		Map<Long, Integer> result = getMapPopulatedWithKey(products);

		List<Object[]> totalSupplied;
		List<Object[]> totalUsed;
		if (isMasterLocation) {
			totalSupplied = productFlowRepository.getTotalIncomingProductsFromSupplier(products, date);
			totalUsed = productFlowRepository.getTotalUsedProductsToCustomerOrBranchWarehouseAtDate(products, date,
					location.getId());
		} else {
			totalSupplied = productFlowRepository.getTotalIncomingProductsAtBranchWarehouse(products, date,
					location.getId());
			totalUsed = productFlowRepository.getTotalUsedProductsToCustomerAtDate(products, date, location.getId());
		}
		if (null == totalSupplied) {
			return result;
		}
		// populate supplied
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
		Integer supplied = totalSupplied.stream().map(t -> Integer.parseInt(t[1].toString()))
				.collect(Collectors.summingInt(i -> i));
		Integer used = totalUsed == null ? 0
				: totalUsed.stream().map(t -> Integer.parseInt(t[1].toString())).collect(Collectors.summingInt(i -> i));
		log.info("Sum Stock at {}: {} - {} = {}", date, supplied, used, supplied - used);
		;
		return result;
	}

	public synchronized void adjustStock() {
		stockAdjusterService.adjustStock();
	}

	private List<ProductInventory> getExpiringProductsData(List<HealthCenter> locations, Integer remainingDays) {

		List<ProductInventory> inventories = new ArrayList<>();
		for (HealthCenter location : locations) {
			BigInteger totalItems;
			if (wareHouseService.isMasterHealthCenter(location)) {
				totalItems = productStockRepository.getTotalItemsWillExpireAtMasterWarehouse(remainingDays);
			} else {
				totalItems = productStockRepository.getTotalItemsWillExpireAtBranchWarehouse(location.getId(),
						remainingDays);
			}

			ProductInventory inventory = new ProductInventory();
			inventory.setLocation(location.toModel());

			inventory.setTotalItems(totalItems == null ? 0 : totalItems.intValue());
			inventories.add(inventory);
			progressService.sendProgress(1, locations.size(), 30);
		}
		return inventories;
	}

	public WebResponse getInventoriesData() {
		ConfigurationModel config = inventoryConfigurationService.getConfiguration();
		int warningDays = config.getExpiredWarningDays();

		List<HealthCenter> locations = healthCenterRepository.findAll();
		progressService.sendProgress(10);
		List<ProductInventory> willExpiredList = getExpiringProductsData(locations, warningDays);
		List<ProductInventory> expiredList = getExpiringProductsData(locations, 0);
		List<ProductInventory> totalList = getExpiringProductsData(locations, null);

		InventoryData inventoryData = new InventoryData();
		inventoryData.setInventories(ProductInventory.combine(totalList, willExpiredList, expiredList));
		inventoryData.calculateInventoryStatusSummary();

		WebResponse response = new WebResponse();
		response.setConfiguration(config);
		response.setInventoryData(inventoryData);
		return response;
	}

	public WebResponse filterStock(WebRequest webRequest) {
		log.info("filterStock: {}", webRequest.getFilter());

		CommonFilterResult<ProductFlow> result = productStockRepository.filter(webRequest.getFilter());
		ConfigurationModel config = inventoryConfigurationService.getConfiguration();

		WebResponse response = new WebResponse();
		response.setEntities(BaseModel.toModels(result.getEntities()));
		response.setTotalData(result.getCount());
		response.setFilter(webRequest.getFilter());
		response.setConfiguration(config);
		return response;
	}

	private static Map<Long, Integer> getMapPopulatedWithKey(List<Product> products) {
		Map<Long, Integer> result = new HashMap<>();
		if (null == products || products.isEmpty()) {
			result.put(-1L, 0);
			return result;
		}

		products.forEach(p -> {
			result.put(p.getId(), 0);
		});
		return result;
	}
}
