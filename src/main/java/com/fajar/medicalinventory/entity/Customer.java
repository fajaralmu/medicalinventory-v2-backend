/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 
 * @author fajar
 */
@Entity
@Table(name = "customer")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends BaseEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2953923202077469683L;
	 
	@Column
	private String name;
	@Column
	private String address;
	@Column
	private String gender;
	@Column
	private Date birthDate;
	 
	@ManyToOne
	@JoinColumn(name = "health_center_id")
	private HealthCenter healthCenter;
	   
 
}
