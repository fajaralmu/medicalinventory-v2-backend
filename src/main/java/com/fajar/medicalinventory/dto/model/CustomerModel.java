/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.dto.model;

import static com.fajar.medicalinventory.util.DateUtil.cal;

import java.util.Calendar;
import java.util.Date;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.constants.FieldType;
import com.fajar.medicalinventory.constants.Gender;
import com.fajar.medicalinventory.entity.Customer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
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
	@FormField(editable = false, filterable = false)
	@Getter(value=AccessLevel.NONE)
	private int age;
	
	
	public int getAge() {
		int age = getDiffYears(birthDate, new Date());
		if (age < 0) {
			age = 0;
		}
		return age;
	}
//	  
	public static int getDiffYears(Date first, Date last) {
	    Calendar a = cal(first);
	    Calendar b = cal(last);
	    int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
	    if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) || 
	        (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
	        diff--;
	    }
	    return diff;
	}
	 
}
