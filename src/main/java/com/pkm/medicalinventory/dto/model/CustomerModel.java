/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pkm.medicalinventory.dto.model;

import static com.pkm.medicalinventory.util.DateUtil.cal;

import java.util.Calendar;
import java.util.Date;

import com.pkm.medicalinventory.annotation.Dto;
import com.pkm.medicalinventory.annotation.FormField;
import com.pkm.medicalinventory.constants.FieldType;
import com.pkm.medicalinventory.constants.Filterable;
import com.pkm.medicalinventory.constants.Gender;
import com.pkm.medicalinventory.entity.Customer;

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
@Dto( value = "Pelanggan")
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
	@FormField(required = false)
	private String familyCode;
	@FormField
	private String name;
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, required = false)
	private String address;
	@FormField(type=FieldType.FIELD_TYPE_PLAIN_LIST)
	private Gender gender;
	@FormField(type=FieldType.FIELD_TYPE_DATE)
	private Date birthDate;
	@FormField(editable = false, filterable = Filterable.DISABLE_ALL)
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
