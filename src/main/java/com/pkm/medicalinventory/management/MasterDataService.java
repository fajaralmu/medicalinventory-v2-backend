package com.pkm.medicalinventory.management;

import com.pkm.medicalinventory.dto.Filter;
import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.dto.model.BaseModel;
import com.pkm.medicalinventory.entity.BaseEntity;
import com.pkm.medicalinventory.entity.setting.EntityProperty;
import com.pkm.medicalinventory.management.impl.CommonFilterResult;

public interface MasterDataService {

	BaseModel saveEntity(WebRequest request, boolean newRecord);
	WebResponse filter(WebRequest request);
	BaseModel delete(WebRequest request);
	EntityProperty getConfig(WebRequest request);
	<T extends BaseEntity> CommonFilterResult<T> filterEntities(Filter filter, Class<T> class1);

}
