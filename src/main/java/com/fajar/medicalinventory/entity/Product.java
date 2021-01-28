/*
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author fajar
 */
@Entity
@Table(name = "product")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4398862371443391887L;
	 
	@Column(unique = true)
	private String code;
	@Column 
	private String name;
	@Column 
	private String description;
	@ManyToOne 
	@JoinColumn(name = "unit_id")
	private Unit unit;
	@Column(name = "utility_tool")
	private boolean utilityTool;

	// web stuff

	// private String namasatuan;
	@Transient
	private Integer jmlobat;
	@Transient
	private double stokaman;
	@Transient
	private double nextorder;
	@Transient
	private Integer hargasatuan;
	@Transient
	private Integer kumulatifpakai;
	@Transient
	private String kelas;
	@Transient
	private Integer pemakaian;
	@Transient
	private String keterangan;

 

}
