package com.fajar.medicalinventory.report;

public class DrugConsumption {

    private Integer count;
    private String drugId;
    private Integer day;

   
    public DrugConsumption(Integer hari,Integer jumlah, String kodeObat) {
        this.day = hari;
        this.count = jumlah;
        this.drugId = kodeObat;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer hari) {
        this.day = hari;
    }
        
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer jumlah) {
        this.count = jumlah;
    }

    public String getKodeObat() {
        return drugId;
    }

    public void setKodeObat(String kodeObat) {
        this.drugId = kodeObat;
    }

}
