package com.fajar.medicalinventory.statistics;

public class ItemTabelFrek {

	private int id, bawah, atas, frekuensi, midPoint, frek_MidPoint,
			midPointKuadrat, frek_midPointKuadrat;

	public ItemTabelFrek() {

	}

	public ItemTabelFrek(int id, int atas, int bawah, int frekuensi) {
		super();
		this.id = id;
		this.bawah = bawah;
		this.atas = atas;
		this.frekuensi = frekuensi;

	}

	public int getId() {
		return id;
	}

	public int getBawah() {
		return bawah;
	}

	public void setBawah(int bawah) {
		this.bawah = bawah;
	}

	public int getAtas() {
		return atas;
	}

	public void setAtas(int atas) {
		this.atas = atas;
	}

	public int getFrekuensi() {
		return frekuensi;
	}

	public void setFrekuensi(int frekuensi) {
		this.frekuensi = frekuensi;
	}

	public int getMidPoint() {
		return midPoint;
	}

	public void setMidPoint(int midPoint) {
		this.midPoint = midPoint;
	}

	public int getFrek_MidPoint() {
		return frek_MidPoint;
	}

	public void setFrek_MidPoint(int frek_MidPoint) {
		this.frek_MidPoint = frek_MidPoint;
	}

	public int getMidPointKuadrat() {
		return midPointKuadrat;
	}

	public void setMidPointKuadrat(int midPointKuadrat) {
		this.midPointKuadrat = midPointKuadrat;
	}

	public int getFrek_MidPointKuadrat() {
		return frek_midPointKuadrat;
	}

	public void setFrek_midPointKuadrat(int frek_midPointKuadrat) {
		this.frek_midPointKuadrat = frek_midPointKuadrat;
	}

	public String toString2() {
		return "ItemTabelFrek [bawah=" + bawah + ", atas=" + atas
				+ ", frekuensi=" + frekuensi + "]";
	}

	@Override
	public String toString() {
		return "ItemTabelFrek [id:" + id + ",bawah:" + bawah + ", atas:" + atas
				+ ", frekuensi:" + frekuensi + ", midPoint:" + midPoint
				+ ", frek_MidPoint:" + frek_MidPoint + ", midPointKuadrat:"
				+ midPointKuadrat + ", frek_midPointKuadrat:"
				+ frek_midPointKuadrat + "]";
	}

	public void setFrek(int f) {
		this.frekuensi = f;
	}

}
