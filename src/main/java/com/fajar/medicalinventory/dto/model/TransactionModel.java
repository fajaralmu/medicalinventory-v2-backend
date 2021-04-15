/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.dto.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.constants.FieldType;
import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.entity.Transaction;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 */
@JsonInclude(value = Include.NON_NULL)
@Dto(deletable = false, editable = true, creatable = false, updateService = "transactionUpdateService", value = "Transaksi")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class TransactionModel extends BaseModel<Transaction> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1055517081803635273L;
	@FormField(editable = false)
	private String code;
	@FormField(type = FieldType.FIELD_TYPE_DATETIME)
	@Default
	private Date transactionDate = new Date();
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String description;
	@FormField(editable = false, filterable = false)
	@Getter(value = AccessLevel.NONE)
	private int productCount;

	@FormField(editable = false)
	private TransactionType type;
	@FormField(optionItemName = "displayName", editable = false)
	@Getter(value = AccessLevel.NONE)
	private UserModel user;
	@FormField(type = FieldType.FIELD_TYPE_DYNAMIC_LIST, optionItemName = "name", editable = false)
	private SupplierModel supplier;
	@FormField(type = FieldType.FIELD_TYPE_DYNAMIC_LIST, optionItemName = "name", editable = false)
	private CustomerModel customer;
	@FormField(type = FieldType.FIELD_TYPE_DYNAMIC_LIST, optionItemName = "name", editable = false)
	private HealthCenterModel healthCenterDestination;

	/**
	 * health center where transaction is performed
	 */
	@FormField(type = FieldType.FIELD_TYPE_DYNAMIC_LIST, optionItemName = "name", editable = false)
	private HealthCenterModel healthCenterLocation;

	@Default
	private List<ProductFlowModel> productFlows = new ArrayList<>();

	@Override
	public Transaction toEntity() {
		Transaction entity = new Transaction();
		productFlows.forEach(p -> {
			entity.addProductFlow(p.toEntity());
		});
		return copy(entity, "productFlows");
	}

	public void addProductFlow(ProductFlowModel productFlow) {
		productFlows.add(productFlow);
	}

	public int getProductCount() {
		if (null == productFlows)
			return 0;
		int count = 0;
		for (ProductFlowModel productFlow : productFlows) {
			count += productFlow.getCount();
		}
		return count;
	}

	public UserModel getUser() {
		user.setPassword(null);
		user.setAuthorities(null);
		return user;
	}

}
