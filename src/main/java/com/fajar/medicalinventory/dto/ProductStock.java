package com.fajar.medicalinventory.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.dto.model.ProductFlowModel;
import com.fajar.medicalinventory.dto.model.ProductModel;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Data 
@NoArgsConstructor 
@JsonInclude(value = Include.NON_NULL)
public class ProductStock implements Serializable {
	
	
/**
	 * 
	 */
	private static final long serialVersionUID = 8961378375766998898L;
	private ProductModel product;
	private List<ProductFlowModel> productFlows;
	private Integer totalIncomingCount = 0;
	private Integer totalUsedCount = 0;
	private Integer totalStock = 0;
	private Integer previousStock = 0;
	
	 
	public ProductStock(ProductModel model, int prevStock, int totalIncoming, int totalUsed, int totalStock) {
		this.product = model;
		this.totalIncomingCount = totalIncoming;
		this.totalUsedCount = totalUsed;
		this.totalStock = totalStock;
		this.previousStock = prevStock;
	}
	public ProductStock(ProductModel model, List<ProductFlow> stockRecords) {
		this.product = model;
		this.productFlows = new ArrayList<>();
		if (null != stockRecords) {
			stockRecords.forEach(p->{
				productFlows.add(p.toModel());
			});
		}
		productFlows.forEach(p->{
			try {
				if (TransactionType.TRANS_IN.equals(p.getTransaction().getType())) {
					p.setStockLocation(p.getTransaction().getHealthCenterLocation().getName());
				} else {
					p.setStockLocation(p.getTransaction().getHealthCenterDestination().getName());
				}
				p.setTransaction(null);
			} catch(Exception e) {}
		});
		populateUsageSummary();
	}

	private void populateUsageSummary() {
		totalIncomingCount = 0;
		totalUsedCount=  0;
		if (null == productFlows) {
			return;
		}
		for (ProductFlowModel flow : productFlows) {
			totalIncomingCount += flow.getCount();
			totalUsedCount  += flow.getUsedCount();
			
		}
		totalStock = totalIncomingCount - totalUsedCount;
	}
}
