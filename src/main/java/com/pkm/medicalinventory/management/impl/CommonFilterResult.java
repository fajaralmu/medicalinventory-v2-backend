package com.pkm.medicalinventory.management.impl;

import java.io.Serializable;
import java.util.List;

import com.pkm.medicalinventory.entity.BaseEntity;

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
	private List<T> entities;
	private int count;
	public static <T extends BaseEntity> CommonFilterResult<T> listAndCount(List<T> list, Integer count) {
		 
		 CommonFilterResult<T> result = new  CommonFilterResult<>();
		 result.setEntities(list);
		 result.setCount(count);
		 return result;
	}
}
