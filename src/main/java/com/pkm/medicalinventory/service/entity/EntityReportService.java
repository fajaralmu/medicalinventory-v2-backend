package com.pkm.medicalinventory.service.entity;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.pkm.medicalinventory.dto.model.BaseModel;
import com.pkm.medicalinventory.entity.BaseEntity;
import com.pkm.medicalinventory.entity.User;
import com.pkm.medicalinventory.entity.setting.EntityProperty;
import com.pkm.medicalinventory.service.ProgressService;
import com.pkm.medicalinventory.service.SessionValidationService;
import com.pkm.medicalinventory.service.report.CustomWorkbook;
import com.pkm.medicalinventory.service.report.EntityReportBuilder;
import com.pkm.medicalinventory.service.report.ProgressNotifier;
import com.pkm.medicalinventory.util.EntityPropertyBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityReportService {
 
	@Autowired
	private ProgressService progressService;
	@Autowired
	private SessionValidationService sessionValidationService;

	public CustomWorkbook getEntityReport(List<? extends BaseModel> entities, Class<? extends BaseEntity> entityClass,
			HttpServletRequest httpRequest) throws Exception {
		log.info("Generate entity report: {}", entityClass); 
		User currentUser = sessionValidationService.getLoggedUser(httpRequest);
		String requestId = currentUser.getRequestId();
		Class modelClass = BaseEntity.getModelClass(entityClass);
		
		EntityProperty entityProperty = EntityPropertyBuilder.createEntityProperty(modelClass, null);
//		ReportData reportData = ReportData.builder().entities(entities).entityProperty(entityProperty).requestId(requestId).build(); 
	
		EntityReportBuilder reportBuilder = new EntityReportBuilder( entityProperty, entities, requestId);
		reportBuilder.setProgressNotifier(notifier(httpRequest));
		
		progressService.sendProgress(1, 1, 10, false, httpRequest);

		CustomWorkbook file = reportBuilder.buildReport(); 
		
		log.info("Entity Report generated");

		return file;
	}
	
	private ProgressNotifier notifier(final HttpServletRequest httpServletRequest) {

		return new ProgressNotifier() {

			@Override
			public void notify(int progress, int maxProgress, double percent) {
				progressService.sendProgress(progress, maxProgress, percent, httpServletRequest);

			}
		};
	}

}
