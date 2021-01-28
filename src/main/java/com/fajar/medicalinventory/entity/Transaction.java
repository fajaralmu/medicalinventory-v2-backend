/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.fajar.medicalinventory.constants.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 
 * @author fajar
 */
@Component
@Entity
@Table(name = "transaction")  
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1055517081803635273L;
	@Column(unique = true)
	private String code;
	@Column
	private Date transactionDate;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column
	@Enumerated(EnumType.STRING)
	private TransactionType type;
	@ManyToOne 
	@JoinColumn(name = "supplier_id")
	@Nullable
	private Supplier supplier;
	@ManyToOne 
	@JoinColumn(name = "customer_id")
	@Nullable
	private Customer customer;
	@ManyToOne // dibutuhkan di stok obat
	@JoinColumn(name = "health_center_destination_id")
	@Nullable
	private HealthCenter healthCenterDestionation;
	
	@ManyToOne 
	@JoinColumn(name = "health_center_location_id")
	private HealthCenter healthCenter;
	 

	@Transient
	@Default
	private List<ProductFlow> listAliranObat = new ArrayList<>();
	@Transient
	@Default
	private transient  List<DrugStock> listStokObat = new ArrayList<>();

 

}
