package com.fajar.medicalinventory.statistics;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Tabelkolmogorov implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6105861932496503729L; 
	private int n; 
	private double sig90; 
	private double sig95; 
	private double sig98; 
	private double sig99; 
	
	public double get_Val(double sig) {
		if (sig == 0.1)
			return sig90;
		else if (sig == 0.05)
			return sig95;
		else if (sig == 0.02)
			return sig98;
		else if (sig == 0.01)
			return sig99;
		else
			return sig90;
	}

}
