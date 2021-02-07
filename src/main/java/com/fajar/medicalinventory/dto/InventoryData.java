package com.fajar.medicalinventory.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class InventoryData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1083066691216486822L;
	private List<ProductInventory> inventories;
	private int totalItemsSum;
	private int  totalExpiredSum;
	private int totalWillExpiredSum;
	
	public void calculateSummary () {
		if (null == inventories) return;
		for (ProductInventory inventory : inventories) {
			totalItemsSum+=inventory.getTotalItems();
			totalWillExpiredSum+=inventory.getWillExpiredItems();
			totalExpiredSum+=inventory.getExpiredItems();
		}
	}
}
