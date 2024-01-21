package com.pkm.medicalinventory.inventory;

import javax.servlet.http.HttpServletRequest;

import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.WebResponse;

public interface ProductStatisticService {

	WebResponse getProductUsageByCode(String productCode, WebRequest webRequest);

	WebResponse getProductListWithUsage(WebRequest webRequest);

}
