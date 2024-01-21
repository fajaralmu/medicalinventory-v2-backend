package com.pkm.medicalinventory.inventory;

import java.util.List;

import com.pkm.medicalinventory.dto.InventoryData;
import com.pkm.medicalinventory.dto.PeriodicReviewResult;

public interface StockReviewer {

	PeriodicReviewResult periodicReview(int stock, List<InventoryData> usageData, boolean normalest);

}
