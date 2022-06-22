package com.pkm.medicalinventory.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pkm.medicalinventory.dto.model.HealthCenterModel;

import lombok.Data;

@Data
public class ProductInventory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -852148837705757089L;
	private HealthCenterModel location;
	private int totalItems;
	private int expiredItems;
	private int willExpiredItems;
	
	public static List<ProductInventory> combine(List<ProductInventory> totalList,
			List<ProductInventory> willExpiredList, List<ProductInventory> expiredList) {
		for (int i = 0; i < totalList.size(); i++) {
			try {
				totalList.get(i).setWillExpiredItems(willExpiredList.get(i).totalItems);
				totalList.get(i).setExpiredItems(expiredList.get(i).totalItems);
			} catch (Exception e) {
				 
			}
		}
		return totalList;
	}
	
	 

}
