package com.pkm.medicalinventory.statistics;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tabelz implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4329849240459301059L; 
	private double z; 
	private double zero; 
	private double one; 
	private double two; 
	private double three; 
	private double four; 
	private double five; 
	private double six; 
	private double seven; 
	private double eight; 
	private double nine;
 

	public double get_Value(int column) {
		switch (column) {
		case 0:
			System.out.println(zero);
			return zero;
		case 1:
			return one;
		case 2:
			return two;
		case 3:
			return three;
		case 4:
			return four;
		case 5:
			return five;
		case 6:
			return six;
		case 7:
			return seven;
		case 8:
			return eight;
		case 9:
			return nine;
		default:
			return zero;

		}

	}

 

}
