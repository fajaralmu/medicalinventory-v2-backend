/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.constants.FieldType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 *
 * @author fajar
 */
@Dto
@Entity
@Table(name = "health_center")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthCenter extends BaseEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2688885440038052615L;
	@Column(unique = true)
	@FormField
	private String code;
	@Column(unique = true)
	@FormField
	private String name;
	@Column
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String address;

	@Transient
	@JsonIgnore
	private Integer monthlyProductCount;

}
