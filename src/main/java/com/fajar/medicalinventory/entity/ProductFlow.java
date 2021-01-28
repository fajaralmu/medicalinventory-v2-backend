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
	@JoinColumn(name = "kodetransaksi")
	private Transaction transaksi;

	@ManyToOne
	@JoinColumn(name = "kodeobat")
	private Product drug;

	@Column
	private Date kadaluarsa;
	@Column
	private int jumlah;
	@Column
	private Integer kodestokobat;
	@Column
	@Default
	private boolean sesuai = true;
	@Column
	private long harga;
	@Column
	private boolean generik;

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
