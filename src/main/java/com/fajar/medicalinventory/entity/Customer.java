/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.fajar.medicalinventory.annotation.CustomEntity;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.constants.FieldType;
import com.fajar.medicalinventory.constants.Gender;
import com.fajar.medicalinventory.dto.model.CustomerModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 
 * @author fajar
 */
@CustomEntity(CustomerModel.class)
@Entity
@Table(name = "customer")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends BaseEntity<CustomerModel> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2953923202077469683L;
	 
	@Column(unique = true)
	@FormField
	private String code;
	@Column(name="family_code")
	@FormField(required = true)
	private String familyCode;
	@Column
	@FormField
	private String name;
	@Column
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String address;
	@Column
	@Enumerated(EnumType.STRING)
	@FormField(type=FieldType.FIELD_TYPE_PLAIN_LIST)
	private Gender gender;
	@Column(name="date_of_birth")
	@FormField(type=FieldType.FIELD_TYPE_DATE)
	private Date birthDate;
//	 
//	@ManyToOne
//	@JoinColumn(name = "health_center_id")
//	@FormField(type = FieldType.FIELD_TYPE_FIXED_LIST, optionItemName = "name")
//	private HealthCenter healthCenter;
	   
	
 
}
