package com.fajar.medicalinventory.statistics;

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
	private double nol; 
	private double satu; 
	private double dua; 
	private double tiga; 
	private double empat; 
	private double lima; 
	private double enam; 
	private double tujuh; 
	private double delapan; 
	private double sembilan;
 

	public double get_Value(int column) {
		switch (column) {
		case 0:
			System.out.println(nol);
			return nol;
		case 1:
			return satu;
		case 2:
			return dua;
		case 3:
			return tiga;
		case 4:
			return empat;
		case 5:
			return lima;
		case 6:
			return enam;
		case 7:
			return tujuh;
		case 8:
			return delapan;
		case 9:
			return sembilan;
		default:
			return nol;

		}

	}

	@Override
	public String toString() {
		return "Ztabel [Z=" + z + ", nol=" + nol + ", satu=" + satu + ", dua=" + dua + ", tiga=" + tiga + ", empat="
				+ empat + ", lima=" + lima + ", enam=" + enam + ", tujuh=" + tujuh + ", delapan=" + delapan
				+ ", sembilan=" + sembilan + "]";
	}

}
