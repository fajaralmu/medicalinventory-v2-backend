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
import javax.persistence.Transient;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.constants.FieldType;
import com.fajar.medicalinventory.exception.ApplicationException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author fajar
 */
@Dto(updateService = "productFlowUpdateService", creatable= false, withProgressWhenUpdated = true)
@Component
@Entity
@Table(name = "product_flow")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFlow extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8839593046741372229L;

//	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "transaction_id", nullable = false)
	@FormField(optionItemName = "code", editable = false) 
	private Transaction transaction;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	@FormField(optionItemName = "name", editable = false)
	private Product product;

	@Column(name="expired_date")
	@FormField(type=FieldType.FIELD_TYPE_DATE)
	private Date expiredDate;
	@Column
	@FormField(type=FieldType.FIELD_TYPE_NUMBER)
	private int count;
	@Column(name="used_count", nullable = false)
	@FormField(type=FieldType.FIELD_TYPE_NUMBER)
	private int usedCount;
	
	@Nullable
	@ManyToOne
	@JoinColumn(name = "reference_flow_id")
	@Setter(value = AccessLevel.NONE)
	@FormField(optionItemName = "id", editable = false) 
	private ProductFlow referenceProductFlow;

	@Column
	@Default
	@FormField(type=FieldType.FIELD_TYPE_CHECKBOX)
	private boolean suitable = true;
	@Column
	@FormField(type=FieldType.FIELD_TYPE_NUMBER)
	private long price;
	@Column
	@FormField(type=FieldType.FIELD_TYPE_CHECKBOX)
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
	}


	public void setReferenceProductFlow(ProductFlow referenceFlow) {
		if (null != referenceFlow && null != referenceFlow.getProduct()) {
			setProduct(referenceFlow.getProduct());
		}
		this.setGeneric(referenceFlow.isGeneric());
		this.referenceProductFlow = referenceFlow;
		this.setExpDate();
	}

 
}
