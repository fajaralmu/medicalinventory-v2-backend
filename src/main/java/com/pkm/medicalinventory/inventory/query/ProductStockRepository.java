package com.pkm.medicalinventory.inventory.query;

import java.math.BigInteger;

import org.springframework.lang.Nullable;

import com.pkm.medicalinventory.dto.Filter;
import com.pkm.medicalinventory.entity.ProductFlow;
import com.pkm.medicalinventory.management.impl.CommonFilterResult;

public interface ProductStockRepository {

	public CommonFilterResult<ProductFlow> filter(Filter filter);

	public BigInteger getTotalItemsWillExpireAtBranchWarehouse(long locationId, @Nullable Integer expDaysWithin);

	public BigInteger getTotalItemsAllLocation(@Nullable Integer expDaysWithin);

	public BigInteger getTotalItemsWillExpireAtMasterWarehouse(Integer expDaysWithin);

}
