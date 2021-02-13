/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.fajar.medicalinventory.annotation.CustomEntity;
import com.fajar.medicalinventory.dto.model.ProductFlowModel;
import com.fajar.medicalinventory.exception.ApplicationException;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author fajar
 */
@CustomEntity(ProductFlowModel.class)
//@Dto(updateService = "productFlowUpdateService", creatable= false, withProgressWhenUpdated = true)
@Component
@Entity
@Table(name = "product_flow")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFlow extends BaseEntity<ProductFlowModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8839593046741372229L;

//	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "transaction_id", nullable = false) 
	private Transaction transaction;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false) 
	private Product product;

	@Column(name="expired_date") 
	private Date expiredDate;
	@Column 
	private int count;
	@Column(name="used_count", nullable = false) 
	private int usedCount;
	
	@Nullable
	@ManyToOne
	@JoinColumn(name = "reference_flow_id")
	@Setter(value = AccessLevel.NONE) 
	private ProductFlow referenceProductFlow;

	@Column
	@Default 
	private boolean suitable = true;
	@Column 
	private long price;
	@Column 
	private boolean generic;  
	 
	
	public void addUsedCount(int count) {
		 
		if (getStock() - count < 0) {
			throw new ApplicationException("Stock not enough: "+(getStock() - count));
		}
		setUsedCount(getUsedCount()+count);
	}
	
	
	public int getStock() { 
		return count - usedCount;
	}

	public static int sumCount(List<ProductFlow> productFlows) {
		int sum = 0;
		for (ProductFlow productFlow : productFlows) {
			sum+=productFlow.getStock();
		}
		return sum;
	}

	public void resetUsedCount() {
		setUsedCount(0);
	}
	public boolean productsEquals(Product p) {
		if (product == null) return false;
		return product.idEquals(p);
	} 
	
	
	/**
	 * make the expDate same as referenceProductFlow.expDate
	 */
	public void setExpDate() {
		if (null == referenceProductFlow) {
			return;
		}
		setExpiredDate(referenceProductFlow.getExpiredDate());
		setGeneric(referenceProductFlow.isGeneric());
	}


	public void setReferenceProductFlow(ProductFlow referenceFlow) {
		if (null != referenceFlow && null != referenceFlow.getProduct()) {
			setProduct(referenceFlow.getProduct());
		}
		this.setGeneric(referenceFlow.isGeneric());
		this.referenceProductFlow = referenceFlow;
		this.setExpDate();
	}
	
	@JsonIgnore
	public Long getTransactionId() {
		if (null == transaction) return null;
		return transaction.getId();
	}
	/**
	 * distributed to customer/to branch warehouse
	 * @return
	 */
	@JsonIgnore
	public boolean isDistributed  () {
		return null != referenceProductFlow;
	}

 
}
