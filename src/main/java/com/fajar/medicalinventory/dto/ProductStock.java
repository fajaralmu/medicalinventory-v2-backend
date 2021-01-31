package com.fajar.medicalinventory.dto;

import java.io.Serializable;
import java.util.List;

import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.ProductFlow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ProductStock implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 8961378375766998898L;
	private Product product;
	private List<ProductFlow> productFlows;
	
}
