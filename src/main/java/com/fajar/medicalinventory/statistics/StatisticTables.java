package com.fajar.medicalinventory.statistics;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class StatisticTables {

	Map<Double, Tabelz> zTabels = new HashMap<Double, Tabelz>();
	Map<Integer, Tabelchi> chiTabels = new HashMap<>();
	Map<Integer, Tabelkolmogorov> kolmogorovTables = new HashMap<>();

	// table z props : z;nol;satu;dua;tiga;empat;lima;enam;tujuh;delapan;sembilan"
	// table chi props: derajatbebas;sig90;sig95;sig97p5;sig99;sig99p5
	// table kolmogorov props: n;sig90;sig95;sig98;sig99
	@PostConstruct
	public void init() {
		setZTable();
		setChiTable();
		setKolomogorovTable();
	}
	
	public Map<Double, Tabelz> getzTabels() {
		return zTabels;
	}
	public Map<Integer, Tabelchi> getChiTabels() {
		return chiTabels;
	}
	public Map<Integer, Tabelkolmogorov> getKolmogorovTables() {
		return kolmogorovTables;
	}
	Double db(String val) {
		return Double.valueOf(val);
	}

	private void setKolomogorovTable() {
		kolmogorovTables.clear();
		String raw = TableKolmogorovRawData.data;
		String[] rawSplitted = raw.split("\r\n");
		for (int i = 0; i < rawSplitted.length; i++) {
			String[] properties = rawSplitted[i].split(";");
			Integer n = Integer.valueOf(properties[0]);
			Tabelkolmogorov chiProp = Tabelkolmogorov.builder().n(n).sig90(db(properties[1])).sig95(db(properties[2]))
					.sig98(db(properties[3])).sig99(db(properties[4])).build();

			kolmogorovTables.put(n, chiProp);
		}

	}

	private void setChiTable() {
		chiTabels.clear();
		String raw = TableChiRawData.data;
		String[] rawSplitted = raw.split("\r\n");
		for (int i = 0; i < rawSplitted.length; i++) {
			String[] properties = rawSplitted[i].split(";");
			Integer degree = Integer.valueOf(properties[0]);
			Tabelchi chiProp = Tabelchi.builder().degree(degree).sig90(db(properties[1])).sig95(db(properties[2]))
					.sig97p5(db(properties[3])).sig99(db(properties[4])).sig99p5(db(properties[5])).build();

			chiTabels.put(degree, chiProp);
		}

	}

	private void setZTable() {
		zTabels.clear();
		String raw = TableZRawData.data;
		String[] rawSplitted = raw.split("\r\n");
		for (int i = 0; i < rawSplitted.length; i++) {
			String[] properties = rawSplitted[i].split(";");
			Double z = Double.valueOf(properties[0]);
			Tabelz zProp = Tabelz.builder().z(z).zero(db(properties[1])).one(db(properties[2])).two(db(properties[3]))
					.three(db(properties[4])).four(db(properties[5])).five(db(properties[6])).six(db(properties[7]))
					.seven(db(properties[8])).eight(db(properties[9])).nine(db(properties[10])).build();
			zTabels.put(z, zProp);
			
		}
	}

}
