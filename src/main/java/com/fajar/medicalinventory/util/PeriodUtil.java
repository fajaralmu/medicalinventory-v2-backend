package com.fajar.medicalinventory.util;

import java.util.Date;

import com.fajar.medicalinventory.dto.Filter;

public class PeriodUtil {

	public static Date getStartPeriod(Filter filter) {

		Integer startMonth = filter.getMonth();
		Integer startYear = filter.getYear();
		Date startDate = DateUtil.getStartPeriod(startMonth - 1, startYear);
		return startDate;
	}

	public static Date getEndPeriod(Filter filter) {

		Integer endMonth = filter.getMonthTo();
		Integer endYear = filter.getYearTo();
		Date endDate = DateUtil.getStartPeriod(endMonth - 1, endYear);
		return endDate;
	}
}
