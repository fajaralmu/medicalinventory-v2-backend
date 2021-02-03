package com.fajar.medicalinventory.report;

public class ProductRequestSheetGenerator {
	
	

	
	public String strMonth(Integer bulan, Integer tahun) {
		String tgl = "";
		switch (bulan) {
		case 1:
			tgl = "Januari";
			break;
		case 2:
			tgl = "Februari";
			break;
		case 3:
			tgl = "Maret";
			break;
		case 4:
			tgl = "April";
			break;
		case 5:
			tgl = "Mei";
			break;
		case 6:
			tgl = "Juni";
			break;
		case 7:
			tgl = "Juli";
			break;
		case 8:
			tgl = "Agustus";
			break;
		case 9:
			tgl = "September";
			break;
		case 10:
			tgl = "Oktotber";
			break;
		case 11:
			tgl = "Nopember";
			break;
		case 12:
			tgl = "Desembar";
			break;
		case 13:
		default:
			tgl = "Januari";
			tahun++;
			break;

		}
		tgl += " " + tahun.toString();
		return tgl;
	}

}
