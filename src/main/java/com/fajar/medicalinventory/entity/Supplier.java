/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.medicalinventory.annotation.CustomEntity;
import com.fajar.medicalinventory.dto.model.SupplierModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 *
 * @author fajar
 */
@CustomEntity(SupplierModel.class)
@Entity
@Table(name="supplier")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Supplier extends BaseEntity<SupplierModel>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6919147802315112851L;
	 
	@Column(unique = true) 
    private String code;
	@Column  
    private String name;
	@Column 
    private String address;
	@Column  
    private String contact;

  
}
