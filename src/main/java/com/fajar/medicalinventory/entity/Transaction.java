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
import org.springframework.stereotype.Component;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.constants.FieldType;
import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.util.StringUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 
 * @author fajar
 */
@Dto(editable = false)
@Entity
@Table(name = "transaction")  
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1055517081803635273L;
	@Column(unique = true)
	@FormField
	private String code;
	@Column
	@FormField(type=FieldType.FIELD_TYPE_DATE)
	private Date transactionDate;
	
	
	@Column
	@FormField
	@Enumerated(EnumType.STRING)
	private TransactionType type;
	@ManyToOne
	@FormField(type=FieldType.FIELD_TYPE_FIXED_LIST,optionItemName = "name")
	@JoinColumn(name = "user_id")
	private User user;
	@ManyToOne 
	@FormField(type=FieldType.FIELD_TYPE_FIXED_LIST,optionItemName = "name")
	@JoinColumn(name = "supplier_id")
	@Nullable
	private Supplier supplier;
	@ManyToOne 
	@FormField(type=FieldType.FIELD_TYPE_FIXED_LIST,optionItemName = "name")
	@JoinColumn(name = "customer_id")
	@Nullable
	private Customer customer;
	@ManyToOne // dibutuhkan di stok obat
	@JoinColumn(name = "health_center_destination_id")
	@FormField(type=FieldType.FIELD_TYPE_FIXED_LIST,optionItemName = "name")
	@Nullable
	private HealthCenter healthCenterDestionation; 
	@ManyToOne 
	@JoinColumn(name = "health_center_location_id")
	@FormField(type=FieldType.FIELD_TYPE_FIXED_LIST, optionItemName = "name")
	private HealthCenter healthCenter;
	 

	@Transient
	@Default
	private List<ProductFlow> productFlows = new ArrayList<>(); 
	
	public void generateUniqueCode() {
		this.code = StringUtil.generateRandomNumber(10);
	}

 

}
