package com.fajar.medicalinventory.service.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.entity.ProductFlow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public   class CommonFilterResult<T extends BaseEntity> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7627112916142073122L;
	List<T> entities;
	int count;
	public static <T extends BaseEntity> CommonFilterResult<T> listAndCount(List<T> list, Integer count) {
		 
		 CommonFilterResult<T> result = new  CommonFilterResult<>();
		 result.setEntities(list);
		 result.setCount(count);
		 return result;
	}
}