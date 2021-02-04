/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.lang.Nullable;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.constants.FieldType;
import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.exception.ApplicationException;
import com.fajar.medicalinventory.util.DateUtil;
import com.fajar.medicalinventory.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 */
@JsonInclude(value = Include.NON_NULL)
@Dto(editable = false)
@Entity
@Table(name = "transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class Transaction extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1055517081803635273L;
	@Column(unique = true)
	@FormField
	private String code;
	@Column
	@FormField(type = FieldType.FIELD_TYPE_DATE)
	@Default
	private Date transactionDate = new Date();
	@Column(columnDefinition = "TEXT")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String description;

	@Column
	@FormField
	@Enumerated(EnumType.STRING)
	private TransactionType type;
	@ManyToOne
	@FormField(type = FieldType.FIELD_TYPE_FIXED_LIST, optionItemName = "name")
	@JoinColumn(name = "user_id")
	private User user;
	@ManyToOne
	@FormField(type = FieldType.FIELD_TYPE_FIXED_LIST, optionItemName = "name")
	@JoinColumn(name = "supplier_id")
	@Nullable
	private Supplier supplier;
	@ManyToOne
	@FormField(type = FieldType.FIELD_TYPE_FIXED_LIST, optionItemName = "name")
	@JoinColumn(name = "customer_id")
	@Nullable
	private Customer customer;
	@ManyToOne // dibutuhkan di stok obat
	@JoinColumn(name = "health_center_destination_id")
	@FormField(type = FieldType.FIELD_TYPE_FIXED_LIST, optionItemName = "name")
	@Nullable
	private HealthCenter healthCenterDestination;
	
	/**
	 * health center where transaction is performed
	 */
	@ManyToOne
	@JoinColumn(name = "health_center_location_id")
	@FormField(type = FieldType.FIELD_TYPE_FIXED_LIST, optionItemName = "name")
	private HealthCenter healthCenterLocation;

	@Transient
	@Default
	private List<ProductFlow> productFlows = new ArrayList<>();

	private void generateUniqueCode(TransactionType type) {
		int year = DateUtil.getCalendarYear(transactionDate);
		int month = DateUtil.getCalendarMonth(transactionDate);
		int day = DateUtil.getCalendarDayOfMonth(transactionDate);
		String dateCode = year + StringUtil.twoDigits(month+1) + StringUtil.twoDigits(day);
		this.code = dateCode + type.ordinal()+"-" + StringUtil.generateRandomNumber(6);
	}

	/**
	 * determine type 
	 */
	public void setTypeAndCode() {
		TransactionType type;
		if (supplier != null) {
			type = TransactionType.TRANS_IN;
		} else if (customer != null) {
			type = TransactionType.TRANS_OUT;
		} else if (healthCenterDestination != null) {
			type = TransactionType.TRANS_OUT_TO_WAREHOUSE;
		} else {
			throw new ApplicationException("Missing transaction data!");
		}
		log.info("Transaction type: {}", type);
		setType(type);
		generateUniqueCode(type);
		
	}
	
	public int getTotalProductFlowCount() {
		if (null == productFlows) return 0;
		int count = 0;
		for (ProductFlow productFlow : productFlows) {
			count += productFlow.getCount();
		}
		return count;
	}

	public void addProductFlow(ProductFlow productFlow) {
		productFlows.add(productFlow);
	}
	
	public int getProductCount(Product product) {
		if (null == productFlows) return 0;
		int count = 0;
		for (ProductFlow productFlow : productFlows) {
			if (product.idEquals(productFlow.getProduct())) {
				count += productFlow.getCount();
			}
		}
		return count;
	}

	public void setProductFlowsTransactionNull() {
		if (null == productFlows) return;
		productFlows.forEach(p->p.setTransaction(null));
	}
	
	 

}
