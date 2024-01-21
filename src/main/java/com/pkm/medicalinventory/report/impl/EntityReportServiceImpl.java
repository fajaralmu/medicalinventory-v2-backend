package com.pkm.medicalinventory.report.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pkm.medicalinventory.dto.model.BaseModel;
import com.pkm.medicalinventory.entity.BaseEntity;
import com.pkm.medicalinventory.entity.setting.EntityProperty;
import com.pkm.medicalinventory.exception.ApplicationException;
import com.pkm.medicalinventory.report.EntityReportService;
import com.pkm.medicalinventory.report.WritableReport;
import com.pkm.medicalinventory.report.builder.EntityReportBuilder;
import com.pkm.medicalinventory.report.builder.ReportBuilder;
import com.pkm.medicalinventory.service.ProgressNotifier;
import com.pkm.medicalinventory.service.ProgressService;
import com.pkm.medicalinventory.util.EntityPropertyBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EntityReportServiceImpl implements EntityReportService {
	@Autowired
	private ProgressService progressService;

	private ProgressNotifier notifier() {

		return new ProgressNotifier() {

			@Override
			public void notify(int progress, int maxProgress, double percent) {
				progressService.sendProgress(progress, maxProgress, percent);

			}
		};
	}

	@Override
	public WritableReport getEntityReport(List<BaseModel> entities, Class<? extends BaseEntity> entityClass) {
		log.info("Generate entity report: {}", entityClass);

		try {
			Class modelClass = BaseEntity.getModelClass(entityClass);
	
			EntityProperty entityProperty = EntityPropertyBuilder.createEntityProperty(modelClass, null);
	//		ReportData reportData = ReportData.builder().entities(entities).entityProperty(entityProperty).requestId(requestId).build(); 
	
			String name = "Repor-"+entityClass.getSimpleName()+".xls";
			ReportBuilder reportBuilder = new EntityReportBuilder(entityProperty, entities, notifier(), name);
			
			progressService.sendProgress(1, 1, 10, false);
	
			WritableReport file = reportBuilder.build();
	
			log.info("Entity Report generated");
	
			return file;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}
}
