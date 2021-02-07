/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.dto.model;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.constants.FieldType;
import com.fajar.medicalinventory.entity.HealthCenter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 *
 * @author fajar
 */
@Dto(entityClass = HealthCenter.class)
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

	@JsonIgnore
	private Integer monthlyProductCount; 
}