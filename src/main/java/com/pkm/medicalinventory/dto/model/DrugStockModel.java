/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pkm.medicalinventory.dto.model;

import java.io.Serializable;

import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.entity.ProductFlow;
import com.pkm.medicalinventory.entity.Transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author fajar
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DrugStockModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4549400312671526707L;
	private Integer id;
	private Product product;
	private Transaction transaction;
	// private Date kadaluarsa;
	private int count;
	private ProductFlow productFlow;
	private int incomingCount ;
	private int disributedCount;

	// UNTUK KEPERLUAN VIEW
	// private String kodegudang;
	// private String namaobat, namasatuan;
	// private Date kadaluarsa, tgltransaksi;
	private int expStatus;
 
}
