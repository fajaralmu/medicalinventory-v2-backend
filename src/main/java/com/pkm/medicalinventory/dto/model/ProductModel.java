/*
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pkm.medicalinventory.dto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pkm.medicalinventory.annotation.Dto;
import com.pkm.medicalinventory.annotation.FormField;
import com.pkm.medicalinventory.constants.FieldType;
import com.pkm.medicalinventory.entity.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author fajar
 */
@Dto( value= "Produk")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class ProductModel extends BaseModel<Product>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4398862371443391887L;

	 
	@FormField
	private String code;
	@FormField
	private String name;
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, required = false)
	private String description;
	@FormField(type = FieldType.FIELD_TYPE_FIXED_LIST, optionItemName = "name")
	private UnitModel unit;
	@FormField(type = FieldType.FIELD_TYPE_CHECKBOX)
	private boolean utilityTool;

	// web stuff

	@FormField(multipleImage = true, type = FieldType.FIELD_TYPE_IMAGE)
	private String imageNames;

//	@JsonIgnore
	private Integer count;
	@JsonIgnore
	private Double price;
	
	
	public static void main(String[] args) {
		ProductModel p = ProductModel.builder().name("TEST").build();
		p.setUnit(UnitModel.builder().name("UNIT NAME").build());
		
		System.out.println(p.toEntity());
	}

}
