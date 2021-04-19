package com.fajar.medicalinventory.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.domain.PageRequest;

import com.fajar.medicalinventory.constants.FilterFlag;
import com.fajar.medicalinventory.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class Filter implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -5151185528546046666L;
	@Builder.Default
	private Integer limit = 0;
	@Builder.Default
	private Integer page = 0;
	private String orderType;
	private String orderBy;
	@Builder.Default
	private boolean exacts = false;
	@Builder.Default
	private Integer day = 1;
	@Builder.Default
	private Integer dayTo = 1;
	@Builder.Default
	private Integer year = 0;
	@Builder.Default // starts at 1
	@Getter(value = AccessLevel.NONE)
	private Integer month = 1;
	@Builder.Default
	private Map<String, Object> fieldsFilter = new HashMap<>();

	@Getter(value = AccessLevel.NONE)
	private Integer monthTo;
	private Integer yearTo;

	private boolean ignoreEmptyValue;
	private boolean filterExpDate;

	@Default
	private FilterFlag flag = FilterFlag.DEFAULT;

	@JsonIgnore
	private int maxValue;

	/**
	 * starts at 1
	 * 
	 * @return
	 */
	public Integer getMonth() {
		return month;
	}

	/**
	 * starts at 1
	 * 
	 * @return
	 */
	public Integer getMonthTo() {
		return monthTo;
	}

	@JsonIgnore
	public Date getStartPeriodByYYMMDD() {

		Integer startMonth = getMonth();
		Integer startYear = getYear();
		Date startDate = DateUtil.getDate(startYear, startMonth - 1, getDay());

		return DateUtil.clock00Midnight(startDate);
	}

	@JsonIgnore
	public Date getEndPeriodByYYMM() {

		Integer endMonth = getMonthTo();
		Integer endYear = getYearTo();
		Date endDate = DateUtil.getEndPeriod(endMonth, endYear);

		return DateUtil.clock24Midnight(endDate);
	}

	@JsonIgnore
	public Date getStartPeriodByYYMM() {
		Filter filter = this;
		Integer startMonth = filter.getMonth();
		Integer startYear = filter.getYear();
		Date startDate = DateUtil.getStartPeriod(startMonth - 1, startYear);
		return DateUtil.clock00Midnight(startDate);
	}

	@JsonIgnore
	public Date getEndPeriodByYYMMDD() {
		Filter filter = this;
		Integer endMonth = filter.getMonthTo();
		Integer endYear = filter.getYearTo();
		Date endDate = DateUtil.getDate(endYear, endMonth - 1, filter.getDayTo());
		return DateUtil.clock24Midnight(endDate);
	}

	@JsonIgnore
	public PageRequest getPageRequest() {
		final PageRequest pageable = PageRequest.of(getPage(), getLimit());
		return pageable;
	}
	@JsonIgnore
	public boolean isAllFlag() {
		return FilterFlag.ALL.equals(getFlag());
	}
	public Object getFieldsFilterValue(String key) {
		return getFieldsFilterSavely().get(key);
	}

	@JsonIgnore
	public Map<String, Object> getFieldsFilterSavely() {
		if (null == fieldsFilter) {
			return new LinkedHashMap<String, Object>();
		}
		return fieldsFilter;
	}
}
