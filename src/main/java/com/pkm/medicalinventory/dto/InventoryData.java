package com.pkm.medicalinventory.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value=Include.NON_NULL)
public class InventoryData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1083066691216486822L;
	private List<ProductInventory> inventories;
	private Integer totalItemsSum;
	private Integer totalExpiredSum;
	private Integer totalWillExpiredSum;
	
	private Integer month, year;
	
	private PeriodicReviewResult periodicReviewResult;
	
	public void checkNulls() {
		if (null == totalItemsSum) totalItemsSum = 0;
		if (null == totalWillExpiredSum) totalWillExpiredSum = 0;
		if (null == totalExpiredSum) totalExpiredSum = 0;
	}
	
	public void calculateInventoryStatusSummary () {
		if (null == inventories) return;
		checkNulls();
		for (ProductInventory inventory : inventories) {
			totalItemsSum+=inventory.getTotalItems();
			totalWillExpiredSum+=inventory.getWillExpiredItems();
			totalExpiredSum+=inventory.getExpiredItems();
		}
	}
	
	/**
	 * add to totalItemsSum
	 * @param item
	 */
	public void addItems(int item) {
		if (null == getTotalItemsSum()) {
			setTotalItemsSum(0);
		}
		setTotalItemsSum( getTotalItemsSum()+item);
	}
}
