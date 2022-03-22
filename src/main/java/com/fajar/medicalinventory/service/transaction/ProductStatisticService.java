package com.fajar.medicalinventory.service.transaction;

import static com.fajar.medicalinventory.util.DateUtil.cal;
import static com.fajar.medicalinventory.util.DateUtil.getCalendarMonth;
import static com.fajar.medicalinventory.util.DateUtil.getCalendarYear;

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

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.dto.Filter;
import com.fajar.medicalinventory.dto.InventoryData;
import com.fajar.medicalinventory.dto.PeriodicReviewResult;
import com.fajar.medicalinventory.dto.WebRequest;
import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.dto.model.ProductModel;
import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.exception.ApplicationException;
import com.fajar.medicalinventory.exception.DataNotFoundException;
import com.fajar.medicalinventory.repository.ProductFlowRepository;
import com.fajar.medicalinventory.repository.ProductRepository;
import com.fajar.medicalinventory.service.ProgressService;
import com.fajar.medicalinventory.service.entity.CommonFilterResult;
import com.fajar.medicalinventory.service.entity.MasterDataService;
import com.fajar.medicalinventory.util.DateUtil;
import com.fajar.medicalinventory.util.MapUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductStatisticService {

	@Autowired
	private ProgressService progressService;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductFlowRepository productFlowRepository;
	@Autowired
	private CalculationUtil calculationUtil;
	@Autowired
	private MasterDataService masterDataService;

	public WebResponse getProductUsageByCode(String productCode, WebRequest webRequest,
			HttpServletRequest httpServletRequest) {

		Product product = productRepository.findTop1ByCode(productCode);
		if (null == product) {
			throw new DataNotFoundException("Product with code: " + productCode + " not found");
		}

		Date startDate = webRequest.getFilter().getStartPeriodByYYMM();
		Date endDate = webRequest.getFilter().getEndPeriodByYYMM();
		progressService.sendProgress(30, httpServletRequest);

		if (startDate.after(endDate)) {
			throw new ApplicationException(new Exception("invalid period"));
		}

		log.info("startDate : {}, endDate: {}", startDate, endDate);
		List<ProductFlow> productFlows = productFlowRepository.getByTransactionTypeAndDateBetween(productCode,
				TransactionType.TRANS_OUT, startDate, endDate);
		List<InventoryData> usageData = getUsageData(productFlows, startDate, endDate);
		progressService.sendProgress(30, httpServletRequest);

		int totalData = ProductFlow.sumQtyCount(productFlows);
		InventoryData inventoryData = calculatePeriodicReview(product, usageData);
		progressService.sendProgress(40, httpServletRequest);

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
		PeriodicReviewResult result = calculationUtil.periodicReview(stock, usageData, true);
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

	public static void main(String[] args) {
		List<int[]> periods = DateUtil.getMonths(cal(DateUtil.getDate(2019, 8, 10)), 10);
		for (int[] is : periods) {
			System.out.println(is[0] + "-" + is[1]);
		}

	}

	public WebResponse getProductListWithUsage(WebRequest webRequest, HttpServletRequest httpServletRequest) {
		Filter filter = webRequest.getFilter();
		Date startDate = filter.getStartPeriodByYYMMDD();
		Date endDate = filter.getEndPeriodByYYMMDD();

		if (startDate.after(endDate)) {
			throw new ApplicationException(new Exception("invalid period"));
		}
		CommonFilterResult<Product> filtered = masterDataService.filterEntities(filter, Product.class);
		List<Product> products = filtered.getEntities(); 
		int totalData = filtered.getCount();
		progressService.sendProgress(30, httpServletRequest);

		List<ProductFlow> productFlows = productFlowRepository
				.getByTransactionTypeAndDateBetweenAndProducts(TransactionType.TRANS_OUT, startDate, endDate, products);
		progressService.sendProgress(20, httpServletRequest);

		List<ProductModel> mappedProduct = mapProductAndUsage(products, productFlows);
		progressService.sendProgress(50, httpServletRequest);

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
