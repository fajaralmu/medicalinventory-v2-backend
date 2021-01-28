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

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
 *
 * @author fajar
 */
@Component
@Entity
@Table(name = "health_center")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthCenter extends BaseEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2688885440038052615L;
	@Column(unique = true)
	private String code;
	@Column
	private String name;
	@Column
	private String address;
	
	
	@Transient
	private Integer monthlyProductCount;
 
 
}
