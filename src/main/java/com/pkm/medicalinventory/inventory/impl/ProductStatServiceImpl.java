package com.pkm.medicalinventory.inventory.impl;

import static com.pkm.medicalinventory.util.DateUtil.cal;
import static com.pkm.medicalinventory.util.DateUtil.getCalendarMonth;
import static com.pkm.medicalinventory.util.DateUtil.getCalendarYear;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pkm.medicalinventory.constants.TransactionType;
import com.pkm.medicalinventory.dto.Filter;
import com.pkm.medicalinventory.dto.InventoryData;
import com.pkm.medicalinventory.dto.PeriodicReviewResult;
import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.dto.model.ProductModel;
import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.entity.ProductFlow;
import com.pkm.medicalinventory.exception.ApplicationException;
import com.pkm.medicalinventory.exception.DataNotFoundException;
import com.pkm.medicalinventory.inventory.StockReviewer;
import com.pkm.medicalinventory.management.MasterDataService;
import com.pkm.medicalinventory.management.impl.CommonFilterResult;
import com.pkm.medicalinventory.repository.readonly.ProductFlowRepository;
import com.pkm.medicalinventory.repository.readonly.ProductRepository;
import com.pkm.medicalinventory.inventory.ProductStatisticService;
import com.pkm.medicalinventory.service.ProgressService;
import com.pkm.medicalinventory.util.DateUtil;
import com.pkm.medicalinventory.util.MapUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductStatServiceImpl implements ProductStatisticService {

	@Autowired
	private ProgressService progressService;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductFlowRepository productFlowRepository;
	@Autowired
	private StockReviewer stockReviewer;
	@Autowired
	private MasterDataService masterDataService;

	public WebResponse getProductUsageByCode(String productCode, WebRequest webRequest) {

		Product product = productRepository.findTop1ByCode(productCode);
		if (null == product) {
			throw new DataNotFoundException("Product with code: " + productCode + " not found");
		}

		Date startDate = webRequest.getFilter().getStartPeriodByYYMM();
		Date endDate = webRequest.getFilter().getEndPeriodByYYMM();
		progressService.sendProgress(30);

		if (startDate.after(endDate)) {
			throw new ApplicationException(new Exception("invalid period"));
		}

		log.info("startDate : {}, endDate: {}", startDate, endDate);
		List<ProductFlow> productFlows = productFlowRepository.getByTransactionTypeAndDateBetween(productCode,
				TransactionType.TRANS_OUT, startDate, endDate);
		List<InventoryData> usageData = getUsageData(productFlows, startDate, endDate);
		progressService.sendProgress(30);

		int totalData = ProductFlow.sumQtyCount(productFlows);
		InventoryData inventoryData = calculatePeriodicReview(product, usageData);
		progressService.sendProgress(40);

		WebResponse response = new WebResponse();
		response.setInventoriesData(usageData);
		response.setInventoryData(inventoryData);
		response.setTotalData(totalData);
		return response;
	}

	private InventoryData calculatePeriodicReview(Product product, List<InventoryData> usageData) {
		int stock = 0;
		try {
			BigInteger totalIncomingFromSupplier = productFlowRepository
					.getSumOfProductFlowByTransactionType(TransactionType.TRANS_IN, product.getId());
			BigInteger totalDistributedToCustomer = productFlowRepository
					.getSumOfProductFlowByTransactionType(TransactionType.TRANS_OUT, product.getId());
			stock = totalIncomingFromSupplier.intValue() - totalDistributedToCustomer.intValue();
		} catch (Exception e) {
		}
		PeriodicReviewResult result = stockReviewer.periodicReview(stock, usageData, true);
		return InventoryData.builder().periodicReviewResult(result).build();
	}

	private List<InventoryData> getUsageData(List<ProductFlow> productFlows, Date from, Date to) {

		Map<String, InventoryData> mappedUsage = blankMappedUsage(from, to);

		for (ProductFlow productFlow : productFlows) {
			if (null == productFlow.getTransaction())
				continue;
			try {
				int month = productFlow.getTransactionMonth();
				int year = productFlow.getTransactionYear();
				String key = month + "-" + year;
				if (null == mappedUsage.get(key)) {
					mappedUsage.put(key, InventoryData.builder().month(month).year(year).build());
				}
				mappedUsage.get(key).addItems(productFlow.getCount());
			} catch (Exception e) {

			}
		}

		return MapUtil.mapValuesToList(mappedUsage);
	}

	private Map<String, InventoryData> blankMappedUsage(Date from, Date to) {
		int diffMonth = DateUtil.getDiffMonth(getCalendarMonth(from) + 1, getCalendarYear(from),
				getCalendarMonth(to) + 1, getCalendarYear(to));
		List<int[]> periods = DateUtil.getMonths(cal(to), diffMonth);
		Map<String, InventoryData> mappedUsage = new LinkedHashMap<>();
		for (int[] period : periods) {
			int month = period[1];
			int year = period[0];
			String key = month + "-" + year;
			mappedUsage.put(key, InventoryData.builder().month(month).year(year).build());

		}
		return mappedUsage;
	}

	public WebResponse getProductListWithUsage(WebRequest webRequest) {
		Filter filter = webRequest.getFilter();
		Date startDate = filter.getStartPeriodByYYMMDD();
		Date endDate = filter.getEndPeriodByYYMMDD();

		if (startDate.after(endDate)) {
			throw new ApplicationException(new Exception("invalid period"));
		}
		CommonFilterResult<Product> filtered = masterDataService.filterEntities(filter, Product.class);
		List<Product> products = filtered.getEntities();
		int totalData = filtered.getCount();
		progressService.sendProgress(30);

		List<ProductFlow> productFlows = productFlowRepository
				.getByTransactionTypeAndDateBetweenAndProducts(TransactionType.TRANS_OUT, startDate, endDate, products);
		progressService.sendProgress(20);

		List<ProductModel> mappedProduct = mapProductAndUsage(products, productFlows);
		progressService.sendProgress(50);

		WebResponse response = new WebResponse();
		response.setEntities(mappedProduct);
		response.setTotalData(totalData);
		return response;
	}

	private List<ProductModel> mapProductAndUsage(List<Product> products, List<ProductFlow> productFlows) {
		Map<Long, Long> mapped = new HashMap<>();
		List<ProductModel> models = new LinkedList<>();
		// initialize map
		for (Product product : products) {
			mapped.put(product.getId(), 0L);
		}
		// mapping usage count
		for (ProductFlow productFlow : productFlows) {
			try {
				long value = mapped.get(productFlow.getProduct().getId());
				value += productFlow.getCount();
				mapped.put(productFlow.getProduct().getId(), value);
			} catch (Exception e) {

			}
		}
		// populating models
		for (Product product : products) {
			Long usage = mapped.get(product.getId());
			ProductModel model = product.toModel();
			model.setCount(usage.intValue());
			models.add(model);
		}
		return models;
	}
}
