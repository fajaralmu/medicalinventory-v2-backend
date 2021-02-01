/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.exception.ApplicationException;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
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

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "transaction_id", nullable = false)
	private Transaction transaction;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(name="expired_date")
	private Date expiredDate;
	@Column
	private int count;
	@Column(name="used_count", nullable = false)
	@Getter(value = AccessLevel.NONE)
	private int usedCount;
	
	@Nullable
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
	
	public Integer getUsedCount() {
		 
		return usedCount;
	}
	
	public void addUsedCount(int count) {
		 
		if (getStock() - count < 0) {
			throw new ApplicationException("Stock not enough: "+(getStock() - count));
		}
		setUsedCount(getUsedCount()+count);
	}
	
	
	public int getStock() {
	 
		return count - usedCount;
	}

	public static int sumCount(List<ProductFlow> productFlows) {
		int sum = 0;
		for (ProductFlow productFlow : productFlows) {
			sum+=productFlow.getStock();
		}
		return sum;
	}
	
	// transients //

//	@Transient
//	private int hargaPerItem;
//	@Transient
//	private int hargatotal;
//	@Transient
//	private int jumlahobatLama;
//
//	// WEB STUFF
//	@Transient
//	private String namaobat;
//	@Transient
//	private boolean terdaftar;
//	@Transient
//	private boolean sudah_diedit;

}
