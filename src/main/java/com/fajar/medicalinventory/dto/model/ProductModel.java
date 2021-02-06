/*
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.dto.model;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.constants.FieldType;
import com.fajar.medicalinventory.entity.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author fajar
 */
@Dto(entityClass = Product.class)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel extends BaseModel<Product>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4398862371443391887L;

	 
	@FormField
	private String code;
	@FormField
	private String name;
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String description;
	@FormField(type = FieldType.FIELD_TYPE_DYNAMIC_LIST, optionItemName = "name")
	private UnitModel unit;
	@FormField(type = FieldType.FIELD_TYPE_CHECKBOX)
	private boolean utilityTool;

	// web stuff

	@FormField(multipleImage = true, type = FieldType.FIELD_TYPE_IMAGE)
	private String imageNames;

	@JsonIgnore
	private Integer count;
	@JsonIgnore
	private Integer price;
	
	
	public static void main(String[] args) {
		ProductModel p = ProductModel.builder().name("TEST").build();
		p.setUnit(UnitModel.builder().name("UNIT NAME").build());
		
		System.out.println(p.toEntity());
	}

}
