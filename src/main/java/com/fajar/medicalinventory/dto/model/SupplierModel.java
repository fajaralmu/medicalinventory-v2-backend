/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.dto.model;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.constants.FieldType;
import com.fajar.medicalinventory.entity.Supplier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 *
 * @author fajar
 */
@Dto(entityClass = Supplier.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class SupplierModel extends BaseModel<Supplier>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6919147802315112851L;
	 
	@FormField
    private String code; 
	@FormField
    private String name;
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
    private String address;
	@FormField
    private String contact; 

  
}
