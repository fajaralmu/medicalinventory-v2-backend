/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pkm.medicalinventory.dto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pkm.medicalinventory.annotation.Dto;
import com.pkm.medicalinventory.annotation.FormField;
import com.pkm.medicalinventory.constants.FieldType;
import com.pkm.medicalinventory.entity.HealthCenter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 *
 * @author fajar
 */
@Dto(  value="Puskesmas")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthCenterModel extends BaseModel<HealthCenter>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2688885440038052615L;
	@FormField
	private String code;
	@FormField
	private String name;
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String address;
	@FormField
	private String contact;

	@JsonIgnore
	private Integer monthlyProductCount; 
}
