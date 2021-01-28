/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.constants.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 *
 * @author fajar
 */
@Dto
@Entity
@Table(name="supplier")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Supplier extends BaseEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6919147802315112851L;
	 
	@Column(unique = true)
	@FormField
    private String code;
	@Column 
	@FormField
    private String name;
	@Column
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
    private String address;
	@Column 
	@FormField
    private String contact;

  
}
