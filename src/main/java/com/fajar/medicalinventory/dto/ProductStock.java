package com.fajar.medicalinventory.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.dto.model.ProductFlowModel;
import com.fajar.medicalinventory.dto.model.ProductModel;
import com.fajar.medicalinventory.entity.ProductFlow;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
public class ProductStock implements Serializable {
	
	
/**
	 * 
	 */
	private static final long serialVersionUID = 8961378375766998898L;
	private ProductModel product;
	private List<ProductFlowModel> productFlows;
	public ProductStock(ProductModel model, List<ProductFlow> productFlows2) {
		this.product = model;
		this.productFlows = new ArrayList<>();
		if (null != productFlows2) {
			productFlows2.forEach(p->{
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
	}
}
