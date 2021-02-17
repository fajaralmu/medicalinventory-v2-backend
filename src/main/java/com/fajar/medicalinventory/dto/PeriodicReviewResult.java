package com.fajar.medicalinventory.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class PeriodicReviewResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8457898277676660361L;
	private Double safetyStock;
	private Double targetStockLevel;
	private Double orderSize;
	
	private String description;
}
