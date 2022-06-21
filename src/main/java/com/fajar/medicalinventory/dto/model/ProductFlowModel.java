/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.dto.model;

import java.util.Date;
import java.util.List;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.constants.FieldType;
import com.fajar.medicalinventory.constants.Filterable;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
@Dto(value="Transaction Item", updateService = "productFlowUpdateService", creatable= false, withProgressWhenUpdated = true)
@JsonInclude(value=Include.NON_NULL)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFlowModel extends BaseModel<ProductFlow> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8839593046741372229L;

 

	@FormField(optionItemName = "name", editable = false)
	private ProductModel product;

	@FormField(optionItemName = "code", editable = false)
	 
	private TransactionModel transaction; 
	@FormField(labelName = "Trans. Type", optionItemName = "type", entityField="transaction", editable = false) 
	private TransactionModel transaction2;
	
	@FormField(type=FieldType.FIELD_TYPE_DATE)
	private Date expiredDate;
	@FormField(type=FieldType.FIELD_TYPE_NUMBER)
	private int count;
	@FormField(type=FieldType.FIELD_TYPE_NUMBER)
	private int usedCount;
	@FormField(type=FieldType.FIELD_TYPE_NUMBER, filterable = Filterable.UNFILTERABLE_ORDERABLE)
	private int stock;
	
	@Setter(value = AccessLevel.NONE)
	@FormField(optionItemName = "id", editable = false) 
	private ProductFlowModel referenceProductFlow;
//	@FormField
//	private Long refStockId;

	@Default
	@FormField(type=FieldType.FIELD_TYPE_CHECKBOX)
	private boolean suitable = true;
	@FormField(type=FieldType.FIELD_TYPE_NUMBER)
	private double price;
	@FormField(type=FieldType.FIELD_TYPE_CHECKBOX)
	private boolean generic;  
	@FormField
	private String batchNum;
	private String stockLocation;
	
	private List<ProductFlowModel> referencingItems;
	 
 	public String getType() {
 		try {
 			return transaction.getType().toString();
 		} catch (Exception e) {
 			return null;
		}
	}
	public int getStock() { 
		return count - usedCount;
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


	public void setReferenceProductFlow(ProductFlowModel referenceFlow) {
		if (null != referenceFlow && null != referenceFlow.getProduct()) {
			setProduct(referenceFlow.getProduct());
		}
		if (referenceFlow != null) {
			this.setGeneric(referenceFlow.isGeneric());
		}
		this.referenceProductFlow = referenceFlow;
		this.setExpDate();
	} 
}
