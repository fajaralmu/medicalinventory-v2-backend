/*
 * To change sthis license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pkm.medicalinventory.dto.model;

import com.pkm.medicalinventory.annotation.Dto;
import com.pkm.medicalinventory.annotation.FormField;
import com.pkm.medicalinventory.constants.FieldType;
import com.pkm.medicalinventory.entity.Unit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author fajar
 */
@Dto
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnitModel extends BaseModel<Unit> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8298314953785695479L;

	@FormField
	private String name;
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, required = false)
	private String description; 

}
