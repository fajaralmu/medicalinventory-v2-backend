/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pkm.medicalinventory.dto.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pkm.medicalinventory.annotation.Dto;
import com.pkm.medicalinventory.annotation.FormField;
import com.pkm.medicalinventory.constants.FieldType;
import com.pkm.medicalinventory.constants.Filterable;
import com.pkm.medicalinventory.constants.TransactionType;
import com.pkm.medicalinventory.entity.Transaction;

import lombok.AccessLevel;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 */
@JsonInclude(value = Include.NON_NULL)
@Dto(
	deletable = false,
	editable = true,
	creatable = false,
	managementService = "transactionManagementService",
	value = "Transaksi"
)
@Data
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
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, required = false)
	private String description;
	
	@FormField(editable = false)
	private TransactionType type;
	@FormField(optionItemName = "displayName", editable = false)
	@Getter(value = AccessLevel.NONE)
	private UserModel user;
	@FormField(type = FieldType.FIELD_TYPE_DYNAMIC_LIST, optionItemName = "name", required = false)
	private SupplierModel supplier;
	@FormField(type = FieldType.FIELD_TYPE_DYNAMIC_LIST, optionItemName = "name", required = false)
	private CustomerModel customer;
	@FormField(type = FieldType.FIELD_TYPE_DYNAMIC_LIST, optionItemName = "name", required = false)
	private HealthCenterModel healthCenterDestination;

	/**
	 * health center where transaction is performed
	 */
	@FormField(type = FieldType.FIELD_TYPE_DYNAMIC_LIST, optionItemName = "name", editable = false)
	private HealthCenterModel healthCenterLocation;

	///// Read Only Fields //////
	
	@FormField(editable = false, filterable = Filterable.DISABLE_ALL)
	@Getter(value = AccessLevel.NONE)
	private Integer productCount;
	@FormField(editable = false, filterable = Filterable.DISABLE_ALL)
	@Getter(value = AccessLevel.NONE)
	private Integer productVariant;

	private List<ProductFlowModel> productFlows = new ArrayList<>();

	public TransactionModel() {}
	
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

	public Integer getProductCount() {
		if (null == productFlows)
			return null;
		int count = 0;
		for (ProductFlowModel productFlow : productFlows) {
			count += productFlow.getCount();
		}
		return count;
	}
	
	public Integer getProductVariant() {
		if (null == productFlows) {
			return null;
		}
		return productFlows.size();
	}

	public UserModel getUser() {
		if (null == user) return null;
		user.setPassword(null);
		user.setAuthorities(null);
		return user;
	}

}
