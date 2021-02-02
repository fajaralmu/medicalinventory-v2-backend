package com.fajar.medicalinventory.report;

import java.util.ArrayList;
import java.util.List;

public class DailyConsumption {

    private Integer count;
    private Integer day;
    public List<DrugConsumption> drugConsumptions  = new ArrayList<>();;

    public DailyConsumption() { 
    }

    public DailyConsumption(Integer jumlah, Integer hari) {
        this.count = jumlah;
        this.day = hari; 
    }
    
    public List<DrugConsumption> getDrugConsumptions(){
        return this.drugConsumptions;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer jumlah) {
        this.count = jumlah;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer hari) {
        this.day = hari;
    }

	public void addDrugConsumption(DrugConsumption ko) {
		 
		drugConsumptions.add(ko);
	}

}

