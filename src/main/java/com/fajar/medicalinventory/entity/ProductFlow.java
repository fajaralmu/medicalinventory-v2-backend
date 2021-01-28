/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.stereotype.Component;

import com.fajar.medicalinventory.annotation.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author fajar
 */
@Dto
@Component
@Entity
@Table(name = "product_flow")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFlow extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8839593046741372229L;

	@ManyToOne
	@JoinColumn(name = "transaction_id")
	private Transaction transaction;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@Column(name="expired_date")
	private Date expiredDate;
	@Column
	private int count;
	@ManyToOne
	@JoinColumn(name = "reference_flow_id")
	private ProductFlow referenceProductFlow;

	@Column
	@Default
	private boolean suitable = true;
	@Column
	private long price;
	@Column
	private boolean generic;

	@Transient
	Integer hargaPerItem;
	@Transient
	private Integer hargatotal = 0;
	@Transient
	private Integer jumlahobatLama = 0;

	// WEB STUFF
	@Transient
	private String namaobat;
	@Transient
	private boolean terdaftar;
	@Transient
	private boolean sudah_diedit;

}
