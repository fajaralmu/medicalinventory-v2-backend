package com.fajar.medicalinventory.service.transaction;

import static com.fajar.medicalinventory.util.DateUtil.getCalendarMonth;
import static com.fajar.medicalinventory.util.DateUtil.getCalendarYear;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.dto.InventoryData;
import com.fajar.medicalinventory.dto.WebRequest;
import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.exception.ApplicationException;
import com.fajar.medicalinventory.exception.DataNotFoundException;
import com.fajar.medicalinventory.repository.ProductFlowRepository;
import com.fajar.medicalinventory.repository.ProductRepository;
import com.fajar.medicalinventory.service.ProgressService;
import com.fajar.medicalinventory.util.DateUtil;
import com.fajar.medicalinventory.util.MapUtil;
import com.fajar.medicalinventory.util.PeriodUtil;

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

	public WebResponse getProductUsage(String productCode, WebRequest webRequest, HttpServletRequest httpRequest) {

		Product product = productRepository.findTop1ByCode(productCode);
		if (null == product) {
			throw new DataNotFoundException("Product with code: " + productCode + " not found");
		}

		Date startDate = PeriodUtil.getStartPeriod(webRequest.getFilter());
		Date endDate = PeriodUtil.getEndPeriod(webRequest.getFilter());
		
		if (startDate.after(endDate)) {
			throw new ApplicationException("invalid period");
		}
		
		log.info("startDate : {}, endDate: {}", startDate, endDate);
		List<ProductFlow> productFlows = productFlowRepository.getByTransactionTypeAndDateBetween(productCode,
				TransactionType.TRANS_OUT, startDate, endDate);
		List<InventoryData> usageData = getUsageData(productFlows, startDate, endDate);
		int totalData = ProductFlow.sumQtyCount(productFlows);

		WebResponse response = new WebResponse();
		response.setInventoriesData(usageData);
		response.setTotalData(totalData);
		return response;
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
		int diffMonth = DateUtil.getDiffMonth(getCalendarMonth(from) + 1, 
				getCalendarYear(from),
				getCalendarMonth(to) + 1, 
				getCalendarYear(to));
		List<int[]> periods = DateUtil.getMonths(DateUtil.cal(to), diffMonth);
		Map<String, InventoryData> mappedUsage = new HashMap<>();
		for (int[] period : periods) {
			int month = period[1];
			int year = period[0];
			String key = month + "-" + year;
			mappedUsage.put(key, InventoryData.builder().month(month).year(year).build());
			 
		}
		return mappedUsage;
	}
}
