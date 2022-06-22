package com.pkm.medicalinventory.statistics;

public class itemTabelKumulatif {

	private int id, dataPoint, datayangsama;
	private double kumulatifNormal, diff, kumulatifEmpiris, mean, stdev, Z;

	public itemTabelKumulatif(int id, int dp) {
		this.id = id;
		this.dataPoint = dp;
	}

	public void hitung() {
		setZ((dataPoint - mean) / stdev);
	}

	public double getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getDataPoint() {
		return dataPoint;
	}

	public void setDataPoint(int dataPoint) {
		this.dataPoint = dataPoint;
	}

	public double getZ() {
		return Z;
	}

	public void setZ(double z) {
		Z = z;
	}

	public double getKumulatifNormal() {
		return kumulatifNormal;
	}

	public void setKumulatifNormal(double kumulatifNormal) {
		this.kumulatifNormal = kumulatifNormal;
	}

	public double getKumulatifEmpiris() {
		return kumulatifEmpiris;
	}

	public void setKumulatifEmpiris(double kumulatifEmpiris) {
		this.kumulatifEmpiris = kumulatifEmpiris;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	public void setStdev(double std) {
		this.stdev = std;
	}

	public void setDataSama(int s) {
		this.datayangsama = s;
	}

	public int getDataSama() {
		return this.datayangsama;
	}

	public void setDiffNormEmp(double d) {
		this.diff = d;
	}

	public double getDiff() {
		return this.diff;
	}

	@Override
	public String toString() {
		return "itemTabelKumulatif [id=" + id + ", dataPoint=" + dataPoint
				+ ", Z=" + Z + ", kumulatifNormal=" + kumulatifNormal
				+ ", kumulatifEmpiris=" + kumulatifEmpiris + "diff=" + diff
				+ "]";
	}

}
