/*
 * To change sthis license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pkm.medicalinventory.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.pkm.medicalinventory.annotation.CustomEntity;
import com.pkm.medicalinventory.dto.model.UnitModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author fajar
 */
@CustomEntity
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
	private String name;
	@Column 
	private String description;

}
