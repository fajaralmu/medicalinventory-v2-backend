package com.pkm.medicalinventory.report;

import java.util.List;

import com.pkm.medicalinventory.dto.model.BaseModel;
import com.pkm.medicalinventory.entity.BaseEntity;

public interface EntityReportService {

	WritableReport getEntityReport(List<BaseModel> entities, Class<? extends BaseEntity> entityClass);

}
