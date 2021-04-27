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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;


/**
 *
 * @author fajar
 */
@Dto( value="Pemasok")
@Data
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
	
	public SupplierModel() {
		
	}

	public static void main(String[] args) throws JsonProcessingException {
		ObjectMapper m = new ObjectMapper();
		Supplier  model = new Supplier();
		model.setName("NAME");
		model.setNulledFields(null);
		String val = m.writeValueAsString(model.toModel());
		System.out.println(val);
	}
  
}
