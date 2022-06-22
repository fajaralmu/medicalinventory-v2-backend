/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pkm.medicalinventory.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pkm.medicalinventory.annotation.CustomEntity;
import com.pkm.medicalinventory.dto.model.HealthCenterModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 *
 * @author fajar
 */
@CustomEntity 
@Entity
@Table(name = "health_center")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthCenter extends BaseEntity<HealthCenterModel> implements Serializable {
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 2688885440038052615L;
	
	@Column(unique = true)
	private String code;
	@Column(unique = true)
	private String name;
	@Column
	private String address;
	@Column
	private String contact;
	
	@Transient
	@JsonIgnore
	private Integer monthlyProductCount;

}
