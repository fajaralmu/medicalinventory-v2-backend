/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.dto.model;

import java.util.Date;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.constants.FieldType;
import com.fajar.medicalinventory.constants.Gender;
import com.fajar.medicalinventory.entity.Customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 
 * @author fajar
 */
@Dto(entityClass = Customer.class)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerModel extends BaseModel<Customer>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2953923202077469683L;
	 
	@FormField
	private String code;
	@FormField(required = true)
	private String familyCode;
	@FormField
	private String name;
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String address;
	@FormField(type=FieldType.FIELD_TYPE_PLAIN_LIST)
	private Gender gender;
	@FormField(type=FieldType.FIELD_TYPE_DATE)
	private Date birthDate;
//	  
	 
}
