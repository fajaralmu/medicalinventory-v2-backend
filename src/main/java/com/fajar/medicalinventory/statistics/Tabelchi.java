package com.fajar.medicalinventory.statistics;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Tabelchi implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2352164113699963729L;
	 
	private int derajatbebas; 
	private double sig90; 
	private double sig95; 
	private double sig97p5; 
	private double sig99; 
	private double sig99p5; 
 
	public double get_Val(double sig) {
		if (sig == 0.1)
			return sig90;
		else if (sig == 0.05)
			return sig95;
		else if (sig == 0.025)
			return sig97p5;
		else if (sig == 0.01)
			return sig99;
		else if (sig == 0.005)
			return sig99p5;
		else
			return sig90;
	}

}
