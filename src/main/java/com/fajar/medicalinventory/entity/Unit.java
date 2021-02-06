/*
 * To change sthis license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.medicalinventory.annotation.CustomEntity;
import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.constants.FieldType;
import com.fajar.medicalinventory.dto.model.UnitModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author fajar
 */
@CustomEntity(UnitModel.class)
@Entity
@Table(name = "unit")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Unit extends BaseEntity<UnitModel> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8298314953785695479L;

	@Column(unique = true)
	@FormField
	private String name;
	@Column
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String description;

}
