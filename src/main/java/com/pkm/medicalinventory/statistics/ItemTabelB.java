package com.pkm.medicalinventory.statistics;

import java.text.DecimalFormat;

import org.apache.commons.math3.distribution.NormalDistribution;

public class ItemTabelB {

	private ItemTabelFrek item_tabel_f;
	private double batasatas, batasbawah;
	private double ZAtas, ZBawah;
	private double luasInterval, frekuensiHarapan, mean;
	private double MEAN_CALC, STDEV;
	private int JUMLAHDATA;
	
	public ItemTabelB(ItemTabelFrek i) {
		this.item_tabel_f = i;

	}

	public void hitung() {
		setBatasatas(item_tabel_f.getAtas() + 0.5);
		setBatasbawah(item_tabel_f.getBawah() - 0.5);
		setZAtas((batasatas - MEAN_CALC) / STDEV);
		setZBawah((batasbawah - MEAN_CALC) / STDEV);
		
		setLuasInterval(Math.abs(nilaiAreaZ(Math.abs(ZBawah))
				- nilaiAreaZ(Math.abs(ZAtas))));
		setFrekuensiHarapan(luasInterval * JUMLAHDATA);
		setMean((Math.pow((item_tabel_f.getFrekuensi() - frekuensiHarapan), 2) / frekuensiHarapan));
	}

	public void setMEAN_CALC(double mEAN) {
		MEAN_CALC = mEAN;
	}

	public void setJUMLAHDATA(int jml) {
		JUMLAHDATA = jml;
	}

	public void setSTDEV(double sTDEV) {
		STDEV = sTDEV;
	}

	public ItemTabelFrek getItem_tabel_frek() {
		return item_tabel_f;
	}

	public void setItem_tabel_frek(ItemTabelFrek i) {
		this.item_tabel_f = i;
	}

	public double getBatasatas() {
		return batasatas;
	}

	public void setBatasatas(double batasatas) {
		this.batasatas = batasatas;
	}

	public double getBatasbawah() {
		return batasbawah;
	}

	public void setBatasbawah(double batasbawah) {
		this.batasbawah = batasbawah;
	}

	public double getZAtas() {
		return ZAtas;
	}

	public void setZAtas(Double zAtas) {
		String doubleStr = new DecimalFormat("#0.00").format(zAtas).replace(",",".");
		ZAtas = Double.parseDouble(doubleStr);
		;
	}

	public double getZBawah() {
		return ZBawah;
	}

	public void setZBawah(double zBawah) {
		// System.out.println("z bawah: "+zBawah);
		String doubleStr = new DecimalFormat("#0.00").format(zBawah).replace(",",".");
		ZBawah = Double.parseDouble(doubleStr);
		;
		;
	}

	public double getLuasInterval() {
		return luasInterval;
	}

	public void setLuasInterval(double luasInterval) {
		this.luasInterval = luasInterval;
	}

	public double getFrekuensiHarapan() {
		return frekuensiHarapan;
	}

	public void setFrekuensiHarapan(double frekuensiHarapan) {
		this.frekuensiHarapan = frekuensiHarapan;
	}

	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	@Override
	public String toString() {
		return "f:" + item_tabel_f.getFrekuensi() + ", Batas:"
				+ batasbawah + " - " + batasatas + ", Z:" + ZBawah
				+ " dan " + ZAtas + ", luasInterval:" + luasInterval
				+ ", frekuensiHarapan:" + frekuensiHarapan + ", mean:" + mean
				+ "]";
	}
	public Double nilaiAreaZ(Double in){
		NormalDistribution normS = new NormalDistribution(0, 1);
		return normS.cumulativeProbability(in)-0.5; //0.5 norm for 0.0
	}

}
