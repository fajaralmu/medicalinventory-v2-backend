/*
 * To change sthis license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author fajar
 */
@Component
@Entity
@Table(name = "unit")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Unit extends BaseEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8298314953785695479L;
	 
	@Column(unique = true)
	private String name;
	@Column
	private String description;	
 

}
